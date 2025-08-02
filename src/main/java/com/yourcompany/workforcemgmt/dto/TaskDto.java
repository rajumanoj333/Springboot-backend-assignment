package com.yourcompany.workforcemgmt.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.yourcompany.workforcemgmt.common.model.enums.ReferenceType;
import com.yourcompany.workforcemgmt.model.Activity;
import com.yourcompany.workforcemgmt.model.Comment;
import com.yourcompany.workforcemgmt.model.enums.Priority;
import com.yourcompany.workforcemgmt.model.enums.Task;
import com.yourcompany.workforcemgmt.model.enums.TaskStatus;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TaskDto {
   private Long id;
   private Long referenceId;
   private ReferenceType referenceType;
   private Task task;
   private String description;
   private TaskStatus status;
   private Long assigneeId;
   private Long taskDeadlineTime;
   private Priority priority;
   private List<Activity> activities;
   private List<Comment> comments;
}