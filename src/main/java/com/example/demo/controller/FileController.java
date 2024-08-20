package com.example.demo.controller;

import com.example.demo.model.FileEntity;
import com.example.demo.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<FileEntity> getAllFiles() {
        logger.info("Fetching all files");
        return fileService.getAllFiles();
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public void uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        logger.info("Admin access granted for file upload.");
        logger.info("Received file upload request for file: {}", file.getOriginalFilename());
        try {
            fileService.saveFile(file);
            logger.info("File {} uploaded successfully", file.getOriginalFilename());
        } catch (IOException e) {
            logger.error("Error uploading file: {}", file.getOriginalFilename(), e);
            throw e;
        }
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteFile(@PathVariable Long id) {
        logger.info("Received request to delete file with ID: {}", id);
        fileService.deleteFile(id);
        logger.info("File with ID {} deleted successfully", id);
    }
}
