package com.example.demo.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import com.example.demo.model.FileInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FileService {

    private static final String DIRECTORY_PATH = "C://Users//korop//OneDrive//Desktop//files";

    public List<FileInfo> getFiles() throws IOException {
        File directory = new File(DIRECTORY_PATH);
        File[] files = directory.listFiles();
        List<FileInfo> fileInfoList = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setName(file.getName());
                fileInfo.setSize(file.length());
                fileInfo.setExtension(getFileExtension(file));
                fileInfo.setHash(getFileHash(file));
                fileInfo.setCreationDate(getFileCreationDate(file));
                fileInfoList.add(fileInfo);
            }
        }

        return fileInfoList;
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        return lastIndexOf == -1 ? "" : name.substring(lastIndexOf + 1);
    }

    private String getFileHash(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            return DigestUtils.md5Hex(fis);
        }
    }

    private Date getFileCreationDate(File file) throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        return new Date(attrs.creationTime().toMillis());
    }
}
