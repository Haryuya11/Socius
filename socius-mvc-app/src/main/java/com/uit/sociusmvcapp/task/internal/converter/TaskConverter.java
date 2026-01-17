package com.uit.sociusmvcapp.task.internal.converter;

import com.uit.sociusmvcapp.shared.converter.BaseConverter;
import com.uit.sociusmvcapp.shared.utils.TaskValidationUtils;
import com.uit.sociusmvcapp.task.dto.SearchTaskDto;
import com.uit.sociusmvcapp.task.dto.TaskDto;
import com.uit.sociusmvcapp.task.dto.request.CreateTaskRequest;
import com.uit.sociusmvcapp.task.dto.request.UpdateTaskRequest;
import com.uit.sociusmvcapp.task.enums.TaskStatus;
import com.uit.sociusmvcapp.task.internal.domain.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/** MapStruct converter for Task entity with LocalDate to LocalDateTime normalization. */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    imports = {TaskStatus.class, TaskValidationUtils.class})
public interface TaskConverter extends BaseConverter<Task, TaskDto> {

  /**
   * Override entityToDto to explicitly ignore enriched fields that are populated separately in
   * TaskRepository.
   *
   * @param entity the Task entity
   * @return the corresponding TaskDto
   */
  @Override
  @Mapping(target = "receiverName", ignore = true)
  @Mapping(target = "senderName", ignore = true)
  @Mapping(target = "subTaskCount", ignore = true)
  TaskDto entityToDto(Task entity);

  /**
   * Converts a CreateTaskRequest to a Task entity. Normalizes LocalDate to LocalDateTime: - Start
   * date: 00:00:01 - Due date: 23:59:59
   *
   * @param request the CreateTaskRequest
   * @param senderId sender employee ID
   * @return the corresponding Task entity
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "senderId", source = "senderId")
  @Mapping(target = "status", expression = "java(TaskStatus.IN_PROGRESS)")
  @Mapping(
      target = "startDate",
      expression = "java(TaskValidationUtils.normalizeToStartOfDay(request.getStartDate()))")
  @Mapping(
      target = "dueDate",
      expression = "java(TaskValidationUtils.normalizeToEndOfDay(request.getDueDate()))")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "deleteFlag", ignore = true)
  Task createRequestToEntity(CreateTaskRequest request, String senderId);

  /**
   * Converts a Task entity to a SearchTaskDto (lighter DTO for search results).
   *
   * @param task the Task entity
   * @return the corresponding SearchTaskDto
   */
  @Mapping(target = "receiverName", ignore = true)
  @Mapping(target = "senderName", ignore = true)
  SearchTaskDto entityToSearchDto(Task task);

  /**
   * Converts an UpdateTaskRequest to a Task entity. Normalizes LocalDate to LocalDateTime: - Start
   * date: 00:00:01 (if provided) - Due date: 23:59:59 (if provided)
   *
   * @param request the UpdateTaskRequest
   * @param id task ID
   * @return the corresponding Task entity
   */
  @Mapping(target = "id", source = "id")
  @Mapping(target = "senderId", ignore = true)
  @Mapping(target = "parentId", ignore = true)
  @Mapping(target = "teamCode", ignore = true)
  @Mapping(target = "departmentCode", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(
      target = "startDate",
      expression =
          "java(request.getStartDate() != null ?"
              + " TaskValidationUtils.normalizeToStartOfDay(request.getStartDate()) : null)")
  @Mapping(
      target = "dueDate",
      expression =
          "java(request.getDueDate() != null ?"
              + " TaskValidationUtils.normalizeToEndOfDay(request.getDueDate()) : null)")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "deleteFlag", ignore = true)
  Task updateRequestToEntity(UpdateTaskRequest request, Integer id);
}
