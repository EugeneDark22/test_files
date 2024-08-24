package com.example.demo.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class ReportService {

    @Value("${report.directory:/app/reports}")
    private String reportDirectory;

    @Scheduled(cron = "0 30 7 * * ?")
    public void generateReport() throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.beginText();
            contentStream.setLeading(14.5f);
            contentStream.newLineAtOffset(25, 750);
            contentStream.showText("Report generated on: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            contentStream.newLine();
            contentStream.newLine();

            File directory = new File("/app/files");
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                    contentStream.showText("File Name: " + file.getName());
                    contentStream.newLine();
                    contentStream.showText("Size: " + file.length() + " bytes");
                    contentStream.newLine();
                    contentStream.showText("Creation Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(attrs.creationTime().toMillis())));
                    contentStream.newLine();
                    contentStream.showText("Extension: " + getFileExtension(file));
                    contentStream.newLine();
                    contentStream.showText("---------------");
                    contentStream.newLine();
                }
            }

            contentStream.endText();
        }

        File reportFile = new File(reportDirectory, "report_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf");
        document.save(reportFile);
        document.close();
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        return lastIndexOf == -1 ? "" : name.substring(lastIndexOf + 1);
    }
}
