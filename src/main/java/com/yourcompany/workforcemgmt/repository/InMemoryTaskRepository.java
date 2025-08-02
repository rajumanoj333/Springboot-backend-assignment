package com.yourcompany.workforcemgmt.repository;

import com.yourcompany.workforcemgmt.common.model.enums.ReferenceType;
import com.yourcompany.workforcemgmt.model.TaskManagement;
import com.yourcompany.workforcemgmt.model.enums.Priority;
import com.yourcompany.workforcemgmt.model.enums.Task;
import com.yourcompany.workforcemgmt.model.enums.TaskStatus;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryTaskRepository implements TaskRepository {

    private final Map<Long, TaskManagement> tasks = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    public InMemoryTaskRepository() {
        createSeedTask(1L, 101L, ReferenceType.ORDER, Task.CREATE_INVOICE, "1", TaskStatus.ASSIGNED, Priority.HIGH);
        createSeedTask(2L, 101L, ReferenceType.ORDER, Task.ARRANGE_PICKUP, "1", TaskStatus.COMPLETED, Priority.HIGH);
        createSeedTask(3L, 102L, ReferenceType.ORDER, Task.CREATE_INVOICE, "2", TaskStatus.ASSIGNED, Priority.MEDIUM);
        createSeedTask(4L, 201L, ReferenceType.ENTITY, Task.ASSIGN_CUSTOMER_TO_SALES_PERSON, "2", TaskStatus.ASSIGNED, Priority.LOW);
        createSeedTask(5L, 201L, ReferenceType.ENTITY, Task.ASSIGN_CUSTOMER_TO_SALES_PERSON, "3", TaskStatus.ASSIGNED, Priority.LOW); // Duplicate for Bug #1
        createSeedTask(6L, 103L, ReferenceType.ORDER, Task.COLLECT_PAYMENT, "1", TaskStatus.CANCELLED, Priority.MEDIUM); // For Bug #2
    }

    @Override
    public Optional<TaskManagement> findById(Long id) {
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public TaskManagement save(TaskManagement task) {
        if (task.getId() == null) {
            task.setId(sequence.incrementAndGet());
        }
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public List<TaskManagement> findAll() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<TaskManagement> findByReferenceIdAndReferenceType(String referenceId, String referenceType) {
        return tasks.values().stream()
                .filter(task -> task.getReferenceId().toString().equals(referenceId) && task.getReferenceType().toString().equals(referenceType))
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskManagement> findByAssigneeIdIn(List<String> assigneeIds) {
        return tasks.values().stream()
                .filter(task -> assigneeIds.contains(task.getAssigneeId().toString()))
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskManagement> findByPriority(Priority priority) {
        return tasks.values().stream()
                .filter(task -> task.getPriority() == priority)
                .collect(Collectors.toList());
    }

    private void createSeedTask(Long id, Long referenceId, ReferenceType referenceType, Task task, String assigneeId, TaskStatus status, Priority priority) {
        TaskManagement newTask = new TaskManagement();
        newTask.setId(id);
        newTask.setReferenceId(referenceId);
        newTask.setReferenceType(referenceType);
        newTask.setTask(task);
        newTask.setAssigneeId(Long.parseLong(assigneeId));
        newTask.setStatus(status);
        newTask.setPriority(priority);
        newTask.setTaskDeadlineTime(System.currentTimeMillis() + 86400000); // 1 day from now
        tasks.put(id, newTask);
    }
}
