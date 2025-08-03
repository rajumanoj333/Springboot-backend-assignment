package com.railse.hiring.workforcemgmt.mapper;

import com.railse.hiring.workforcemgmt.dto.TaskManagementDto;
import com.railse.hiring.workforcemgmt.model.Activity;
import com.railse.hiring.workforcemgmt.model.Comment;
import com.railse.hiring.workforcemgmt.model.TaskManagement;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-03T17:31:05+0530",
    comments = "version: 1.5.3.Final, compiler: Eclipse JDT (IDE) 3.42.50.v20250624-0847, environment: Java 21.0.7 (Eclipse Adoptium)"
)
@Component
public class ITaskManagementMapperImpl implements ITaskManagementMapper {

    @Override
    public TaskManagementDto modelToDto(TaskManagement model) {
        if ( model == null ) {
            return null;
        }

        TaskManagementDto taskManagementDto = new TaskManagementDto();

        List<Activity> list = model.getActivities();
        if ( list != null ) {
            taskManagementDto.setActivities( new ArrayList<Activity>( list ) );
        }
        taskManagementDto.setAssigneeId( model.getAssigneeId() );
        List<Comment> list1 = model.getComments();
        if ( list1 != null ) {
            taskManagementDto.setComments( new ArrayList<Comment>( list1 ) );
        }
        taskManagementDto.setCreatedAt( model.getCreatedAt() );
        taskManagementDto.setDescription( model.getDescription() );
        taskManagementDto.setId( model.getId() );
        taskManagementDto.setPriority( model.getPriority() );
        taskManagementDto.setReferenceId( model.getReferenceId() );
        taskManagementDto.setReferenceType( model.getReferenceType() );
        taskManagementDto.setStatus( model.getStatus() );
        taskManagementDto.setTask( model.getTask() );
        taskManagementDto.setTaskDeadlineTime( model.getTaskDeadlineTime() );

        return taskManagementDto;
    }

    @Override
    public TaskManagement dtoToModel(TaskManagementDto dto) {
        if ( dto == null ) {
            return null;
        }

        TaskManagement taskManagement = new TaskManagement();

        List<Activity> list = dto.getActivities();
        if ( list != null ) {
            taskManagement.setActivities( new ArrayList<Activity>( list ) );
        }
        taskManagement.setAssigneeId( dto.getAssigneeId() );
        List<Comment> list1 = dto.getComments();
        if ( list1 != null ) {
            taskManagement.setComments( new ArrayList<Comment>( list1 ) );
        }
        taskManagement.setCreatedAt( dto.getCreatedAt() );
        taskManagement.setDescription( dto.getDescription() );
        taskManagement.setId( dto.getId() );
        taskManagement.setPriority( dto.getPriority() );
        taskManagement.setReferenceId( dto.getReferenceId() );
        taskManagement.setReferenceType( dto.getReferenceType() );
        taskManagement.setStatus( dto.getStatus() );
        taskManagement.setTask( dto.getTask() );
        taskManagement.setTaskDeadlineTime( dto.getTaskDeadlineTime() );

        return taskManagement;
    }

    @Override
    public List<TaskManagementDto> modelListToDtoList(List<TaskManagement> models) {
        if ( models == null ) {
            return null;
        }

        List<TaskManagementDto> list = new ArrayList<TaskManagementDto>( models.size() );
        for ( TaskManagement taskManagement : models ) {
            list.add( modelToDto( taskManagement ) );
        }

        return list;
    }
}
