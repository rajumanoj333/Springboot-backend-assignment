package com.yourcompany.workforcemgmt.service.impl;

import com.yourcompany.workforcemgmt.common.exception.ResourceNotFoundException;
import com.yourcompany.workforcemgmt.dto.*;
import com.yourcompany.workforcemgmt.mapper.ITaskManagementMapper;
import com.yourcompany.workforcemgmt.model.Activity;
import com.yourcompany.workforcemgmt.model.Comment;
import com.yourcompany.workforcemgmt.model.TaskManagement;
import com.yourcompany.workforcemgmt.model.enums.Priority;
import com.yourcompany.workforcemgmt.model.enums.Task;
import com.yourcompany.workforcemgmt.model.enums.TaskStatus;
import com.yourcompany.workforcemgmt.repository.TaskRepository;
import com.yourcompany.workforcemgmt.service.TaskManagementService;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskManagementServiceImpl implements TaskManagementService {

   private final TaskRepository taskRepository;
   private final ITaskManagementMapper taskMapper;

   public TaskManagementServiceImpl(TaskRepository taskRepository, ITaskManagementMapper taskMapper) {
       this.taskRepository = taskRepository;
       this.taskMapper = taskMapper;
   }

   @Override
   public TaskDto findTaskById(Long id) {
       TaskManagement task = taskRepository.findById(id)
               .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
       return taskMapper.modelToDto(task);
   }

   @Override
   public List<TaskDto> createTasks(TaskCreateRequest createRequest) {
       List<TaskManagement> createdTasks = new ArrayList<>();
       for (TaskCreateRequest.RequestItem item : createRequest.getRequests()) {
           TaskManagement newTask = new TaskManagement();
           newTask.setReferenceId(item.getReferenceId());
           newTask.setReferenceType(item.getReferenceType());
           newTask.setTask(item.getTask());
           newTask.setAssigneeId(item.getAssigneeId());
           newTask.setPriority(item.getPriority());
           newTask.setTaskDeadlineTime(item.getTaskDeadlineTime());
           newTask.setStatus(TaskStatus.ASSIGNED);
           newTask.setDescription("New task created.");
           createdTasks.add(taskRepository.save(newTask));
       }
       return taskMapper.modelListToDtoList(createdTasks);
   }

   @Override
   public List<TaskDto> updateTasks(UpdateTaskRequest updateRequest) {
       List<TaskManagement> updatedTasks = new ArrayList<>();
       for (UpdateTaskRequest.RequestItem item : updateRequest.getRequests()) {
           TaskManagement task = taskRepository.findById(item.getTaskId())
                   .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + item.getTaskId()));

           if (item.getTaskStatus() != null) {
               task.setStatus(item.getTaskStatus());
           }
           if (item.getDescription() != null) {
               task.setDescription(item.getDescription());
           }
           updatedTasks.add(taskRepository.save(task));
       }
       return taskMapper.modelListToDtoList(updatedTasks);
   }

   @Override
   public String assignByReference(AssignByReferenceRequest request) {
       List<Task> applicableTasks = Task.getTasksByReferenceType(request.getReferenceType());
              List<TaskManagement> existingTasks = taskRepository.findByReferenceIdAndReferenceType(request.getReferenceId().toString(), request.getReferenceType().toString());

       for (Task taskType : applicableTasks) {
           List<TaskManagement> tasksOfType = existingTasks.stream()
                   .filter(t -> t.getTask() == taskType && t.getStatus() != TaskStatus.COMPLETED)
                   .collect(Collectors.toList());

           if (!tasksOfType.isEmpty()) {
               tasksOfType.get(0).setAssigneeId(request.getAssigneeId());
               tasksOfType.get(0).getActivities().add(new Activity("Task reassigned to user " + request.getAssigneeId()));
               taskRepository.save(tasksOfType.get(0));

               for (int i = 1; i < tasksOfType.size(); i++) {
                   tasksOfType.get(i).setStatus(TaskStatus.CANCELLED);
                   tasksOfType.get(i).getActivities().add(new Activity("Task cancelled due to reassignment."));
                   taskRepository.save(tasksOfType.get(i));
               }
           }
           else {
               // Create a new task if none exist
               TaskManagement newTask = new TaskManagement();
               newTask.setReferenceId(request.getReferenceId());
               newTask.setReferenceType(request.getReferenceType());
               newTask.setTask(taskType);
               newTask.setAssigneeId(request.getAssigneeId());
               newTask.setStatus(TaskStatus.ASSIGNED);
               taskRepository.save(newTask);
           }
       }
       return "Tasks assigned successfully for reference " + request.getReferenceId();
   }

   @Override
   public List<TaskDto> fetchTasksByDate(TaskFetchByDateRequest request) {
       List<TaskManagement> tasks = taskRepository.findByAssigneeIdIn(request.getAssigneeIds().stream().map(String::valueOf).collect(Collectors.toList()));

       List<TaskManagement> filteredTasks = tasks.stream()
               .filter(task -> task.getStatus() != TaskStatus.CANCELLED)
               .filter(task -> {
                   OffsetDateTime deadline = OffsetDateTime.ofInstant(java.time.Instant.ofEpochMilli(task.getTaskDeadlineTime()), java.time.ZoneId.systemDefault());
                   OffsetDateTime start = OffsetDateTime.ofInstant(java.time.Instant.ofEpochMilli(request.getStartDate()), java.time.ZoneId.systemDefault());
                   OffsetDateTime end = OffsetDateTime.ofInstant(java.time.Instant.ofEpochMilli(request.getEndDate()), java.time.ZoneId.systemDefault());
                   return (deadline.isAfter(start) && deadline.isBefore(end)) || (deadline.isBefore(start) && task.getStatus() != TaskStatus.COMPLETED);
               })
               .collect(Collectors.toList());

       return taskMapper.modelListToDtoList(filteredTasks);
   }

   @Override
   public TaskDto updateTaskPriority(Long taskId, Priority priority) {
       TaskManagement task = taskRepository.findById(taskId)
               .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
       task.setPriority(priority);
       return taskMapper.modelToDto(taskRepository.save(task));
   }

   @Override
   public List<TaskDto> fetchTasksByPriority(Priority priority) {
       List<TaskManagement> tasks = taskRepository.findByPriority(priority);
       return taskMapper.modelListToDtoList(tasks);
   }

   @Override
   public TaskDto addCommentToTask(Long taskId, String userId, String comment) {
       TaskManagement task = taskRepository.findById(taskId)
               .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
       Comment newComment = new Comment(userId, comment);
       task.getComments().add(newComment);
       task.getActivities().add(new Activity("User " + userId + " added a comment."));
       return taskMapper.modelToDto(taskRepository.save(task));
   }
}