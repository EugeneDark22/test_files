package com.example.demo.service;

import com.example.demo.model.FileEntity;
import com.example.demo.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;

@Service
public class FileUploadService {

    private static final String DIRECTORY_PATH = "C://Users//korop//OneDrive//Desktop//files";

    @Autowired
    private FileRepository fileRepository;

    public void saveFile(MultipartFile file) throws IOException {
        File directory = new File(DIRECTORY_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File saveFile = new File(directory, file.getOriginalFilename());
        file.transferTo(saveFile);

        FileEntity fileEntity = new FileEntity();
        fileEntity.setName(saveFile.getName());
        fileEntity.setPath(saveFile.getAbsolutePath());
        fileEntity.setSize(saveFile.length());
        fileEntity.setExtension(getFileExtension(saveFile));
        fileEntity.setHash(getFileHash(saveFile));
        fileEntity.setCreationDate(getFileCreationDate(saveFile));

        fileRepository.save(fileEntity);
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
