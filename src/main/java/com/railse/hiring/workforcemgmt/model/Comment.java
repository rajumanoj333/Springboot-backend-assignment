package com.railse.hiring.workforcemgmt.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
   private Long id;
   private Long taskId;
   private String comment;
   private Long userId;
   private LocalDateTime createdAt;
   
   public Comment(Long taskId, String comment, Long userId) {
       this.taskId = taskId;
       this.comment = comment;
       this.userId = userId;
       this.createdAt = LocalDateTime.now();
   }
   
   public Comment() {
       this.createdAt = LocalDateTime.now();
   }
}
