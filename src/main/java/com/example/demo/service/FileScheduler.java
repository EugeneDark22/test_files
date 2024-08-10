package com.example.demo.service;

import com.example.demo.model.FileEntity;
import com.example.demo.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@Service
public class FileScheduler {

    @Autowired
    private FileRepository fileRepository;

    private static final String DIRECTORY_PATH = "C://Users//korop//OneDrive//Desktop//files";

    @Scheduled(fixedRate = 1800000) // Запуск кожні 30 хвилин (1800000 мілісекунд)
    public void updateFileDatabase() {
        File directory = new File(DIRECTORY_PATH);
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                try {
                    updateOrSaveFile(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateOrSaveFile(File file) throws IOException {
        List<FileEntity> existingFiles = fileRepository.findByName(file.getName());

        if (!existingFiles.isEmpty()) {
            for (FileEntity fileEntity : existingFiles) {
                fileEntity.setSize(file.length());
                fileEntity.setHash(getFileHash(file));
                fileEntity.setCreationDate(getFileCreationDate(file));
                fileRepository.save(fileEntity);
            }
        } else {
            FileEntity newFile = new FileEntity();
            newFile.setName(file.getName());
            newFile.setPath(file.getAbsolutePath());
            newFile.setSize(file.length());
            newFile.setExtension(getFileExtension(file));
            newFile.setHash(getFileHash(file));
            newFile.setCreationDate(getFileCreationDate(file));
            fileRepository.save(newFile);
        }
    }


    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        return lastIndexOf == -1 ? "" : name.substring(lastIndexOf + 1);
    }

    private String getFileHash(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            return org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
        }
    }

    private String getFileCreationDate(File file) throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(attrs.creationTime().toMillis());
    }
}
