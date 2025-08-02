package com.yourcompany.workforcemgmt.mapper;

import com.yourcompany.workforcemgmt.dto.TaskDto;
import com.yourcompany.workforcemgmt.model.TaskManagement;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ITaskManagementMapper {
   ITaskManagementMapper INSTANCE = Mappers.getMapper(ITaskManagementMapper.class);

   TaskDto modelToDto(TaskManagement model);

   TaskManagement dtoToModel(TaskDto dto);

   List<TaskDto> modelListToDtoList(List<TaskManagement> models);
}