package com.example.demo.controller;

import com.example.demo.model.FileInfo;
import com.example.demo.service.FileService;
import com.example.demo.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private FileUploadService fileUploadService;

    @GetMapping
    public List<FileInfo> getFiles() throws IOException {
        return fileService.getFiles();
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            fileUploadService.saveFile(file);
            return "File uploaded successfully!";
        } catch (IOException e) {
            return "Failed to upload file: " + e.getMessage();
        }
    }
}
