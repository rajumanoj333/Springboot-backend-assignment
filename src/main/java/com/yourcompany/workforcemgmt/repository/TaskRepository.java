package com.yourcompany.workforcemgmt.repository;

import com.yourcompany.workforcemgmt.model.TaskManagement;
import com.yourcompany.workforcemgmt.model.enums.Priority;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    Optional<TaskManagement> findById(Long id);
    TaskManagement save(TaskManagement task);
    List<TaskManagement> findAll();
    List<TaskManagement> findByReferenceIdAndReferenceType(String referenceId, String referenceType);
    List<TaskManagement> findByAssigneeIdIn(List<String> assigneeIds);
    List<TaskManagement> findByPriority(Priority priority);
}