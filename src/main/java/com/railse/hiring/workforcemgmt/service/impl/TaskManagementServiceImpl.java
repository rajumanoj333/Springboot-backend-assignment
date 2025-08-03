package com.railse.hiring.workforcemgmt.service.impl;

import com.railse.hiring.workforcemgmt.common.exception.ResourceNotFoundException;
import com.railse.hiring.workforcemgmt.dto.*;
import com.railse.hiring.workforcemgmt.mapper.ITaskManagementMapper;
import com.railse.hiring.workforcemgmt.model.TaskManagement;
import com.railse.hiring.workforcemgmt.model.enums.Priority;
import com.railse.hiring.workforcemgmt.model.enums.Task;
import com.railse.hiring.workforcemgmt.model.enums.TaskStatus;
import com.railse.hiring.workforcemgmt.repository.InMemoryTaskRepository;
import com.railse.hiring.workforcemgmt.repository.TaskRepository;
import com.railse.hiring.workforcemgmt.service.TaskManagementService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskManagementServiceImpl implements TaskManagementService {

   private final TaskRepository taskRepository;
   private final InMemoryTaskRepository inMemoryTaskRepository;
   private final ITaskManagementMapper taskMapper;

   public TaskManagementServiceImpl(InMemoryTaskRepository taskRepository, ITaskManagementMapper taskMapper) {
       this.taskRepository = taskRepository;
       this.inMemoryTaskRepository = taskRepository;
       this.taskMapper = taskMapper;
   }

   @Override
   public TaskManagementDto findTaskById(Long id) {
       TaskManagement task = taskRepository.findById(id)
               .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
       return taskMapper.modelToDto(task);
   }

   @Override
   public List<TaskManagementDto> getAllTasks() {
       List<TaskManagement> allTasks = taskRepository.findAll();
       return taskMapper.modelListToDtoList(allTasks);
   }

   @Override
   public List<TaskManagementDto> createTasks(TaskCreateRequest createRequest) {
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
           taskRepository.save(newTask);
           inMemoryTaskRepository.addActivity(newTask, "Task created", item.getAssigneeId());
           createdTasks.add(newTask);
       }
       return taskMapper.modelListToDtoList(createdTasks);
   }

   @Override
   public List<TaskManagementDto> updateTasks(UpdateTaskRequest updateRequest) {
       List<TaskManagement> updatedTasks = new ArrayList<>();
       for (UpdateTaskRequest.RequestItem item : updateRequest.getRequests()) {
           TaskManagement task = taskRepository.findById(item.getTaskId())
                   .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + item.getTaskId()));

           if (item.getTaskStatus() != null) {
               task.setStatus(item.getTaskStatus());
               inMemoryTaskRepository.addActivity(task, "Status updated to " + item.getTaskStatus(), null);
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
       List<TaskManagement> existingTasks = taskRepository.findByReferenceIdAndReferenceType(request.getReferenceId(), request.getReferenceType());

       for (Task taskType : applicableTasks) {
           List<TaskManagement> tasksOfType = existingTasks.stream()
                   .filter(t -> t.getTask() == taskType && t.getStatus() != TaskStatus.COMPLETED)
                   .collect(Collectors.toList());

           boolean reassigned = false;
           if (!tasksOfType.isEmpty()) {
               for (TaskManagement taskToUpdate : tasksOfType) {
                   if (!reassigned) {
                       taskToUpdate.setAssigneeId(request.getAssigneeId());
                       inMemoryTaskRepository.addActivity(taskToUpdate, "Task reassigned", request.getAssigneeId());
                       reassigned = true;
                   } else {
                       taskToUpdate.setStatus(TaskStatus.CANCELLED);
                       inMemoryTaskRepository.addActivity(taskToUpdate, "Task cancelled", null);
                   }
                   taskRepository.save(taskToUpdate);
               }
           } else {
               TaskManagement newTask = new TaskManagement();
               newTask.setReferenceId(request.getReferenceId());
               newTask.setReferenceType(request.getReferenceType());
               newTask.setTask(taskType);
               newTask.setAssigneeId(request.getAssigneeId());
               newTask.setStatus(TaskStatus.ASSIGNED);
               taskRepository.save(newTask);
               inMemoryTaskRepository.addActivity(newTask, "New task assigned", request.getAssigneeId());
           }
       }
       return "Tasks assigned successfully for reference " + request.getReferenceId();
   }

   @Override
   public List<TaskManagementDto> fetchTasksByDate(TaskFetchByDateRequest request) {
       List<TaskManagement> tasks = taskRepository.findByAssigneeIdIn(request.getAssigneeIds());

       // Feature 1: Smart Daily Task View
       // Return:
       // 1. All active tasks that started within the date range
       // 2. PLUS all active tasks that started before the range but are still open and not yet completed
       List<TaskManagement> filteredTasks = tasks.stream()
               .filter(task -> task.getStatus() != TaskStatus.CANCELLED) // Bug Fix #2: Exclude cancelled tasks
               .filter(task -> {
                   // Tasks that started within the date range
                   boolean withinRange = task.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() >= request.getStartDate() &&
                                       task.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() <= request.getEndDate();
                   
                   // Tasks that started before the range but are still open (not completed)
                   boolean beforeRangeButOpen = task.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() < request.getStartDate() &&
                                              (task.getStatus() == TaskStatus.ASSIGNED || task.getStatus() == TaskStatus.STARTED);
                   
                   return withinRange || beforeRangeButOpen;
               })
               .collect(Collectors.toList());

       return taskMapper.modelListToDtoList(filteredTasks);
   }

   @Override
   public List<TaskManagementDto> fetchTasksByPriority(Priority priority) {
       List<TaskManagement> tasksByPriority = taskRepository.findByPriority(priority);
       return taskMapper.modelListToDtoList(tasksByPriority);
   }

   @Override
   public void updateTaskPriority(PriorityUpdateRequest request) {
       TaskManagement task = taskRepository.findById(request.getTaskId())
               .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + request.getTaskId()));

       task.setPriority(request.getPriority());
       inMemoryTaskRepository.addActivity(task, "Priority updated to " + request.getPriority(), request.getUserId());
       taskRepository.save(task);
   }

   @Override
   public void addCommentToTask(CommentRequest request) {
       TaskManagement task = taskRepository.findById(request.getTaskId())
               .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + request.getTaskId()));

       inMemoryTaskRepository.addComment(task, request.getComment(), request.getUserId());
       taskRepository.save(task);
   }
}
