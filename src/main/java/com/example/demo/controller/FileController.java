package com.example.demo.controller;

import com.example.demo.model.FileEntity;
import com.example.demo.service.FileSearchService;
import com.example.demo.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    private final FileService fileService;

    private final FileSearchService fileSearchService;

    public FileController(FileService fileService, FileSearchService fileSearchService) {
        this.fileService = fileService;
        this.fileSearchService = fileSearchService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<FileEntity> getAllFiles() {
        logger.info("Fetching all files");
        return fileService.getAllFiles();
    }


    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<FileEntity> searchFiles(@RequestParam("keyword") String keyword) throws IOException {
        logger.info("Searching for files containing keyword: {}", keyword);
        List<File> files = fileSearchService.searchFilesByContent(keyword);
        return files.stream()
                .map(file -> {
                    FileEntity entity = new FileEntity();
                    entity.setName(file.getName());
                    entity.setPath(file.getAbsolutePath());
                    entity.setSize(file.length());
                    entity.setExtension(file.getName().substring(file.getName().lastIndexOf('.') + 1));
                    entity.setCreationDate(file.lastModified());
                    return entity;
                })
                .collect(Collectors.toList());
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
