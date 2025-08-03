package com.railse.hiring.workforcemgmt.repository;

import com.railse.hiring.workforcemgmt.common.model.enums.ReferenceType;
import com.railse.hiring.workforcemgmt.model.TaskManagement;
import com.railse.hiring.workforcemgmt.model.Activity;
import com.railse.hiring.workforcemgmt.model.Comment;
import com.railse.hiring.workforcemgmt.model.enums.Priority;
import com.railse.hiring.workforcemgmt.model.enums.Task;
import com.railse.hiring.workforcemgmt.model.enums.TaskStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
   private final AtomicLong activityIdCounter = new AtomicLong(0);
   private final AtomicLong commentIdCounter = new AtomicLong(0);

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
       newTask.setCreatedAt(LocalDateTime.now());
       newTask.setActivities(new ArrayList<>());
       newTask.setComments(new ArrayList<>());
       
       // Add initial activity
       addActivity(newTask, "Task created", assigneeId);
       
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
           task.setCreatedAt(LocalDateTime.now());
           if (task.getActivities() == null) {
               task.setActivities(new ArrayList<>());
           }
           if (task.getComments() == null) {
               task.setComments(new ArrayList<>());
           }
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

   @Override
   public List<TaskManagement> findByPriority(Priority priority) {
       return taskStore.values().stream()
               .filter(task -> task.getPriority() == priority)
               .collect(Collectors.toList());
   }

   // Helper method to add activities
   public void addActivity(TaskManagement task, String description, Long userId) {
       Activity activity = new Activity(task.getId(), description, userId);
       activity.setId(activityIdCounter.incrementAndGet());
       task.getActivities().add(activity);
   }

   // Helper method to add comments
   public void addComment(TaskManagement task, String comment, Long userId) {
       Comment taskComment = new Comment(task.getId(), comment, userId);
       taskComment.setId(commentIdCounter.incrementAndGet());
       task.getComments().add(taskComment);
   }
}
