package com.yourcompany.workforcemgmt.service;

import com.yourcompany.workforcemgmt.common.model.enums.ReferenceType;
import com.yourcompany.workforcemgmt.dto.*;
import com.yourcompany.workforcemgmt.mapper.ITaskManagementMapper;
import com.yourcompany.workforcemgmt.model.TaskManagement;
import com.yourcompany.workforcemgmt.model.enums.Priority;
import com.yourcompany.workforcemgmt.model.enums.Task;
import com.yourcompany.workforcemgmt.model.enums.TaskStatus;
import com.yourcompany.workforcemgmt.repository.TaskRepository;
import com.yourcompany.workforcemgmt.service.impl.TaskManagementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TaskManagementServiceTest {

    @InjectMocks
    private TaskManagementServiceImpl taskManagementService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ITaskManagementMapper taskMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateTasks() {
        TaskCreateRequest request = new TaskCreateRequest();
        TaskCreateRequest.RequestItem item = new TaskCreateRequest.RequestItem();
        item.setReferenceId(123L);
        item.setReferenceType(ReferenceType.ORDER);
        item.setTask(Task.CREATE_INVOICE);
        item.setAssigneeId(1L);
        item.setPriority(Priority.HIGH);
        item.setTaskDeadlineTime(System.currentTimeMillis());
        request.setRequests(List.of(item));

        TaskManagement task = new TaskManagement();
        task.setId(1L);
        task.setStatus(TaskStatus.ASSIGNED);

        when(taskRepository.save(any(TaskManagement.class))).thenReturn(task);
        when(taskMapper.modelListToDtoList(any())).thenReturn(List.of(new TaskDto()));

        List<TaskDto> result = taskManagementService.createTasks(request);

        assertEquals(1, result.size());
    }

    @Test
    public void testFindTaskById() {
        TaskManagement task = new TaskManagement();
        task.setId(1L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.modelToDto(any(TaskManagement.class))).thenReturn(new TaskDto());

        TaskDto result = taskManagementService.findTaskById(1L);

        assertEquals(TaskDto.class, result.getClass());
    }

    @Test
    public void testAssignByReference() {
        AssignByReferenceRequest request = new AssignByReferenceRequest();
        request.setReferenceId(123L);
        request.setReferenceType(ReferenceType.ORDER);
        request.setAssigneeId(1L);

        List<TaskManagement> existingTasks = new ArrayList<>();
        TaskManagement task1 = new TaskManagement();
        task1.setId(1L);
        task1.setTask(Task.CREATE_INVOICE);
        task1.setStatus(TaskStatus.ASSIGNED);
        existingTasks.add(task1);

        TaskManagement task2 = new TaskManagement();
        task2.setId(2L);
        task2.setTask(Task.CREATE_INVOICE);
        task2.setStatus(TaskStatus.ASSIGNED);
        existingTasks.add(task2);

        when(taskRepository.findByReferenceIdAndReferenceType(any(), any())).thenReturn(existingTasks);

        String result = taskManagementService.assignByReference(request);

        assertEquals("Tasks assigned successfully for reference 123", result);
        assertEquals(1L, task1.getAssigneeId());
        assertEquals(TaskStatus.CANCELLED, task2.getStatus());
    }

    @Test
    public void testFetchTasksByDate() {
        TaskFetchByDateRequest request = new TaskFetchByDateRequest();
        request.setAssigneeIds(List.of(1L));
        request.setStartDate(OffsetDateTime.now().minusDays(1).toEpochSecond());
        request.setEndDate(OffsetDateTime.now().plusDays(1).toEpochSecond());

        List<TaskManagement> tasks = new ArrayList<>();
        TaskManagement task1 = new TaskManagement();
        task1.setId(1L);
        task1.setStatus(TaskStatus.ASSIGNED);
        task1.setTaskDeadlineTime(OffsetDateTime.now().toEpochSecond());
        tasks.add(task1);

        TaskManagement task2 = new TaskManagement();
        task2.setId(2L);
        task2.setStatus(TaskStatus.CANCELLED);
        task2.setTaskDeadlineTime(System.currentTimeMillis());
        tasks.add(task2);

        when(taskRepository.findByAssigneeIdIn(any())).thenReturn(tasks);
        when(taskMapper.modelListToDtoList(any())).thenReturn(List.of(new TaskDto()));

        List<TaskDto> result = taskManagementService.fetchTasksByDate(request);

        assertEquals(1, result.size());
    }
}