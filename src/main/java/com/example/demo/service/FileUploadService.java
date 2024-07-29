package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class FileUploadService {

    private static final String DIRECTORY_PATH = "C://Users//korop//OneDrive//Desktop//files";

    public void saveFile(MultipartFile file) throws IOException {
        File directory = new File(DIRECTORY_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File saveFile = new File(directory, file.getOriginalFilename());
        file.transferTo(saveFile);
    }
}
