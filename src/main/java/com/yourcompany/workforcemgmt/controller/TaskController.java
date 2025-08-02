package com.yourcompany.workforcemgmt.controller;

import com.yourcompany.workforcemgmt.common.model.response.Response;
import com.yourcompany.workforcemgmt.dto.*;
import com.yourcompany.workforcemgmt.model.enums.Priority;
import com.yourcompany.workforcemgmt.service.TaskManagementService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task-mgmt")
public class TaskController {

   private final TaskManagementService taskManagementService;

   public TaskController(TaskManagementService taskManagementService) {
       this.taskManagementService = taskManagementService;
   }

   @GetMapping("/{id}")
   public Response<TaskDto> getTaskById(@PathVariable Long id) {
       return new Response<>(taskManagementService.findTaskById(id));
   }

   @PostMapping("/create")
   public Response<List<TaskDto>> createTasks(@RequestBody TaskCreateRequest request) {
       return new Response<>(taskManagementService.createTasks(request));
   }

   @PostMapping("/update")
   public Response<List<TaskDto>> updateTasks(@RequestBody UpdateTaskRequest request) {
       return new Response<>(taskManagementService.updateTasks(request));
   }

   @PostMapping("/assign-by-ref")
   public Response<String> assignByReference(@RequestBody AssignByReferenceRequest request) {
       return new Response<>(taskManagementService.assignByReference(request));
   }

   @PostMapping("/fetch-by-date/v2")
   public Response<List<TaskDto>> fetchByDate(@RequestBody TaskFetchByDateRequest request) {
       return new Response<>(taskManagementService.fetchTasksByDate(request));
   }

   @PutMapping("/{taskId}/priority/{priority}")
   public Response<TaskDto> updateTaskPriority(@PathVariable Long taskId, @PathVariable Priority priority) {
       return new Response<>(taskManagementService.updateTaskPriority(taskId, priority));
   }

   @GetMapping("/priority/{priority}")
   public Response<List<TaskDto>> fetchTasksByPriority(@PathVariable Priority priority) {
       return new Response<>(taskManagementService.fetchTasksByPriority(priority));
   }

   @PostMapping("/{taskId}/comments")
   public Response<TaskDto> addCommentToTask(@PathVariable Long taskId, @RequestBody CommentRequest request) {
       return new Response<>(taskManagementService.addCommentToTask(taskId, request.getUserId(), request.getComment()));
   }
}