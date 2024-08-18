package com.example.demo.service;

import com.example.demo.model.FileEntity;
import com.example.demo.repository.FileRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class FileScheduler {

    private final String DIRECTORY_PATH = "C://Users//koropetskiy//Desktop//files"; // Укажите путь к директории с файлами

    @Autowired
    private FileRepository fileRepository;

    @Scheduled(fixedRate = 1800000) // Запуск каждые полчаса
    public void updateFileDatabase() throws IOException {
        List<Path> filesInDirectory = Files.list(Paths.get(DIRECTORY_PATH)).toList();

        for (Path filePath : filesInDirectory) {
            String fileName = filePath.getFileName().toString();
            fileRepository.findByName(fileName).ifPresentOrElse(
                    fileEntity -> updateFile(fileEntity, filePath),
                    () -> saveNewFile(filePath)
            );
        }
    }

    private void updateFile(FileEntity fileEntity, Path filePath) {
        try {
            fileEntity.setSize(Files.size(filePath));
            fileEntity.setHash(DigestUtils.sha256Hex(Files.newInputStream(filePath)));
            fileEntity.setCreationDate(Files.getLastModifiedTime(filePath).toMillis());
            fileRepository.save(fileEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveNewFile(Path filePath) {
        try {
            FileEntity newFile = new FileEntity();
            newFile.setName(filePath.getFileName().toString());
            newFile.setSize(Files.size(filePath));
            newFile.setExtension(getFileExtension(filePath.getFileName().toString()));
            newFile.setHash(DigestUtils.sha256Hex(Files.newInputStream(filePath)));
            newFile.setCreationDate(Files.getLastModifiedTime(filePath).toMillis());
            fileRepository.save(newFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }
}
