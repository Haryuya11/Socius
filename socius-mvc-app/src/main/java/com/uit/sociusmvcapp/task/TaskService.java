package com.uit.sociusmvcapp.task;

import com.uit.sociusmvcapp.shared.request.PaginationSearchRequest;
import com.uit.sociusmvcapp.shared.response.PageResponse;
import com.uit.sociusmvcapp.task.dto.SearchTaskDto;
import com.uit.sociusmvcapp.task.dto.TaskActivityDto;
import com.uit.sociusmvcapp.task.dto.TaskDto;
import com.uit.sociusmvcapp.task.dto.request.ApproveTaskRequest;
import com.uit.sociusmvcapp.task.dto.request.CancelTaskRequest;
import com.uit.sociusmvcapp.task.dto.request.CreateTaskRequest;
import com.uit.sociusmvcapp.task.dto.request.RejectTaskRequest;
import com.uit.sociusmvcapp.task.dto.request.ReopenTaskRequest;
import com.uit.sociusmvcapp.task.dto.request.SearchTaskRequest;
import com.uit.sociusmvcapp.task.dto.request.SubmitReviewRequest;
import com.uit.sociusmvcapp.task.dto.request.UpdateTaskRequest;
import java.util.List;

/** Service interface for task operations. */
public interface TaskService {

  /**
   * Create a new task.
   *
   * @param request create task request
   */
  void create(CreateTaskRequest request);

  /**
   * Create a sub-task.
   *
   * @param parentId parent task ID
   * @param request create task request
   */
  void createSubTask(Integer parentId, CreateTaskRequest request);

  /**
   * Get task by ID.
   *
   * @param id task ID
   * @return task DTO
   */
  TaskDto getById(Integer id);

  /**
   * Update task.
   *
   * @param id task ID
   * @param request update request
   * @return updated task DTO
   */
  TaskDto update(Integer id, UpdateTaskRequest request);

  /**
   * Delete task (soft delete with cascade to children).
   *
   * @param id task ID
   */
  void delete(Integer id);

  /**
   * Search tasks with filters and pagination.
   *
   * @param request the pagination search request containing search criteria
   * @return page response with tasks
   */
  PageResponse<SearchTaskDto> search(PaginationSearchRequest<SearchTaskRequest> request);

  /**
   * Get sub-tasks of a parent task.
   *
   * @param parentId parent task ID
   * @return list of sub-tasks
   */
  List<SearchTaskDto> getSubTasks(Integer parentId);

  /**
   * Get all activities for a task.
   *
   * @param taskId task ID
   * @return list of activity DTOs
   */
  List<TaskActivityDto> getActivities(Integer taskId);

  /**
   * Get tasks assigned to current user (receiver).
   *
   * @param pageNumber page number
   * @param pageSize page size
   * @param sortBy sort field
   * @param sortDirection sort direction
   * @return page response with tasks
   */
  PageResponse<SearchTaskDto> getMyTasks(
      Integer pageNumber, Integer pageSize, String sortBy, String sortDirection);

  /**
   * Get tasks assigned by current user (sender).
   *
   * @param pageNumber page number
   * @param pageSize page size
   * @param sortBy sort field
   * @param sortDirection sort direction
   * @return page response with tasks
   */
  PageResponse<SearchTaskDto> getAssignedByMe(
      Integer pageNumber, Integer pageSize, String sortBy, String sortDirection);

  /**
   * Get tasks by team code.
   *
   * @param teamCode team code
   * @param pageNumber page number
   * @param pageSize page size
   * @param sortBy sort field
   * @param sortDirection sort direction
   * @return page response with tasks
   */
  PageResponse<SearchTaskDto> getTasksByTeam(
      String teamCode, Integer pageNumber, Integer pageSize, String sortBy, String sortDirection);

  /**
   * Get tasks by department code.
   *
   * @param departmentCode department code
   * @param pageNumber page number
   * @param pageSize page size
   * @param sortBy sort field
   * @param sortDirection sort direction
   * @return page response with tasks
   */
  PageResponse<SearchTaskDto> getTasksByDepartment(
      String departmentCode,
      Integer pageNumber,
      Integer pageSize,
      String sortBy,
      String sortDirection);

  /**
   * Submit task for review (IN_PROGRESS → PENDING).
   *
   * @param id task ID
   * @param request submit review request
   */
  void submitReview(Integer id, SubmitReviewRequest request);

  /**
   * Approve task (PENDING → APPROVED).
   *
   * @param id task ID
   * @param request approve request
   */
  void approve(Integer id, ApproveTaskRequest request);

  /**
   * Reject task (PENDING → REJECTED).
   *
   * @param id task ID
   * @param request reject request
   */
  void reject(Integer id, RejectTaskRequest request);

  /**
   * Cancel task (any status except APPROVED → CANCELLED).
   *
   * @param id task ID
   * @param request cancel request
   */
  void cancel(Integer id, CancelTaskRequest request);

  /**
   * Reopen task (OVERDUE/REJECTED → IN_PROGRESS).
   *
   * @param id task ID
   * @param request reopen request
   */
  void reopen(Integer id, ReopenTaskRequest request);
}
