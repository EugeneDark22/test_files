package com.example.demo.model;

import lombok.Data;
import java.util.Date;

@Data
public class FileInfo {
    private String name;
    private long size;
    private String extension;
    private String hash;
    private Date creationDate;
}
