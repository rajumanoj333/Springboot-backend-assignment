Your Mission: The Challenge Tasks 🎯
Your mission is divided into three parts. You'll start with setting up the project, then move on to fixing bugs and implementing new features.
Part 0: Project Setup & Structuring
Your first goal is to get the provided code running in a professional project structure. We have provided you with a single Java file that contains all the application's code in the Codebase tab
Create a Project: Set up a new, fully functional Spring Boot project using Gradle.
Structure the Code: Take the classes from the single file and organize them into a standard project layout. A good structure is essential for maintainable code. Here’s an example of what that might look like:
src/main/java/com/yourcompany/workforcemgmt/
├── WorkforcemgmtApplication.java
├── controller/
│   └── TaskController.java
├── service/
│   └── TaskService.java
├── model/
│   └── Task.java
│   └── Staff.java
└── dto/
    └── TaskDto.java
    └── CreateTaskRequest.java


Configure Dependencies: Create your build.gradle file and add the necessary dependencies to make the project work. Key dependencies include:
Spring Web: For building the API endpoints.
Lombok: A great tool to reduce boilerplate code (like getters, setters, and constructors).
MapStruct: A library that simplifies mapping between data objects (e.g., converting a Task model to a TaskDto).

Part 1: Bug Fixes 🐞

Once your project is running, it's time to fix two issues reported by our users.
Bug 1: Task Re-assignment Creates Duplicates
User Report: "As a manager, when I reassign a customer's work to a new salesperson using the 'assign-by-ref' feature, the old task isn't being removed. This creates a duplicate task and causes confusion."
Your Goal: Fix the logic so that when a task is reassigned, the old task for the previous employee is marked as CANCELLED.
Bug 2: Cancelled Tasks Clutter the View
User Report: "As an operations employee, when I fetch my tasks for a specific date range, the list is cluttered with cancelled tasks that I don't need to see."
Your Goal: Modify the task-fetching endpoint so that it only returns tasks that are not CANCELLED.

Part 2: New Features ✨

Now, let's build some highly requested features to improve the application.
Feature 1: A "Smart" Daily Task View
User Need: "As an operations employee, just seeing tasks created today isn't enough. I need a true 'today's work' view that shows everything I need to act on."
Your Goal: Enhance the date-based task fetching logic. When a user queries for a date range, the API should return:
All active tasks that started within that range.
PLUS all active tasks that started before the range but are still open and not yet completed.
Feature 2: Implement Task Priority
User Need: "As a manager, I need to set and change task priorities so my team knows what to focus on."
Your Goal:
Add a priority field to the Task model (e.g., HIGH, MEDIUM, LOW).
Create a new endpoint that allows a manager to change a task's priority after it has been created.
Create a new endpoint to fetch all tasks of a specific priority (e.g., /tasks/priority/HIGH).
Feature 3: Implement Task Comments & Activity History
User Need: "As a team lead, I need to see a full history of a task and add comments so everyone is on the same page."
Your Goal:
Activity History: Automatically log key events for each task. For example: "User A created this task," "User B changed the priority to HIGH."
User Comments: Allow users to add free-text comments to a task.
Viewing: When a user fetches the details for a single task, the API response must include its complete activity history and all user comments, sorted chronologically.

Technical Environment 🛠️

Language: Java 17
Framework: Spring Boot 3.0.4
Build Tool: Gradle
Database: None required! Please use in-memory Java collections (Map, List, etc.) inside a service class to store your data.

// =====================================================================================
// NOTE TO CANDIDATE:
// This is a single-file representation of a multi-file Spring Boot project.
// Each class is clearly marked with its intended file path.
// You can copy each class into its corresponding file in a new Spring Boot project.
// =====================================================================================




// =====================================================================================
// FILE: build.gradle
// =====================================================================================
/*
plugins {
   id 'java'
   id 'org.springframework.boot' version '3.0.4'
   id 'io.spring.dependency-management' version '1.1.0'
}


group = 'com.railse.hiring'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '17'


configurations {
   compileOnly {
       extendsFrom annotationProcessor
   }
}


repositories {
   mavenCentral()
}


dependencies {
   implementation 'org.springframework.boot:spring-boot-starter-web'
   implementation 'org.mapstruct:mapstruct:1.5.3.Final'
   compileOnly 'org.projectlombok:lombok'
   annotationProcessor 'org.projectlombok:lombok'
   annotationProcessor 'org.mapstruct:mapstruct-processor:1.5.3.Final'
   testImplementation 'org.springframework.boot:spring-boot-starter-test'
}


tasks.named('test') {
   useJUnitPlatform()
}
*/


// =====================================================================================
// FILE: src/main/resources/application.properties
// =====================================================================================
/*
server.port=8080
*/




// =====================================================================================
// FILE: src/main/java/com/railse/hiring/workforcemgmt/Application.java
// =====================================================================================
package com.railse.hiring.workforcemgmt;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class Application {
   public static void main(String[] args) {
       SpringApplication.run(Application.class, args);
   }
}




// =====================================================================================
// FILE: src/main/java/com/railse/hiring/workforcemgmt/common/model/enums/ReferenceType.java
// =====================================================================================
package com.railse.hiring.workforcemgmt.common.model.enums;


public enum ReferenceType {
   ENTITY, // Represents a Customer/Transporter
   ORDER,
   ENQUIRY
}




// =====================================================================================
// FILE: src/main/java/com/railse/hiring/workforcemgmt/model/enums/Task.java
// =====================================================================================
package com.railse.hiring.workforcemgmt.model.enums;


import com.railse.hiring.workforcemgmt.common.model.enums.ReferenceType;
import lombok.Getter;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Getter
public enum Task {
   ASSIGN_CUSTOMER_TO_SALES_PERSON(List.of(ReferenceType.ENTITY), "Assign customer to Sales person"),
   CREATE_INVOICE(List.of(ReferenceType.ORDER), "Create Invoice"),
   ARRANGE_PICKUP(List.of(ReferenceType.ORDER), "Arrange Pickup"),
   COLLECT_PAYMENT(List.of(ReferenceType.ORDER), "Collect Payment");


   private final List<ReferenceType> applicableReferenceTypes;
   private final String view;


   Task(List<ReferenceType> applicableReferenceTypes, String view) {
       this.applicableReferenceTypes = applicableReferenceTypes;
       this.view = view;
   }


   public static List<Task> getTasksByReferenceType(ReferenceType referenceType) {
       return Arrays.stream(Task.values())
               .filter(task -> task.getApplicableReferenceTypes().contains(referenceType))
               .collect(Collectors.toList());
   }
}




// =====================================================================================
// FILE: src/main/java/com/railse/hiring/workforcemgmt/model/enums/TaskStatus.java
// =====================================================================================
package com.railse.hiring.workforcemgmt.model.enums;


public enum TaskStatus {
   ASSIGNED,
   STARTED,
   COMPLETED,
   CANCELLED
}




// =====================================================================================
// FILE: src/main/java/com/railse/hiring/workforcemgmt/model/enums/Priority.java
// =====================================================================================
package com.railse.hiring.workforcemgmt.model.enums;


public enum Priority {
   LOW,
   MEDIUM,
   HIGH
}




// =====================================================================================
// FILE: src/main/java/com/railse/hiring/workforcemgmt/model/TaskManagement.java
// =====================================================================================
package com.railse.hiring.workforcemgmt.model;


import com.railse.hiring.workforcemgmt.common.model.enums.ReferenceType;
import com.railse.hiring.workforcemgmt.model.enums.Priority;
import com.railse.hiring.workforcemgmt.model.enums.Task;
import com.railse.hiring.workforcemgmt.model.enums.TaskStatus;
import lombok.Data;


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
}




// =====================================================================================
// FILE: src/main/java/com/railse/hiring/workforcemgmt/dto/TaskManagementDto.java
// =====================================================================================
package com.railse.hiring.workforcemgmt.dto;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.railse.hiring.workforcemgmt.common.model.enums.ReferenceType;
import com.railse.hiring.workforcemgmt.model.enums.Priority;
import com.railse.hiring.workforcemgmt.model.enums.Task;
import com.railse.hiring.workforcemgmt.model.enums.TaskStatus;
import lombok.Data;


@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TaskManagementDto {
   private Long id;
   private Long referenceId;
   private ReferenceType referenceType;
   private Task task;
   private String description;
   private TaskStatus status;
   private Long assigneeId;
   private Long taskDeadlineTime;
   private Priority priority;
}




// =====================================================================================
// FILE: src/main/java/com/railse/hiring/workforcemgmt/dto/TaskCreateRequest.java
// =====================================================================================
package com.railse.hiring.workforcemgmt.dto;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.railse.hiring.workforcemgmt.common.model.enums.ReferenceType;
import com.railse.hiring.workforcemgmt.model.enums.Priority;
import com.railse.hiring.workforcemgmt.model.enums.Task;
import lombok.Data;


import java.util.List;


@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TaskCreateRequest {
   private List<RequestItem> requests;


   @Data
   @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
   public static class RequestItem {
       private Long referenceId;
       private ReferenceType referenceType;
       private Task task;
       private Long assigneeId;
       private Priority priority;
       private Long taskDeadlineTime;
   }
}




// =====================================================================================
// FILE: src/main/java/com/railse/hiring/workforcemgmt/dto/UpdateTaskRequest.java
// =====================================================================================
package com.railse.hiring.workforcemgmt.dto;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.railse.hiring.workforcemgmt.model.enums.TaskStatus;
import lombok.Data;


import java.util.List;


@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateTaskRequest {
   private List<RequestItem> requests;


   @Data
   @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
   public static class RequestItem {
       private Long taskId;
       private TaskStatus taskStatus;
       private String description;
   }
}




// =====================================================================================
// FILE: src/main/java/com/railse/hiring/workforcemgmt/dto/AssignByReferenceRequest.java
// =====================================================================================
package com.railse.hiring.workforcemgmt.dto;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.railse.hiring.workforcemgmt.common.model.enums.ReferenceType;
import lombok.Data;


@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AssignByReferenceRequest {
   private Long referenceId;
   private ReferenceType referenceType;
   private Long assigneeId;
}




// =====================================================================================
// FILE: src/main/java/com/railse/hiring/workforcemgmt/dto/TaskFetchByDateRequest.java
// =====================================================================================
package com.railse.hiring.workforcemgmt.dto;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;


import java.util.List;


@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TaskFetchByDateRequest {
   private Long startDate;
   private Long endDate;
   private List<Long> assigneeIds;
}




// =====================================================================================
// FILE: src/main/java/com/railse/hiring/workforcemgmt/common/model/response/Response.java
// =====================================================================================
package com.railse.hiring.workforcemgmt.common.model.response;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.railse.hiring.workforcemgmt.common.exception.StatusCode;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Response<T> {
   private T data;
   private Pagination pagination;
   private ResponseStatus status;


   public Response(T data, Pagination pagination, ResponseStatus status) {
       this.data = data;
       this.pagination = pagination;
       this.status = status;
   }


   public Response(T data) {
       this(data, null, new ResponseStatus(StatusCode.SUCCESS.getCode(), StatusCode.SUCCESS.getMessage()));
   }
}




// =====================================================================================
// FILE: src/main/java/com/railse/hiring/workforcemgmt/common/model/response/ResponseStatus.java
// =====================================================================================
package com.railse.hiring.workforcemgmt.common.model.response;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResponseStatus {
   private Integer code;
   private String message;
}




// =====================================================================================
// FILE: src/main/java/com/railse/hiring/workforcemgmt/common/model/response/Pagination.java
// =====================================================================================
package com.railse.hiring.workforcemgmt.common.model.response;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;


@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Pagination {
   // Not implemented for this assignment
}




// =====================================================================================
// FILE: src/main/java/com/railse/hiring/workforcemgmt/common/exception/StatusCode.java
// =====================================================================================
package com.railse.hiring.workforcemgmt.common.exception;


import lombok.Getter;


@Getter
public enum StatusCode {
   SUCCESS(200, "Success"),
   BAD_REQUEST(400, "Bad Request"),
   NOT_FOUND(404, "Resource Not Found"),
   INTERNAL_SERVER_ERROR(500, "Internal Server Error");


   private final int code;
   private final String message;


   StatusCode(int code, String message) {
       this.code = code;
       this.message = message;
   }
}




// =====================================================================================
// FILE: src/main/java/com/railse/hiring/workforcemgmt/common/exception/ResourceNotFoundException.java
// =====================================================================================
package com.railse.hiring.workforcemgmt.common.exception;


public class ResourceNotFoundException extends RuntimeException {
   public ResourceNotFoundException(String message) {
       super(message);
   }
}




// =====================================================================================
// FILE: src/main/java/com/railse/hiring/workforcemgmt/common/exception/CustomExceptionHandler.java
// =====================================================================================
package com.railse.hiring.workforcemgmt.common.exception;


import com.railse.hiring.workforcemgmt.common.model.response.Response;
import com.railse.hiring.workforcemgmt.common.model.response.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {


   @ExceptionHandler(ResourceNotFoundException.class)
   public final ResponseEntity<Response<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
       ResponseStatus status = new ResponseStatus(StatusCode.NOT_FOUND.getCode(), ex.getMessage());
       Response<Object> response = new Response<>(null, null, status);
       return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
   }


   @ExceptionHandler(Exception.class)
   public final ResponseEntity<Response<Object>> handleAllExceptions(Exception ex) {
       ResponseStatus status = new ResponseStatus(StatusCode.INTERNAL_SERVER_ERROR.getCode(), "An unexpected error occurred: " + ex.getMessage());
       Response<Object> response = new Response<>(null, null, status);
       return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
   }
}




// =====================================================================================
// FILE: src/main/java/com/railse/hiring/workforcemgmt/mapper/ITaskManagementMapper.java
// =====================================================================================
package com.railse.hiring.workforcemgmt.mapper;


import com.railse.hiring.workforcemgmt.dto.TaskManagementDto;
import com.railse.hiring.workforcemgmt.model.TaskManagement;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;


import java.util.List;


@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ITaskManagementMapper {
   ITaskManagementMapper INSTANCE = Mappers.getMapper(ITaskManagementMapper.class);


   TaskManagementDto modelToDto(TaskManagement model);


   TaskManagement dtoToModel(TaskManagementDto dto);


   List<TaskManagementDto> modelListToDtoList(List<TaskManagement> models);
}




// =====================================================================================
// FILE: src/main/java/com/railse/hiring/workforcemgmt/repository/TaskRepository.java
// =====================================================================================
package com.railse.hiring.workforcemgmt.repository;


import com.railse.hiring.workforcemgmt.model.TaskManagement;
import com.railse.hiring.workforcemgmt.model.enums.TaskStatus;


import java.util.List;
import java.util.Optional;


public interface TaskRepository {
   Optional<TaskManagement> findById(Long id);
   TaskManagement save(TaskManagement task);
   List<TaskManagement> findAll();
   List<TaskManagement> findByReferenceIdAndReferenceType(Long referenceId, com.railse.hiring.workforcemgmt.common.model.enums.ReferenceType referenceType);
   List<TaskManagement> findByAssigneeIdIn(List<Long> assigneeIds);
}




// =====================================================================================
// FILE: src/main/java/com/railse/hiring/workforcemgmt/repository/InMemoryTaskRepository.java
// =====================================================================================
package com.railse.hiring.workforcemgmt.repository;


import com.railse.hiring.workforcemgmt.common.model.enums.ReferenceType;
import com.railse.hiring.workforcemgmt.model.TaskManagement;
import com.railse.hiring.workforcemgmt.model.enums.Priority;
import com.railse.hiring.workforcemgmt.model.enums.Task;
import com.railse.hiring.workforcemgmt.model.enums.TaskStatus;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;


@Repository
public class InMemoryTaskRepository implements TaskRepository {


   private final Map<Long, TaskManagement> taskStore = new ConcurrentHashMap<>();
   private final AtomicLong idCounter = new AtomicLong(0);


   public InMemoryTaskRepository() {
       // Seed data
       createSeedTask(101L, ReferenceType.ORDER, Task.CREATE_INVOICE, 1L, TaskStatus.ASSIGNED, Priority.HIGH);
       createSeedTask(101L, ReferenceType.ORDER, Task.ARRANGE_PICKUP, 1L, TaskStatus.COMPLETED, Priority.HIGH);
       createSeedTask(102L, ReferenceType.ORDER, Task.CREATE_INVOICE, 2L, TaskStatus.ASSIGNED, Priority.MEDIUM);
       createSeedTask(201L, ReferenceType.ENTITY, Task.ASSIGN_CUSTOMER_TO_SALES_PERSON, 2L, TaskStatus.ASSIGNED, Priority.LOW);
       createSeedTask(201L, ReferenceType.ENTITY, Task.ASSIGN_CUSTOMER_TO_SALES_PERSON, 3L, TaskStatus.ASSIGNED, Priority.LOW); // Duplicate for Bug #1
       createSeedTask(103L, ReferenceType.ORDER, Task.COLLECT_PAYMENT, 1L, TaskStatus.CANCELLED, Priority.MEDIUM); // For Bug #2
   }


   private void createSeedTask(Long refId, ReferenceType refType, Task task, Long assigneeId, TaskStatus status, Priority priority) {
       long newId = idCounter.incrementAndGet();
       TaskManagement newTask = new TaskManagement();
       newTask.setId(newId);
       newTask.setReferenceId(refId);
       newTask.setReferenceType(refType);
       newTask.setTask(task);
       newTask.setAssigneeId(assigneeId);
       newTask.setStatus(status);
       newTask.setPriority(priority);
       newTask.setDescription("This is a seed task.");
       newTask.setTaskDeadlineTime(System.currentTimeMillis() + 86400000); // 1 day from now
       taskStore.put(newId, newTask);
   }


   @Override
   public Optional<TaskManagement> findById(Long id) {
       return Optional.ofNullable(taskStore.get(id));
   }


   @Override
   public TaskManagement save(TaskManagement task) {
       if (task.getId() == null) {
           task.setId(idCounter.incrementAndGet());
       }
       taskStore.put(task.getId(), task);
       return task;
   }


   @Override
   public List<TaskManagement> findAll() {
       return List.copyOf(taskStore.values());
   }


   @Override
   public List<TaskManagement> findByReferenceIdAndReferenceType(Long referenceId, ReferenceType referenceType) {
       return taskStore.values().stream()
               .filter(task -> task.getReferenceId().equals(referenceId) && task.getReferenceType().equals(referenceType))
               .collect(Collectors.toList());
   }


   @Override
   public List<TaskManagement> findByAssigneeIdIn(List<Long> assigneeIds) {
       return taskStore.values().stream()
               .filter(task -> assigneeIds.contains(task.getAssigneeId()))
               .collect(Collectors.toList());
   }
}




// =====================================================================================
// FILE: src/main/java/com/railse/hiring/workforcemgmt/service/TaskManagementService.java
// =====================================================================================
package com.railse.hiring.workforcemgmt.service;


import com.railse.hiring.workforcemgmt.dto.*;


import java.util.List;


public interface TaskManagementService {
   List<TaskManagementDto> createTasks(TaskCreateRequest request);
   List<TaskManagementDto> updateTasks(UpdateTaskRequest request);
   String assignByReference(AssignByReferenceRequest request);
   List<TaskManagementDto> fetchTasksByDate(TaskFetchByDateRequest request);
   TaskManagementDto findTaskById(Long id);
}




// =====================================================================================
// FILE: src/main/java/com/railse/hiring/workforcemgmt/service/impl/TaskManagementServiceImpl.java
// =====================================================================================
package com.railse.hiring.workforcemgmt.service.impl;


import com.railse.hiring.workforcemgmt.common.exception.ResourceNotFoundException;
import com.railse.hiring.workforcemgmt.dto.*;
import com.railse.hiring.workforcemgmt.mapper.ITaskManagementMapper;
import com.railse.hiring.workforcemgmt.model.TaskManagement;
import com.railse.hiring.workforcemgmt.model.enums.Task;
import com.railse.hiring.workforcemgmt.model.enums.TaskStatus;
import com.railse.hiring.workforcemgmt.repository.TaskRepository;
import com.railse.hiring.workforcemgmt.service.TaskManagementService;
import org.springframework.stereotype.Service;


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
   public TaskManagementDto findTaskById(Long id) {
       TaskManagement task = taskRepository.findById(id)
               .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
       return taskMapper.modelToDto(task);
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
           createdTasks.add(taskRepository.save(newTask));
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


           // BUG #1 is here. It should assign one and cancel the rest.
           // Instead, it reassigns ALL of them.
           if (!tasksOfType.isEmpty()) {
               for (TaskManagement taskToUpdate : tasksOfType) {
                   taskToUpdate.setAssigneeId(request.getAssigneeId());
                   taskRepository.save(taskToUpdate);
               }
           } else {
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
   public List<TaskManagementDto> fetchTasksByDate(TaskFetchByDateRequest request) {
       List<TaskManagement> tasks = taskRepository.findByAssigneeIdIn(request.getAssigneeIds());


       // BUG #2 is here. It should filter out CANCELLED tasks but doesn't.
       List<TaskManagement> filteredTasks = tasks.stream()
               .filter(task -> {
                   // This logic is incomplete for the assignment.
                   // It should check against startDate and endDate.
                   // For now, it just returns all tasks for the assignees.
                   return true;
               })
               .collect(Collectors.toList());


       return taskMapper.modelListToDtoList(filteredTasks);
   }
}




// =====================================================================================
// FILE: src/main/java/com/railse/hiring/workforcemgmt/controller/TaskManagementController.java
// =====================================================================================
package com.railse.hiring.workforcemgmt.controller;


import com.railse.hiring.workforcemgmt.common.model.response.Response;
import com.railse.hiring.workforcemgmt.dto.*;
import com.railse.hiring.workforcemgmt.service.TaskManagementService;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/task-mgmt")
public class TaskManagementController {


   private final TaskManagementService taskManagementService;


   public TaskManagementController(TaskManagementService taskManagementService) {
       this.taskManagementService = taskManagementService;
   }


   @GetMapping("/{id}")
   public Response<TaskManagementDto> getTaskById(@PathVariable Long id) {
       return new Response<>(taskManagementService.findTaskById(id));
   }


   @PostMapping("/create")
   public Response<List<TaskManagementDto>> createTasks(@RequestBody TaskCreateRequest request) {
       return new Response<>(taskManagementService.createTasks(request));
   }


   @PostMapping("/update")
   public Response<List<TaskManagementDto>> updateTasks(@RequestBody UpdateTaskRequest request) {
       return new Response<>(taskManagementService.updateTasks(request));
   }


   @PostMapping("/assign-by-ref")
   public Response<String> assignByReference(@RequestBody AssignByReferenceRequest request) {
       return new Response<>(taskManagementService.assignByReference(request));
   }


   @PostMapping("/fetch-by-date/v2")
   public Response<List<TaskManagementDto>> fetchByDate(@RequestBody TaskFetchByDateRequest request) {
       return new Response<>(taskManagementService.fetchTasksByDate(request));
   }
}




// =====================================================================================
// FILE: README.md
// =====================================================================================
/*
# Workforce Management - Starter Project


This is a Spring Boot application for the Backend Engineer take-home assignment.


## How to Run


1.  Ensure you have Java 17 and Gradle installed.
2.  Open the project in your favorite IDE (IntelliJ, VSCode, etc.).
3.  Run the main class `com.railse.hiring.workforcemgmt.Application`.
4.  The application will start on `http://localhost:8080`.


## API Endpoints


Here are some example `cURL` commands to interact with the API.


### Get a single task
```bash
curl --location 'http://localhost:8080/task-mgmt/1'
```


### Create a new task
```bash
curl --location 'http://localhost:8080/task-mgmt/create' \
--header 'Content-Type: application/json' \
--data '{
   "requests": [
       {
           "reference_id": 105,
           "reference_type": "ORDER",
           "task": "CREATE_INVOICE",
           "assignee_id": 1,
           "priority": "HIGH",
           "task_deadline_time": 1728192000000
       }
   ]
}'
```


### Update a task's status
```bash
curl --location 'http://localhost:8080/task-mgmt/update' \
--header 'Content-Type: application/json' \
--data '{
   "requests": [
       {
           "task_id": 1,
           "task_status": "STARTED",
           "description": "Work has been started on this invoice."
       }
   ]
}'
```


### Assign tasks by reference (Bug #1 is here)
This assigns all tasks for `reference_id: 201` to `assignee_id: 5`.
```bash
curl --location 'http://localhost:8080/task-mgmt/assign-by-ref' \
--header 'Content-Type: application/json' \
--data '{
   "reference_id": 201,
   "reference_type": "ENTITY",
   "assignee_id": 5
}'
```


### Fetch tasks by date (Bug #2 is here)
This fetches tasks for assignees 1 and 2. It incorrectly includes cancelled tasks.
```bash
curl --location 'http://localhost:8080/task-mgmt/fetch-by-date/v2' \
--header 'Content-Type: application/json' \
--data '{
   "start_date": 1672531200000,
   "end_date": 1735689599000,
   "assignee_ids": [1, 2]
}'
```
*/




// =====================================================================================
// FILE: SUBMISSION.md
// =====================================================================================
/*
# Submission


### 1. Link to your Git Repository Branch
[Your Git Branch URL Here]


### 2. Link to your Video Demonstration
(Please ensure the link is publicly accessible)
[Your Google Drive, Loom, or YouTube Link Here]
*/



