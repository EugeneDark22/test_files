package com.example.demo.service;

import com.example.demo.model.FileInfo;
import com.example.demo.repository.FileRepository;
import com.example.demo.model.FileEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    public List<FileInfo> getFiles() {
        return fileRepository.findAll().stream().map(this::mapToFileInfo).collect(Collectors.toList());
    }

    private FileInfo mapToFileInfo(FileEntity fileEntity) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setName(fileEntity.getName());
        fileInfo.setSize(fileEntity.getSize());
        fileInfo.setExtension(fileEntity.getExtension());
        fileInfo.setHash(fileEntity.getHash());
        fileInfo.setCreationDate(java.sql.Timestamp.valueOf(fileEntity.getCreationDate()));
        return fileInfo;
    }
}
