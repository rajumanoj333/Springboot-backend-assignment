package com.yourcompany.workforcemgmt.dto;

import lombok.Data;

@Data
public class CommentRequest {
    private String userId;
    private String comment;
}