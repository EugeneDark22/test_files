package com.example.demo.service;

import com.example.demo.model.FileEntity;
import com.example.demo.repository.FileRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class FileService {

    private static final String DIRECTORY_PATH  = "/app/files";

    @Autowired
    private FileRepository fileRepository;

    public List<FileEntity> getAllFiles() {
        return fileRepository.findAll();
    }

    public void saveFile(MultipartFile file) throws IOException {
        Path filePath = Paths.get(DIRECTORY_PATH, file.getOriginalFilename());
        Files.write(filePath, file.getBytes());

        FileEntity fileEntity = new FileEntity();
        fileEntity.setName(file.getOriginalFilename());
        fileEntity.setSize(file.getSize());
        fileEntity.setExtension(getFileExtension(file.getOriginalFilename()));
        fileEntity.setHash(DigestUtils.sha256Hex(file.getInputStream()));
        fileEntity.setCreationDate(Files.getLastModifiedTime(filePath).toMillis());

        fileRepository.save(fileEntity);
    }

    public void deleteFile(Long id) {
        fileRepository.deleteById(id);
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }
}
