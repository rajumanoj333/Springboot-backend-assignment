package com.railse.hiring.workforcemgmt.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Activity {
   private Long id;
   private Long taskId;
   private String description;
   private Long userId;
   private LocalDateTime createdAt;
   
   public Activity(Long taskId, String description, Long userId) {
       this.taskId = taskId;
       this.description = description;
       this.userId = userId;
       this.createdAt = LocalDateTime.now();
   }
   
   public Activity() {
       this.createdAt = LocalDateTime.now();
   }
}
