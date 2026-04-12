package com.example.demo.mapper;

import com.example.demo.dto.TaskCreateDto;
import com.example.demo.dto.TaskResponseDto;
import com.example.demo.dto.TaskUpdateDto;
import com.example.demo.model.Task;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "completed", constant = "false")
  @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "attachments", ignore = true)
  Task toEntity(TaskCreateDto dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "attachments", ignore = true)
  void updateEntity(TaskUpdateDto dto, @MappingTarget Task task);

  TaskResponseDto toResponseDto(Task task);

  List<TaskResponseDto> toResponseDtoList(List<Task> tasks);
}
