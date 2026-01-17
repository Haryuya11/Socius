package com.uit.sociusmvcapp.task;

import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.request.PaginationSearchRequest;
import com.uit.sociusmvcapp.shared.response.PageResponse;
import com.uit.sociusmvcapp.shared.response.Response;
import com.uit.sociusmvcapp.shared.service.I18nService;
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
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** TaskController handles HTTP requests related to task operations. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class TaskController {

  /** I18nService for internationalization messages. */
  private final I18nService i18nService;

  /** TaskService for task-related operations. */
  private final TaskService taskService;

  /**
   * Create a new task.
   *
   * @param request create task request
   * @return ResponseEntity indicating the result
   */
  @PostMapping
  public ResponseEntity<Response> create(@Valid @RequestBody CreateTaskRequest request) {
    taskService.create(request);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.CREATED.value())
            .code(MessageConstant.S_TASK_001)
            .message(i18nService.getMessage(MessageConstant.S_TASK_001))
            .build();
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * Create a sub-task.
   *
   * @param parentId parent task ID
   * @param request create task request
   * @return ResponseEntity indicating the result
   */
  @PostMapping("/{parentId}/sub-tasks")
  public ResponseEntity<Response> createSubTask(
      @PathVariable Integer parentId, @Valid @RequestBody CreateTaskRequest request) {
    taskService.createSubTask(parentId, request);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.CREATED.value())
            .code(MessageConstant.S_TASK_002)
            .message(i18nService.getMessage(MessageConstant.S_TASK_002))
            .build();
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * Get task by ID.
   *
   * @param id task ID
   * @return ResponseEntity containing the task
   */
  @GetMapping("/{id}")
  public ResponseEntity<Response> getById(@PathVariable Integer id) {
    TaskDto task = taskService.getById(id);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_TASK_003)
            .message(i18nService.getMessage(MessageConstant.S_TASK_003))
            .data(task)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Update task.
   *
   * @param id task ID
   * @param request update request
   * @return ResponseEntity containing the updated task
   */
  @PutMapping("/{id}")
  public ResponseEntity<Response> update(
      @PathVariable Integer id, @Valid @RequestBody UpdateTaskRequest request) {
    TaskDto task = taskService.update(id, request);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_TASK_004)
            .message(i18nService.getMessage(MessageConstant.S_TASK_004))
            .data(task)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Delete task (soft delete with cascade).
   *
   * @param id task ID
   * @return ResponseEntity indicating the result
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Response> delete(@PathVariable Integer id) {
    taskService.delete(id);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_TASK_005)
            .message(i18nService.getMessage(MessageConstant.S_TASK_005))
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Search tasks with filters and pagination.
   *
   * @param request the pagination search request containing search criteria
   * @return ResponseEntity containing paginated task data
   */
  @PostMapping("/search")
  public ResponseEntity<Response> search(
      @RequestBody PaginationSearchRequest<SearchTaskRequest> request) {
    PageResponse<SearchTaskDto> tasks = taskService.search(request);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_TASK_006)
            .message(i18nService.getMessage(MessageConstant.S_TASK_006))
            .data(tasks)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Get sub-tasks of a parent task.
   *
   * @param parentId parent task ID
   * @return ResponseEntity containing list of sub-tasks
   */
  @GetMapping("/{parentId}/sub-tasks")
  public ResponseEntity<Response> getSubTasks(@PathVariable Integer parentId) {
    List<SearchTaskDto> subTasks = taskService.getSubTasks(parentId);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_TASK_007)
            .message(i18nService.getMessage(MessageConstant.S_TASK_007))
            .data(subTasks)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Get all activities/notes for a task.
   *
   * @param id task ID
   * @return ResponseEntity containing list of task activities
   */
  @GetMapping("/{id}/activities")
  public ResponseEntity<Response> getActivities(@PathVariable Integer id) {
    List<TaskActivityDto> activities = taskService.getActivities(id);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_TASK_006)
            .message(i18nService.getMessage(MessageConstant.S_TASK_006))
            .data(activities)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Get tasks assigned to me (I am the receiver).
   *
   * @param pageNumber page number (default: 1)
   * @param pageSize page size (default: 10)
   * @param sortBy sort field (default: createdAt)
   * @param sortDirection sort direction (default: DESC)
   * @return ResponseEntity containing paginated tasks assigned to current user
   */
  @GetMapping("/my-tasks")
  public ResponseEntity<Response> getMyTasks(
      @RequestParam(defaultValue = "1") Integer pageNumber,
      @RequestParam(defaultValue = "10") Integer pageSize,
      @RequestParam(required = false) String sortBy,
      @RequestParam(required = false) String sortDirection) {

    PageResponse<SearchTaskDto> tasks =
        taskService.getMyTasks(pageNumber, pageSize, sortBy, sortDirection);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_TASK_006)
            .message(i18nService.getMessage(MessageConstant.S_TASK_006))
            .data(tasks)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Get tasks assigned by me (I am the sender).
   *
   * @param pageNumber page number (default: 1)
   * @param pageSize page size (default: 10)
   * @param sortBy sort field (default: createdAt)
   * @param sortDirection sort direction (default: DESC)
   * @return ResponseEntity containing paginated tasks assigned by current user
   */
  @GetMapping("/assigned-by-me")
  public ResponseEntity<Response> getAssignedByMe(
      @RequestParam(defaultValue = "1") Integer pageNumber,
      @RequestParam(defaultValue = "10") Integer pageSize,
      @RequestParam(required = false) String sortBy,
      @RequestParam(required = false) String sortDirection) {

    PageResponse<SearchTaskDto> tasks =
        taskService.getAssignedByMe(pageNumber, pageSize, sortBy, sortDirection);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_TASK_006)
            .message(i18nService.getMessage(MessageConstant.S_TASK_006))
            .data(tasks)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Get tasks by team code.
   *
   * @param teamCode team code
   * @param pageNumber page number (default: 1)
   * @param pageSize page size (default: 10)
   * @param sortBy sort field (default: createdAt)
   * @param sortDirection sort direction (default: DESC)
   * @return ResponseEntity containing paginated tasks for the team
   */
  @GetMapping("/team/{teamCode}")
  public ResponseEntity<Response> getTasksByTeam(
      @PathVariable String teamCode,
      @RequestParam(defaultValue = "1") Integer pageNumber,
      @RequestParam(defaultValue = "10") Integer pageSize,
      @RequestParam(required = false) String sortBy,
      @RequestParam(required = false) String sortDirection) {

    PageResponse<SearchTaskDto> tasks =
        taskService.getTasksByTeam(teamCode, pageNumber, pageSize, sortBy, sortDirection);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_TASK_006)
            .message(i18nService.getMessage(MessageConstant.S_TASK_006))
            .data(tasks)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Get tasks by department code.
   *
   * @param departmentCode department code
   * @param pageNumber page number (default: 1)
   * @param pageSize page size (default: 10)
   * @param sortBy sort field (default: createdAt)
   * @param sortDirection sort direction (default: DESC)
   * @return ResponseEntity containing paginated tasks for the department
   */
  @GetMapping("/department/{departmentCode}")
  public ResponseEntity<Response> getTasksByDepartment(
      @PathVariable String departmentCode,
      @RequestParam(defaultValue = "1") Integer pageNumber,
      @RequestParam(defaultValue = "10") Integer pageSize,
      @RequestParam(required = false) String sortBy,
      @RequestParam(required = false) String sortDirection) {

    PageResponse<SearchTaskDto> tasks =
        taskService.getTasksByDepartment(
            departmentCode, pageNumber, pageSize, sortBy, sortDirection);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_TASK_006)
            .message(i18nService.getMessage(MessageConstant.S_TASK_006))
            .data(tasks)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Submit task for review.
   *
   * @param id task ID
   * @param request submit review request
   * @return ResponseEntity indicating the result
   */
  @PutMapping("/{id}/submit-review")
  public ResponseEntity<Response> submitReview(
      @PathVariable Integer id, @Valid @RequestBody SubmitReviewRequest request) {
    taskService.submitReview(id, request);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_TASK_008)
            .message(i18nService.getMessage(MessageConstant.S_TASK_008))
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Approve task.
   *
   * @param id task ID
   * @param request approve request
   * @return ResponseEntity indicating the result
   */
  @PutMapping("/{id}/approve")
  public ResponseEntity<Response> approve(
      @PathVariable Integer id, @Valid @RequestBody ApproveTaskRequest request) {
    taskService.approve(id, request);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_TASK_009)
            .message(i18nService.getMessage(MessageConstant.S_TASK_009))
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Reject task.
   *
   * @param id task ID
   * @param request reject request
   * @return ResponseEntity indicating the result
   */
  @PutMapping("/{id}/reject")
  public ResponseEntity<Response> reject(
      @PathVariable Integer id, @Valid @RequestBody RejectTaskRequest request) {
    taskService.reject(id, request);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_TASK_010)
            .message(i18nService.getMessage(MessageConstant.S_TASK_010))
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Cancel task.
   *
   * @param id task ID
   * @param request cancel request
   * @return ResponseEntity indicating the result
   */
  @PutMapping("/{id}/cancel")
  public ResponseEntity<Response> cancel(
      @PathVariable Integer id, @RequestBody CancelTaskRequest request) {
    taskService.cancel(id, request);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_TASK_011)
            .message(i18nService.getMessage(MessageConstant.S_TASK_011))
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Reopen task.
   *
   * @param id task ID
   * @param request reopen request
   * @return ResponseEntity indicating the result
   */
  @PutMapping("/{id}/reopen")
  public ResponseEntity<Response> reopen(
      @PathVariable Integer id, @Valid @RequestBody ReopenTaskRequest request) {
    taskService.reopen(id, request);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_TASK_012)
            .message(i18nService.getMessage(MessageConstant.S_TASK_012))
            .build();
    return ResponseEntity.ok(response);
  }
}
