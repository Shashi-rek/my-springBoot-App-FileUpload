package com.example.my_spring_boot_app.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.my_spring_boot_app.model.FileEntity;
import com.example.my_spring_boot_app.model.Student;
import com.example.my_spring_boot_app.repository.FileRepository;
import com.example.my_spring_boot_app.repository.StudentRepository;

@Service
public class FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    @Autowired
    private FileRepository fileRepository;  // Repository to store file data

    @Autowired
    private StudentRepository studentRepository;


    public void processFile(MultipartFile multipartFile) throws IOException {
        // 1. Save the file locally (optional)
        String localFilePath = "D:\\FILES\\tmp\\uploads\\" + multipartFile.getOriginalFilename();
        File localFile = new File(localFilePath);
        localFile.getParentFile().mkdirs();
        multipartFile.transferTo(localFile);

        logger.info("File saved locally at: {}", localFilePath);

        // 2. Process the Word document to extract student data
        List<Student> students = extractStudentsFromWord(localFile);

        logger.info("Extracted {} students from the file.", students.size());

        // 3. Save extracted student data to the database
        studentRepository.saveAll(students);

        logger.info("Student data saved to the database.");

        // 4. Save file information to the database
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(multipartFile.getOriginalFilename());
        fileEntity.setUploadedAt(new Date());
        fileRepository.save(fileEntity);

        logger.info("File information saved to the database.");

        // 5. Move the file to the processed folder
        String processedFilePath = "D:/FILES/tmp/processed/" + multipartFile.getOriginalFilename();
        File processedFile = new File(processedFilePath);
        processedFile.getParentFile().mkdirs();

        if (localFile.renameTo(processedFile)) {
            logger.info("File successfully moved to processed folder at: {}", processedFilePath);
        } else {
            logger.error("Failed to move the file to the processed folder.");
            throw new IOException("Failed to move the file to the processed folder");
        }
    }

    private List<Student> extractStudentsFromWord(File file) throws IOException {
        List<Student> students = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis)) {

            // Iterate through all tables in the document
            for (XWPFTable table : document.getTables()) {
                List<XWPFTableRow> rows = table.getRows();

                // Assuming first row is header
                for (int i = 1; i < rows.size(); i++) {
                    XWPFTableRow row = rows.get(i);
                    List<XWPFTableCell> cells = row.getTableCells();

                    if (cells.size() >= 2) {
                        String name = cells.get(0).getText().trim();
                        String marksStr = cells.get(1).getText().trim();
                        Integer marks = null;

                        try {
                            marks = Integer.parseInt(marksStr);
                        } catch (NumberFormatException e) {
                            // Handle parsing error, e.g., log and skip this row
                            logger.error("Invalid marks for student: {}", name);
                            continue;
                        }

                        Student student = new Student(name, marks);
                        students.add(student);
                    }
                }
            }
        }

        return students;
    }
}
