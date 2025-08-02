package com.yourcompany.workforcemgmt.model;

import com.yourcompany.workforcemgmt.common.model.enums.ReferenceType;
import com.yourcompany.workforcemgmt.model.enums.Priority;
import com.yourcompany.workforcemgmt.model.enums.Task;
import com.yourcompany.workforcemgmt.model.enums.TaskStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TaskManagement {
   private Long id;
   private Long referenceId;
   private ReferenceType referenceType;
   private Task task;
   private String description;
   private TaskStatus status;
   private Long assigneeId; // Simplified from Entity for this assignment
   private Long taskDeadlineTime;
   private Priority priority;
   private List<Activity> activities = new ArrayList<>();
   private List<Comment> comments = new ArrayList<>();
}