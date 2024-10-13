package com.example.my_spring_boot_app.controller;

import com.example.my_spring_boot_app.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    private final FileService fileService;

    @Autowired
    public FileUploadController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * Handles file upload requests.
     *
     * @param file The multipart file to upload.
     * @return ResponseEntity with success or error message.
     */
    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        logger.info("Received file upload request: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            logger.warn("Empty file received.");
            return ResponseEntity.badRequest().body("Please select a file to upload.");
        }

        try {
            fileService.processFile(file);
            logger.info("File processed successfully: {}", file.getOriginalFilename());
            return ResponseEntity.ok("File uploaded and processed successfully.");
        } catch (IOException e) {
            logger.error("Error processing file: {}", file.getOriginalFilename(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Failed to upload and process the file.");
        }
    }

    /**
     * Optional: Endpoint to check if the service is running.
     *
     * @return Simple status message.
     */
    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("FileUploadController is up and running.");
    }
}
