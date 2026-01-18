package com.uit.sociusmvcapp.task.internal.repository;

import com.uit.sociusmvcapp.shared.request.SortRequest;
import com.uit.sociusmvcapp.task.EmployeeGateway;
import com.uit.sociusmvcapp.task.dto.SearchTaskDto;
import com.uit.sociusmvcapp.task.dto.TaskActivityDto;
import com.uit.sociusmvcapp.task.dto.TaskDto;
import com.uit.sociusmvcapp.task.dto.request.CreateTaskRequest;
import com.uit.sociusmvcapp.task.dto.request.SearchTaskRequest;
import com.uit.sociusmvcapp.task.dto.request.UpdateTaskRequest;
import com.uit.sociusmvcapp.task.internal.converter.TaskConverter;
import com.uit.sociusmvcapp.task.internal.domain.Task;
import com.uit.sociusmvcapp.task.internal.domain.TaskActivity;
import com.uit.sociusmvcapp.task.internal.dto.ParentTaskContext;
import com.uit.sociusmvcapp.task.internal.persistence.TaskActivityMapper;
import com.uit.sociusmvcapp.task.internal.persistence.TaskMapper;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** Repository for Task and TaskActivity operations. */
@Repository
@RequiredArgsConstructor
public class TaskRepository {

  private final TaskMapper taskMapper;
  private final TaskActivityMapper taskActivityMapper;
  private final TaskConverter taskConverter;
  private final EmployeeGateway employeeGateway;

  /**
   * Create a new task from request.
   *
   * @param request create task request
   * @param currentUserId current user ID
   */
  public void createTask(CreateTaskRequest request, String currentUserId) {
    taskMapper.insert(taskConverter.createRequestToEntity(request, currentUserId));
  }

  /**
   * Create a sub-task from request.
   *
   * @param request create task request
   * @param currentUserId current user ID
   * @param parentContext parent task context (id, teamCode, departmentCode)
   * @return task ID
   */
  public Integer createSubTask(
      CreateTaskRequest request, String currentUserId, ParentTaskContext parentContext) {
    Task task = taskConverter.createRequestToEntity(request, currentUserId);
    task.setParentId(parentContext.getParentId());
    task.setTeamCode(parentContext.getTeamCode());
    task.setDepartmentCode(parentContext.getDepartmentCode());
    taskMapper.insert(task);
    return task.getId();
  }

  /**
   * Update task from request.
   *
   * @param id task ID
   * @param request update task request
   */
  public void updateTask(Integer id, UpdateTaskRequest request) {
    taskMapper.update(taskConverter.updateRequestToEntity(request, id));
  }

  /**
   * Soft delete task.
   *
   * @param id task ID
   */
  public void delete(Integer id) {
    taskMapper.softDelete(id);
  }

  /**
   * Find task entity by ID.
   *
   * @param id task ID
   * @return task entity
   */
  public Task findEntityById(Integer id) {
    return taskMapper.findById(id);
  }

  /**
   * Check if task exists by ID.
   *
   * @param id task ID
   * @return true if task exists
   */
  public boolean existsById(Integer id) {
    return taskMapper.existsById(id);
  }

  /**
   * Find task DTO by ID.
   *
   * @param id task ID
   * @return task DTO
   */
  public TaskDto findDtoById(Integer id) {
    TaskDto dto = taskConverter.entityToDto(taskMapper.findById(id));
    // Enrich with employee names
    enrichWithEmployeeNames(dto);
    // Set sub-task count
    dto.setSubTaskCount(taskMapper.countChildren(id));
    return dto;
  }

  /**
   * Find tasks by parent ID.
   *
   * @param parentId parent task ID
   * @return list of tasks
   */
  public List<Task> findByParentId(Integer parentId) {
    return taskMapper.findByParentId(parentId);
  }

  /**
   * Find sub-tasks of a parent task with employee name enrichment.
   *
   * @param parentId parent task ID
   * @return list of SearchTaskDto with enriched employee names
   */
  public List<SearchTaskDto> findSubTasksWithEnrichment(Integer parentId) {
    List<Task> entities = taskMapper.findByParentId(parentId);

    // Convert entities to DTOs
    List<SearchTaskDto> tasks =
        entities.stream().map(taskConverter::entityToSearchDto).collect(Collectors.toList());

    // Batch enrich with employee names (avoid N+1)
    enrichSearchTasksWithEmployeeNames(tasks);

    return tasks;
  }

  /**
   * Search tasks with filters and pagination.
   *
   * @param criteria search criteria
   * @param sorts sorting options
   * @param currentUserId current user ID
   * @param limit maximum number of records to return
   * @param offset starting point for records to return
   * @return list of SearchTaskDto matching the search criteria
   */
  public List<SearchTaskDto> search(
      SearchTaskRequest criteria,
      List<SortRequest> sorts,
      String currentUserId,
      int limit,
      int offset) {
    List<Task> entities = taskMapper.search(criteria, sorts, currentUserId, offset, limit);

    // Convert entities to DTOs
    List<SearchTaskDto> tasks =
        entities.stream().map(taskConverter::entityToSearchDto).collect(Collectors.toList());

    // Batch enrich with employee names (avoid N+1)
    enrichSearchTasksWithEmployeeNames(tasks);

    return tasks;
  }

  /**
   * Count tasks matching search criteria.
   *
   * @param criteria search criteria
   * @param currentUserId current user ID
   * @return total count of tasks matching the criteria
   */
  public long count(SearchTaskRequest criteria, String currentUserId) {
    return taskMapper.count(criteria, currentUserId);
  }

  /**
   * Update task status.
   *
   * @param id task ID
   * @param statusCode status code
   */
  public void updateStatus(Integer id, int statusCode) {
    taskMapper.updateStatus(id, statusCode);
  }

  /**
   * Check if task has children.
   *
   * @param taskId task ID
   * @return true if has children
   */
  public boolean hasChildren(Integer taskId) {
    return taskMapper.hasChildren(taskId);
  }

  /**
   * Check if all children are approved.
   *
   * @param parentId parent task ID
   * @return true if all children approved
   */
  public boolean areAllChildrenApproved(Integer parentId) {
    return taskMapper.areAllChildrenApproved(parentId);
  }

  /**
   * Cascade delete children.
   *
   * @param parentId parent task ID
   */
  public void cascadeDeleteChildren(Integer parentId) {
    taskMapper.cascadeDeleteChildren(parentId);
  }

  /**
   * Cascade cancel children.
   *
   * @param parentId parent task ID
   */
  public void cascadeCancelChildren(Integer parentId) {
    taskMapper.cascadeCancelChildren(parentId);
  }

  /**
   * Save task activity.
   *
   * @param activity activity to save
   */
  public void saveActivity(TaskActivity activity) {
    taskActivityMapper.insert(activity);
  }

  /**
   * Get all activities for a task.
   *
   * @param taskId task ID
   * @return list of activity DTOs
   */
  public List<TaskActivityDto> getActivities(Integer taskId) {
    return taskActivityMapper.findByTaskId(taskId);
  }

  // ========== Private Helper Methods ==========

  /**
   * Enrich single TaskDto with employee names.
   *
   * @param dto task DTO to enrich
   */
  private void enrichWithEmployeeNames(TaskDto dto) {
    if (dto.getReceiverId() != null) {
      String receiverName = employeeGateway.getEmployeeName(dto.getReceiverId());
      dto.setReceiverName(receiverName);
    }

    if (dto.getSenderId() != null) {
      String senderName = employeeGateway.getEmployeeName(dto.getSenderId());
      dto.setSenderName(senderName);
    }
  }

  /**
   * Batch enrich SearchTaskDto list with employee names (avoid N+1 problem).
   *
   * @param dtos list of search task DTOs to enrich
   */
  private void enrichSearchTasksWithEmployeeNames(List<SearchTaskDto> dtos) {
    if (dtos.isEmpty()) {
      return;
    }

    // Collect all unique employee IDs
    Set<String> employeeIds = new HashSet<>();
    dtos.forEach(
        dto -> {
          if (dto.getReceiverId() != null) {
            employeeIds.add(dto.getReceiverId());
          }
          if (dto.getSenderId() != null) {
            employeeIds.add(dto.getSenderId());
          }
        });

    if (employeeIds.isEmpty()) {
      return;
    }

    // Batch load employee names (1 query for all)
    Map<String, String> employeeNames = employeeGateway.getEmployeeNames(employeeIds);

    // Map names to DTOs
    dtos.forEach(
        dto -> {
          if (dto.getReceiverId() != null) {
            dto.setReceiverName(employeeNames.get(dto.getReceiverId()));
          }
          if (dto.getSenderId() != null) {
            dto.setSenderName(employeeNames.get(dto.getSenderId()));
          }
        });
  }
}
