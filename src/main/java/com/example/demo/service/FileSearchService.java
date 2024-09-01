package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileSearchService {

    private static final String DIRECTORY_PATH = "/app/files";

    public List<File> searchFilesByContent(String keyword) throws IOException {
        List<File> resultFiles = new ArrayList<>();
        File directory = new File(DIRECTORY_PATH);

        if (directory.exists() && directory.isDirectory()) {
            searchInDirectory(directory, keyword, resultFiles);
        }

        return resultFiles;
    }

    private void searchInDirectory(File directory, String keyword, List<File> resultFiles) throws IOException {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                searchInDirectory(file, keyword, resultFiles);
            } else if (isTextFile(file) && containsKeyword(file, keyword)) {
                resultFiles.add(file);
            }
        }
    }

    private boolean isTextFile(File file) {
        String[] textFileExtensions = {"txt", "md", "csv", "json", "xml", "html", "log"};
        String fileName = file.getName().toLowerCase();
        for (String extension : textFileExtensions) {
            if (fileName.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsKeyword(File file, String keyword) throws IOException {
        return Files.lines(file.toPath()).anyMatch(line -> line.contains(keyword));
    }
}
