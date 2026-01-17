package com.uit.sociusmvcapp.task.internal.service;

import static com.uit.sociusmvcapp.shared.utils.TaskValidationUtils.normalizeToEndOfDay;
import static com.uit.sociusmvcapp.shared.utils.TaskValidationUtils.normalizeToStartOfDay;

import com.uit.sociusmvcapp.iam.UserContentProvider;
import com.uit.sociusmvcapp.shared.constants.CommonConstant;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.request.PaginationSearchRequest;
import com.uit.sociusmvcapp.shared.response.PageResponse;
import com.uit.sociusmvcapp.shared.service.ExceptionFactory;
import com.uit.sociusmvcapp.shared.utils.TaskValidationUtils;
import com.uit.sociusmvcapp.task.DepartmentGateway;
import com.uit.sociusmvcapp.task.EmployeeGateway;
import com.uit.sociusmvcapp.task.TaskService;
import com.uit.sociusmvcapp.task.TeamGateway;
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
import com.uit.sociusmvcapp.task.enums.ActivityType;
import com.uit.sociusmvcapp.task.enums.TaskStatus;
import com.uit.sociusmvcapp.task.internal.domain.TaskActivity;
import com.uit.sociusmvcapp.task.internal.dto.ParentTaskContext;
import com.uit.sociusmvcapp.task.internal.repository.TaskRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Implementation of TaskService for task-related operations. */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

  /** Repository for accesing task data. */
  private final TaskRepository taskRepository;

  /** Provider for user content. */
  private final UserContentProvider userContentProvider;

  /** Gateway for employee validation. */
  private final EmployeeGateway employeeGateway;

  /** Gateway for team validation. */
  private final TeamGateway teamGateway;

  /** Gateway for department validation. */
  private final DepartmentGateway departmentGateway;

  /**
   * Create a new task.
   *
   * @param request the request containing task creation details
   */
  @Override
  @Transactional
  public void create(CreateTaskRequest request) {
    // Validate dates
    TaskValidationUtils.validateDueDateInFuture(request.getDueDate());
    TaskValidationUtils.validateStartDateBeforeDueDate(
        request.getStartDate(), request.getDueDate());

    // Validate receiver exists
    employeeGateway.validateEmployeeExists(request.getReceiverId());

    String currentUserId = userContentProvider.getUserContent().getClientId();
    // Validate team and department exist
    teamGateway.validateTeamExists(request.getTeamCode());
    departmentGateway.validateDepartmentExists(request.getDepartmentCode());

    taskRepository.createTask(request, currentUserId);
  }

  /**
   * Create a sub-task.
   *
   * @param parentId parent task ID
   * @param request create task request
   */
  @Override
  @Transactional
  public void createSubTask(Integer parentId, CreateTaskRequest request) {
    // Get parent task DTO
    TaskDto parent = getById(parentId);

    // Validate parent is not a sub-task
    if (parent.getParentId() != null) {
      throw ExceptionFactory.badRequest(MessageConstant.E_TASK_018);
    }

    // Validate parent status is IN_PROGRESS
    if (parent.getStatus() != TaskStatus.IN_PROGRESS) {
      throw ExceptionFactory.badRequest(MessageConstant.E_TASK_003);
    }

    String currentUserId = userContentProvider.getUserContent().getClientId();
    // Validate child dates within parent range
    TaskValidationUtils.validateChildDatesWithinParentRange(
        request.getStartDate(), request.getDueDate(),
        parent.getStartDate(), parent.getDueDate());

    // Validate receiver exists
    employeeGateway.validateEmployeeExists(request.getReceiverId());

    // Create sub-task via repository
    ParentTaskContext parentContext =
        new ParentTaskContext(parentId, parent.getTeamCode(), parent.getDepartmentCode());
    taskRepository.createSubTask(request, currentUserId, parentContext);
  }

  /**
   * Get task by ID.
   *
   * @param id task ID
   * @return task DTO
   */
  @Override
  public TaskDto getById(Integer id) {
    TaskDto task = taskRepository.findDtoById(id);
    if (task == null) {
      throw ExceptionFactory.notFound(MessageConstant.E_TASK_006);
    }
    return task;
  }

  /**
   * Update task.
   *
   * @param id task ID
   * @param request update request
   */
  @Override
  @Transactional
  public TaskDto update(Integer id, UpdateTaskRequest request) {
    TaskDto task = getById(id);

    // Validate receiver if provided
    if (request.getReceiverId() != null) {
      employeeGateway.validateEmployeeExists(request.getReceiverId());
    }

    // Validate date changes
    if (request.getStartDate() != null || request.getDueDate() != null) {
      validateDateUpdate(task, request.getStartDate(), request.getDueDate());
    }

    // Update via repository
    taskRepository.updateTask(id, request);
    return getById(id);
  }

  /**
   * Delete task (soft delete with cascade to children).
   *
   * @param id task ID
   */
  @Override
  @Transactional
  public void delete(Integer id) {
    // Validate task exists
    if (!taskRepository.existsById(id)) {
      throw ExceptionFactory.notFound(MessageConstant.E_TASK_006);
    }

    // If parent task, cascade delete children
    if (taskRepository.hasChildren(id)) {
      taskRepository.cascadeDeleteChildren(id);
    }

    taskRepository.delete(id);
  }

  /**
   * Search tasks with filters and pagination.
   *
   * @param request the pagination search request containing search criteria
   * @return a paginated response of SearchTaskDto matching the search criteria
   */
  @Override
  public PageResponse<SearchTaskDto> search(PaginationSearchRequest<SearchTaskRequest> request) {
    String currentUserId = userContentProvider.getUserContent().getClientId();
    int limit = request.getPageRequest().getPageSize();
    int offset = (request.getPageRequest().getPageNumber() - 1) * limit;
    SearchTaskRequest criteria = request.getCondition();

    long total = taskRepository.count(criteria, currentUserId);
    if (total == CommonConstant.INIT_INDEX) {
      log.info("No tasks found matching the search criteria.");
      return PageResponse.empty();
    }

    List<SearchTaskDto> result =
        taskRepository.search(criteria, request.getSortRequests(), currentUserId, limit, offset);

    return PageResponse.of(result, total, offset, limit);
  }

  /**
   * Get sub-tasks of a parent task.
   *
   * @param parentId parent task ID
   * @return list of sub-task DTOs
   */
  @Override
  public List<SearchTaskDto> getSubTasks(Integer parentId) {
    if (!taskRepository.existsById(parentId)) {
      throw ExceptionFactory.notFound(MessageConstant.E_TASK_006);
    }
    return taskRepository.findSubTasksWithEnrichment(parentId);
  }

  /**
   * Get all activities for a task.
   *
   * @param taskId task ID
   * @return list of activity DTOs with actor names enriched
   */
  @Override
  public List<TaskActivityDto> getActivities(Integer taskId) {
    if (!taskRepository.existsById(taskId)) {
      throw ExceptionFactory.notFound(MessageConstant.E_TASK_006);
    }
    List<TaskActivityDto> activities = taskRepository.getActivities(taskId);

    // Batch enrich actor names via gateway (avoid N+1 query)
    Set<String> actorIds =
        activities.stream()
            .map(TaskActivityDto::getActorId)
            .filter(java.util.Objects::nonNull)
            .collect(java.util.stream.Collectors.toSet());

    if (!actorIds.isEmpty()) {
      Map<String, String> actorNames = employeeGateway.getEmployeeNames(actorIds);
      activities.forEach(activity -> activity.setActorName(actorNames.get(activity.getActorId())));
    }

    return activities;
  }

  /**
   * Get tasks assigned to current user (receiver = me).
   *
   * @param pageNumber page number
   * @param pageSize page size
   * @param sortBy sort field
   * @param sortDirection sort direction
   * @return page response with tasks assigned to current user
   */
  @Override
  public PageResponse<SearchTaskDto> getMyTasks(
      Integer pageNumber, Integer pageSize, String sortBy, String sortDirection) {
    SearchTaskRequest criteria = new SearchTaskRequest();
    criteria.setReceiverId("me");

    return search(buildPaginationRequest(criteria, pageNumber, pageSize, sortBy, sortDirection));
  }

  /**
   * Get tasks assigned by current user (sender = me).
   *
   * @param pageNumber page number
   * @param pageSize page size
   * @param sortBy sort field
   * @param sortDirection sort direction
   * @return page response with tasks assigned by current user
   */
  @Override
  public PageResponse<SearchTaskDto> getAssignedByMe(
      Integer pageNumber, Integer pageSize, String sortBy, String sortDirection) {
    SearchTaskRequest criteria = new SearchTaskRequest();
    criteria.setSenderId("me");

    return search(buildPaginationRequest(criteria, pageNumber, pageSize, sortBy, sortDirection));
  }

  /**
   * Get tasks by team code.
   *
   * @param teamCode team code
   * @param pageNumber page number
   * @param pageSize page size
   * @param sortBy sort field
   * @param sortDirection sort direction
   * @return page response with tasks for the team
   */
  @Override
  public PageResponse<SearchTaskDto> getTasksByTeam(
      String teamCode, Integer pageNumber, Integer pageSize, String sortBy, String sortDirection) {
    SearchTaskRequest criteria = new SearchTaskRequest();
    criteria.setTeamCode(teamCode);

    return search(buildPaginationRequest(criteria, pageNumber, pageSize, sortBy, sortDirection));
  }

  /**
   * Get tasks by department code.
   *
   * @param departmentCode department code
   * @param pageNumber page number
   * @param pageSize page size
   * @param sortBy sort field
   * @param sortDirection sort direction
   * @return page response with tasks for the department
   */
  @Override
  public PageResponse<SearchTaskDto> getTasksByDepartment(
      String departmentCode,
      Integer pageNumber,
      Integer pageSize,
      String sortBy,
      String sortDirection) {
    SearchTaskRequest criteria = new SearchTaskRequest();
    criteria.setDepartmentCode(departmentCode);

    return search(buildPaginationRequest(criteria, pageNumber, pageSize, sortBy, sortDirection));
  }

  /**
   * Build PaginationSearchRequest from individual parameters.
   *
   * @param criteria search criteria
   * @param pageNumber page number
   * @param pageSize page size
   * @param sortBy sort field
   * @param sortDirection sort direction
   * @return pagination search request
   */
  private PaginationSearchRequest<SearchTaskRequest> buildPaginationRequest(
      SearchTaskRequest criteria,
      Integer pageNumber,
      Integer pageSize,
      String sortBy,
      String sortDirection) {

    PaginationSearchRequest<SearchTaskRequest> request = new PaginationSearchRequest<>();
    request.setCondition(criteria);

    com.uit.sociusmvcapp.shared.request.PageRequest pageRequest =
        new com.uit.sociusmvcapp.shared.request.PageRequest();
    pageRequest.setPageNumber(pageNumber);
    pageRequest.setPageSize(pageSize);
    request.setPageRequest(pageRequest);

    if (sortBy != null && sortDirection != null) {
      com.uit.sociusmvcapp.shared.request.SortRequest sortRequest =
          new com.uit.sociusmvcapp.shared.request.SortRequest();
      sortRequest.setSortBy(sortBy);
      sortRequest.setSortDirection(sortDirection);
      request.setSortRequests(List.of(sortRequest));
    }

    return request;
  }

  /**
   * Submit task for review (IN_PROGRESS → PENDING).
   *
   * @param id task ID
   * @param request submit review request
   */
  @Override
  @Transactional
  public void submitReview(Integer id, SubmitReviewRequest request) {
    String currentUserId = userContentProvider.getUserContent().getClientId();
    TaskDto task = getById(id);

    // Validate only receiver can submit
    if (!task.getReceiverId().equals(currentUserId)) {
      throw ExceptionFactory.forbidden(MessageConstant.E_TASK_007);
    }

    if (task.getStatus() != TaskStatus.IN_PROGRESS) {
      throw ExceptionFactory.badRequest(MessageConstant.E_TASK_008);
    }

    taskRepository.updateStatus(id, TaskStatus.PENDING.getCode());
    saveTaskActivity(id, ActivityType.SUBMIT, currentUserId, request.getCompletionNote());
  }

  /**
   * Approve task.
   *
   * @param id task ID
   * @param request approve request
   */
  @Override
  @Transactional
  public void approve(Integer id, ApproveTaskRequest request) {
    String currentUserId = userContentProvider.getUserContent().getClientId();
    validateSenderCanReview(id, currentUserId);

    if (taskRepository.hasChildren(id) && !taskRepository.areAllChildrenApproved(id)) {
      throw ExceptionFactory.badRequest(MessageConstant.E_TASK_011);
    }

    taskRepository.updateStatus(id, TaskStatus.APPROVED.getCode());
    saveTaskActivity(id, ActivityType.APPROVE, currentUserId, request.getReviewNote());
  }

  /**
   * Reject task.
   *
   * @param id task ID
   * @param request reject request
   */
  @Override
  @Transactional
  public void reject(Integer id, RejectTaskRequest request) {
    String currentUserId = userContentProvider.getUserContent().getClientId();
    validateSenderCanReview(id, currentUserId);

    taskRepository.updateStatus(id, TaskStatus.REJECTED.getCode());
    saveTaskActivity(id, ActivityType.REJECT, currentUserId, request.getRejectionReason());
  }

  /**
   * Cancel task (any status except APPROVED → CANCELLED).
   *
   * @param id task ID
   * @param request cancel request
   */
  @Override
  @Transactional
  public void cancel(Integer id, CancelTaskRequest request) {
    String currentUserId = userContentProvider.getUserContent().getClientId();
    TaskDto task = getById(id);

    if (task.getStatus() == TaskStatus.APPROVED) {
      throw ExceptionFactory.badRequest(MessageConstant.E_TASK_014);
    }

    // Validate sender or receiver can cancel
    validateSenderOrReceiverCanAct(task, currentUserId, MessageConstant.E_TASK_015);

    if (taskRepository.hasChildren(id)) {
      taskRepository.cascadeCancelChildren(id);
    }

    taskRepository.updateStatus(id, TaskStatus.CANCELLED.getCode());
    saveTaskActivity(id, ActivityType.CANCEL, currentUserId, request.getCancellationReason());
  }

  /**
   * Reopen task (OVERDUE or REJECTED → IN_PROGRESS).
   *
   * @param id task ID
   * @param request reopen request
   */
  @Override
  @Transactional
  public void reopen(Integer id, ReopenTaskRequest request) {
    String currentUserId = userContentProvider.getUserContent().getClientId();
    TaskDto task = getById(id);

    // Validate status is OVERDUE or REJECTED
    if (task.getStatus() != TaskStatus.OVERDUE && task.getStatus() != TaskStatus.REJECTED) {
      throw ExceptionFactory.badRequest(MessageConstant.E_TASK_016);
    }

    // Validate sender or receiver can reopen
    validateSenderOrReceiverCanAct(task, currentUserId, MessageConstant.E_TASK_017);

    taskRepository.updateStatus(id, TaskStatus.IN_PROGRESS.getCode());
    saveTaskActivity(id, ActivityType.REOPEN, currentUserId, request.getReopenReason());
  }

  // ========== Private Helper Methods ==========

  /**
   * Validate that only sender can review (approve/reject) the task.
   *
   * @param id task ID
   * @param currentUserId current user ID
   * @return task DTO
   */
  private TaskDto validateSenderCanReview(Integer id, String currentUserId) {
    TaskDto task = getById(id);

    // Validate only sender can review
    if (!task.getSenderId().equals(currentUserId)) {
      throw ExceptionFactory.forbidden(MessageConstant.E_TASK_009);
    }

    // Validate task status is PENDING
    if (task.getStatus() != TaskStatus.PENDING) {
      throw ExceptionFactory.badRequest(MessageConstant.E_TASK_010);
    }

    return task;
  }

  /**
   * Validate that sender or receiver can perform action.
   *
   * @param task task DTO
   * @param currentUserId current user ID
   * @param errorCode error code if validation fails
   */
  private void validateSenderOrReceiverCanAct(
      TaskDto task, String currentUserId, String errorCode) {
    // Validate sender or receiver can act
    if (!task.getSenderId().equals(currentUserId) && !task.getReceiverId().equals(currentUserId)) {
      throw ExceptionFactory.forbidden(errorCode);
    }
  }

  /**
   * Save task activity.
   *
   * @param taskId task ID
   * @param activityType activity type
   * @param actorId actor ID
   * @param note activity note
   */
  private void saveTaskActivity(
      Integer taskId, ActivityType activityType, String actorId, String note) {
    TaskActivity activity =
        TaskActivity.builder()
            .taskId(taskId)
            .activityType(activityType)
            .actorId(actorId)
            .note(note)
            .build();
    taskRepository.saveActivity(activity);
  }

  /**
   * Validate date update for task.
   *
   * @param task the task to validate
   * @param newStartDate new start date (nullable)
   * @param newDueDate new due date (nullable)
   */
  private void validateDateUpdate(TaskDto task, LocalDate newStartDate, LocalDate newDueDate) {
    LocalDateTime startDateTime =
        newStartDate != null ? normalizeToStartOfDay(newStartDate) : task.getStartDate();
    LocalDateTime dueDateTime =
        newDueDate != null ? normalizeToEndOfDay(newDueDate) : task.getDueDate();

    // Validate start < due
    if (startDateTime.isAfter(dueDateTime)) {
      throw ExceptionFactory.badRequest(MessageConstant.E_TASK_002);
    }

    // If child task, validate against parent dates
    if (task.getParentId() != null) {
      TaskDto parent = getById(task.getParentId());

      if (startDateTime.isBefore(parent.getStartDate())) {
        throw ExceptionFactory.badRequest(MessageConstant.E_TASK_004);
      }

      if (dueDateTime.isAfter(parent.getDueDate())) {
        throw ExceptionFactory.badRequest(MessageConstant.E_TASK_005);
      }
    }
  }
}
