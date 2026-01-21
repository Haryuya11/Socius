package com.uit.sociusmvcapp.task.internal.service;

import static com.uit.sociusmvcapp.shared.utils.TaskValidationUtils.normalizeToEndOfDay;
import static com.uit.sociusmvcapp.shared.utils.TaskValidationUtils.normalizeToStartOfDay;

import com.uit.sociusmvcapp.iam.PermissionSecurityService;
import com.uit.sociusmvcapp.iam.UserContentProvider;
import com.uit.sociusmvcapp.shared.constants.AuthConstant;
import com.uit.sociusmvcapp.shared.constants.CommonConstant;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.event.NotificationMultiSendRequest;
import com.uit.sociusmvcapp.shared.event.NotificationSendEvent;
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
import com.uit.sociusmvcapp.task.internal.constants.TaskConstant;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Implementation of TaskService for task-related operations. */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

  private static final String TASKS_PATH = "/tasks?taskId=";

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

  /** Service for checking user permissions. */
  private final PermissionSecurityService permissionSecurityService;

  /** Event publisher for notifications. */
  private final ApplicationEventPublisher eventPublisher;

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

    // Validate team and department exist
    teamGateway.validateTeamExists(request.getTeamCode());
    departmentGateway.validateDepartmentExists(request.getDepartmentCode());

    // Validate user has permission to create task in this scope
    validateCreatePermission(request.getTeamCode(), request.getDepartmentCode());

    String currentUserId = userContentProvider.getUserContent().getClientId();
    Integer taskId = taskRepository.createTask(request, currentUserId);

    Map<String, String> params =
        Map.of(
            "TASK_ID", taskId.toString(),
            "TASK_TITLE", request.getTitle());
    // Send notifications after task creation
    if (!request.getReceiverId().equals(currentUserId)) {
      eventPublisher.publishEvent(
          new NotificationSendEvent(
              this,
              request.getReceiverId(),
              "S_TASK_TITLE_001",
              "S_TASK_CONTENT_001",
              params,
              TASKS_PATH + taskId));
    }

    // Notify sender (creator)
    eventPublisher.publishEvent(
        new NotificationSendEvent(
            this,
            currentUserId,
            "S_TASK_TITLE_002",
            "S_TASK_CONTENT_002",
            params,
            TASKS_PATH + taskId));
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

    // Validate user has permission to create task in parent's scope
    validateCreatePermission(parent.getTeamCode(), parent.getDepartmentCode());

    // Validate child dates within parent range
    TaskValidationUtils.validateChildDatesWithinParentRange(
        request.getStartDate(), request.getDueDate(),
        parent.getStartDate(), parent.getDueDate());

    // Validate receiver exists
    employeeGateway.validateEmployeeExists(request.getReceiverId());

    // Create sub-task via repository
    String currentUserId = userContentProvider.getUserContent().getClientId();
    ParentTaskContext parentContext =
        new ParentTaskContext(parentId, parent.getTeamCode(), parent.getDepartmentCode());
    Integer subTaskId = taskRepository.createSubTask(request, currentUserId, parentContext);

    Map<String, String> params =
        Map.of(
            "TASK_ID", subTaskId.toString(),
            "TASK_TITLE", request.getTitle());
    // Send notifications after sub-task creation
    if (!request.getReceiverId().equals(currentUserId)) {
      eventPublisher.publishEvent(
          new NotificationSendEvent(
              this,
              request.getReceiverId(),
              "S_TASK_TITLE_003",
              "S_TASK_CONTENT_003",
              params,
              TASKS_PATH + subTaskId));
    }

    // Notify sender (creator)
    eventPublisher.publishEvent(
        new NotificationSendEvent(
            this,
            currentUserId,
            "S_TASK_TITLE_004",
            "S_TASK_CONTENT_004",
            params,
            TASKS_PATH + subTaskId));
  }

  /**
   * Get task by ID.
   *
   * @param id task ID
   * @return task DTO
   */
  @Override
  public TaskDto getById(Integer id) {
    TaskDto task = findTaskById(id);
    String currentUserId = userContentProvider.getUserContent().getClientId();
    validateViewPermission(task, currentUserId);
    return task;
  }

  /**
   * Internal method to find task by ID without permission validation. Used by other service methods
   * that need to fetch task for validation purposes.
   *
   * @param id task ID
   * @return task DTO
   */
  private TaskDto findTaskById(Integer id) {
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
    TaskDto task = findTaskById(id);
    String currentUserId = userContentProvider.getUserContent().getClientId();

    // Validate update permission
    validateUpdatePermission(task, currentUserId);

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
    TaskDto updatedTask = getById(id);

    // Send notifications after task update
    Set<String> recipients = new java.util.HashSet<>();
    recipients.add(updatedTask.getSenderId());
    recipients.add(updatedTask.getReceiverId());
    recipients.remove(currentUserId);

    Map<String, String> params =
        Map.of(
            "TASK_ID", id.toString(),
            "TASK_TITLE", updatedTask.getTitle());

    if (!recipients.isEmpty()) {
      eventPublisher.publishEvent(
          new NotificationMultiSendRequest(
              this,
              new java.util.ArrayList<>(recipients),
              "S_TASK_TITLE_005",
              "S_TASK_CONTENT_005",
              params,
              TASKS_PATH + id));
    }

    if (currentUserId.equals(updatedTask.getSenderId())
        || currentUserId.equals(updatedTask.getReceiverId())) {
      eventPublisher.publishEvent(
          new NotificationSendEvent(
              this,
              currentUserId,
              "S_TASK_TITLE_006",
              "S_TASK_CONTENT_006",
              params,
              TASKS_PATH + id));
    }

    return updatedTask;
  }

  /**
   * Delete task (soft delete with cascade to children).
   *
   * @param id task ID
   */
  @Override
  @Transactional
  public void delete(Integer id) {
    TaskDto task = findTaskById(id);
    String currentUserId = userContentProvider.getUserContent().getClientId();

    // Validate delete permission
    validateDeletePermission(task, currentUserId);

    // If parent task, cascade delete children
    if (taskRepository.hasChildren(id)) {
      taskRepository.cascadeDeleteChildren(id);
    }

    taskRepository.delete(id);

    // Send notifications after task deletion
    Set<String> recipients = new java.util.HashSet<>();
    recipients.add(task.getSenderId());
    recipients.add(task.getReceiverId());

    Map<String, String> deleteParams =
        Map.of(
            "TASK_ID", id.toString(),
            "TASK_TITLE", task.getTitle());
    eventPublisher.publishEvent(
        new NotificationMultiSendRequest(
            this,
            new java.util.ArrayList<>(recipients),
            "S_TASK_TITLE_007",
            "S_TASK_CONTENT_007",
            deleteParams,
            "/tasks"));
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
    // Validate user belongs to the team or is SYS_ADMIN
    validateTeamAccess(teamCode);

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
    // Validate user belongs to the department or is SYS_ADMIN
    validateDepartmentAccess(departmentCode);

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

    // Send notifications after submit
    Map<String, String> params =
        Map.of(
            "TASK_ID", id.toString(),
            "TASK_TITLE", task.getTitle());

    // Notify sender (reviewer) - task needs review
    eventPublisher.publishEvent(
        new NotificationSendEvent(
            this,
            task.getSenderId(),
            "S_TASK_TITLE_008",
            "S_TASK_CONTENT_008",
            params,
            "/tasks?taskId=" + id));

    // Notify receiver (submitter) - submission confirmation
    eventPublisher.publishEvent(
        new NotificationSendEvent(
            this,
            task.getReceiverId(),
            "S_TASK_TITLE_009",
            "S_TASK_CONTENT_009",
            params,
            "/tasks?taskId=" + id));
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

    if (taskRepository.hasChildren(id) && !taskRepository.areAllChildrenApproved(id)) {
      throw ExceptionFactory.badRequest(MessageConstant.E_TASK_011);
    }

    TaskDto task = validateSenderCanReview(id, currentUserId);
    taskRepository.updateStatus(id, TaskStatus.APPROVED.getCode());
    saveTaskActivity(id, ActivityType.APPROVE, currentUserId, request.getReviewNote());

    // Send notifications after approve
    Set<String> recipients = new java.util.HashSet<>();
    recipients.add(task.getSenderId());
    recipients.add(task.getReceiverId());

    Map<String, String> params =
        Map.of(
            "TASK_ID", id.toString(),
            "TASK_TITLE", task.getTitle());
    eventPublisher.publishEvent(
        new NotificationMultiSendRequest(
            this,
            new java.util.ArrayList<>(recipients),
            "S_TASK_TITLE_010",
            "S_TASK_CONTENT_010",
            params,
            "/tasks?taskId=" + id));
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
    TaskDto task = validateSenderCanReview(id, currentUserId);

    taskRepository.updateStatus(id, TaskStatus.REJECTED.getCode());
    saveTaskActivity(id, ActivityType.REJECT, currentUserId, request.getRejectionReason());

    // Send notifications after reject
    Set<String> recipients = new java.util.HashSet<>();
    recipients.add(task.getSenderId());
    recipients.add(task.getReceiverId());

    Map<String, String> params =
        Map.of(
            "TASK_ID", id.toString(),
            "TASK_TITLE", task.getTitle());
    eventPublisher.publishEvent(
        new NotificationMultiSendRequest(
            this,
            new java.util.ArrayList<>(recipients),
            "S_TASK_TITLE_011",
            "S_TASK_CONTENT_011",
            params,
            "/tasks?taskId=" + id));
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

    // Send notifications after cancel
    Set<String> recipients = new java.util.HashSet<>();
    recipients.add(task.getSenderId());
    recipients.add(task.getReceiverId());

    Map<String, String> params =
        Map.of(
            "TASK_ID", id.toString(),
            "TASK_TITLE", task.getTitle());
    eventPublisher.publishEvent(
        new NotificationMultiSendRequest(
            this,
            new java.util.ArrayList<>(recipients),
            "S_TASK_TITLE_012",
            "S_TASK_CONTENT_012",
            params,
            "/tasks?taskId=" + id));
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

    // Send notifications after reopen
    Set<String> recipients = new java.util.HashSet<>();
    recipients.add(task.getSenderId());
    recipients.add(task.getReceiverId());

    Map<String, String> params =
        Map.of(
            "TASK_ID", id.toString(),
            "TASK_TITLE", task.getTitle());
    eventPublisher.publishEvent(
        new NotificationMultiSendRequest(
            this,
            new java.util.ArrayList<>(recipients),
            "S_TASK_TITLE_013",
            "S_TASK_CONTENT_013",
            params,
            "/tasks?taskId=" + id));
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

  // ========== Authorization Helper Methods ==========

  /**
   * Validate that the current user has permission to view the task. User can view a task if:
   *
   * <ol>
   *   <li>User is a system admin (has system.full permission)
   *   <li>User is the sender or receiver of the task
   *   <li>User belongs to the same team as the task (all team members can view team tasks)
   *   <li>User belongs to the same department as the task (all department members can view
   *       department tasks)
   * </ol>
   *
   * <p>Note: View permission only requires membership in the same scope. Update and delete
   * operations require specific permissions.
   *
   * @param task the task to check
   * @param currentUserId the current user's client ID
   */
  private void validateViewPermission(TaskDto task, String currentUserId) {
    // SYS_ADMIN can view all tasks
    if (permissionSecurityService.isSystemAdmin()) {
      return;
    }

    // Sender or receiver can always view their own tasks
    if (currentUserId.equals(task.getSenderId()) || currentUserId.equals(task.getReceiverId())) {
      return;
    }

    // Check if user belongs to the same team (all team members can view team tasks)
    if (task.getTeamCode() != null
        && permissionSecurityService.belongsToScope(AuthConstant.SCOPE_TEAM, task.getTeamCode())) {
      return;
    }

    // Check if user belongs to the same department (all department members can view dept tasks)
    if (task.getDepartmentCode() != null
        && permissionSecurityService.belongsToScope(
            AuthConstant.SCOPE_DEPARTMENT, task.getDepartmentCode())) {
      return;
    }

    throw ExceptionFactory.forbidden(MessageConstant.E_TASK_019);
  }

  /**
   * Validate that the current user has permission to update the task. User can update a task if:
   *
   * <ol>
   *   <li>User is a system admin (has system.full permission)
   *   <li>User is the sender of the task (task creators can update their tasks)
   *   <li>User belongs to the same team as the task and has task.update permission
   *   <li>User belongs to the same department as the task and has task.update permission
   * </ol>
   *
   * @param task the task to check
   * @param currentUserId the current user's client ID
   */
  private void validateUpdatePermission(TaskDto task, String currentUserId) {
    // SYS_ADMIN can update all tasks
    if (permissionSecurityService.isSystemAdmin()) {
      return;
    }

    // Sender can always update their own tasks
    if (currentUserId.equals(task.getSenderId())) {
      return;
    }

    // Check if user belongs to the same team and has update permission
    if (task.getTeamCode() != null
        && permissionSecurityService.hasScopedPermission(
            AuthConstant.SCOPE_TEAM, task.getTeamCode(), TaskConstant.PERMISSION_UPDATE)) {
      return;
    }

    // Check if user belongs to the same department and has update permission
    if (task.getDepartmentCode() != null
        && permissionSecurityService.hasScopedPermission(
            AuthConstant.SCOPE_DEPARTMENT,
            task.getDepartmentCode(),
            TaskConstant.PERMISSION_UPDATE)) {
      return;
    }

    throw ExceptionFactory.forbidden(MessageConstant.E_TASK_020);
  }

  /**
   * Validate that the current user has permission to delete the task. User can delete a task if:
   *
   * <ol>
   *   <li>User is a system admin (has system.full permission)
   *   <li>User is the sender of the task (task creators can delete their tasks)
   *   <li>User belongs to the same team as the task and has task.delete permission
   *   <li>User belongs to the same department as the task and has task.delete permission
   * </ol>
   *
   * @param task the task to check
   * @param currentUserId the current user's client ID
   */
  private void validateDeletePermission(TaskDto task, String currentUserId) {
    // SYS_ADMIN can delete all tasks
    if (permissionSecurityService.isSystemAdmin()) {
      return;
    }

    // Sender can always delete their own tasks
    if (currentUserId.equals(task.getSenderId())) {
      return;
    }

    // Check if user belongs to the same team and has delete permission
    if (task.getTeamCode() != null
        && permissionSecurityService.hasScopedPermission(
            AuthConstant.SCOPE_TEAM, task.getTeamCode(), TaskConstant.PERMISSION_DELETE)) {
      return;
    }

    // Check if user belongs to the same department and has delete permission
    if (task.getDepartmentCode() != null
        && permissionSecurityService.hasScopedPermission(
            AuthConstant.SCOPE_DEPARTMENT,
            task.getDepartmentCode(),
            TaskConstant.PERMISSION_DELETE)) {
      return;
    }

    throw ExceptionFactory.forbidden(MessageConstant.E_TASK_021);
  }

  /**
   * Validate that the current user has permission to create a task in the specified team. User can
   * create a task if:
   *
   * <ol>
   *   <li>User is a system admin (has system.full permission)
   *   <li>User belongs to the specified team and has task.create permission
   *   <li>User belongs to the specified department and has task.create permission
   * </ol>
   *
   * @param teamCode the team code where the task will be created
   * @param departmentCode the department code where the task will be created
   */
  private void validateCreatePermission(String teamCode, String departmentCode) {
    // SYS_ADMIN can create tasks anywhere
    if (permissionSecurityService.isSystemAdmin()) {
      return;
    }

    // Check if user has create permission in the team
    if (teamCode != null
        && permissionSecurityService.hasScopedPermission(
            AuthConstant.SCOPE_TEAM, teamCode, TaskConstant.PERMISSION_CREATE)) {
      return;
    }

    // Check if user has create permission in the department
    if (departmentCode != null
        && permissionSecurityService.hasScopedPermission(
            AuthConstant.SCOPE_DEPARTMENT, departmentCode, TaskConstant.PERMISSION_CREATE)) {
      return;
    }

    throw ExceptionFactory.forbidden(MessageConstant.E_TASK_022);
  }

  /**
   * Validate that the current user has access to view tasks in the specified team. User can access
   * team tasks if:
   *
   * <ol>
   *   <li>User is a system admin (has system.full permission)
   *   <li>User belongs to the specified team
   * </ol>
   *
   * @param teamCode the team code to check
   */
  private void validateTeamAccess(String teamCode) {
    // SYS_ADMIN can view all teams' tasks
    if (permissionSecurityService.isSystemAdmin()) {
      return;
    }

    // User must belong to the team
    if (!permissionSecurityService.belongsToScope(AuthConstant.SCOPE_TEAM, teamCode)) {
      throw ExceptionFactory.forbidden(MessageConstant.E_TASK_019);
    }
  }

  /**
   * Validate that the current user has access to view tasks in the specified department. User can
   * access department tasks if:
   *
   * <ol>
   *   <li>User is a system admin (has system.full permission)
   *   <li>User belongs to the specified department
   * </ol>
   *
   * @param departmentCode the department code to check
   */
  private void validateDepartmentAccess(String departmentCode) {
    // SYS_ADMIN can view all departments' tasks
    if (permissionSecurityService.isSystemAdmin()) {
      return;
    }

    // User must belong to the department
    if (!permissionSecurityService.belongsToScope(AuthConstant.SCOPE_DEPARTMENT, departmentCode)) {
      throw ExceptionFactory.forbidden(MessageConstant.E_TASK_019);
    }
  }
}
