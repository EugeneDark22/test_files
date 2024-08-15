package com.example.demo.controller;

import com.example.demo.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private FileRepository fileRepository;

    @DeleteMapping("/files/{id}")
    public void deleteFile(@PathVariable Long id) {
        fileRepository.deleteById(id);
    }
}
