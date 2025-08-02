package com.yourcompany.workforcemgmt.service;

import com.yourcompany.workforcemgmt.dto.*;
import com.yourcompany.workforcemgmt.model.enums.Priority;

import java.util.List;

public interface TaskManagementService {
   List<TaskDto> createTasks(TaskCreateRequest request);
   List<TaskDto> updateTasks(UpdateTaskRequest request);
   String assignByReference(AssignByReferenceRequest request);
   List<TaskDto> fetchTasksByDate(TaskFetchByDateRequest request);
   TaskDto findTaskById(Long id);
   TaskDto updateTaskPriority(Long taskId, Priority priority);
   List<TaskDto> fetchTasksByPriority(Priority priority);
   TaskDto addCommentToTask(Long taskId, String userId, String comment);
}