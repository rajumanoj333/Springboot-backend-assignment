package com.yourcompany.workforcemgmt.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
    private String userId;
    private String comment;
    private LocalDateTime timestamp;

    public Comment(String userId, String comment) {
        this.userId = userId;
        this.comment = comment;
        this.timestamp = LocalDateTime.now();
    }
}