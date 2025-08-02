package com.yourcompany.workforcemgmt.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Activity {
    private String description;
    private LocalDateTime timestamp;

    public Activity(String description) {
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }
}