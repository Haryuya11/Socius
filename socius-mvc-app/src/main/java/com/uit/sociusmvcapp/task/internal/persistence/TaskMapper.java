package com.uit.sociusmvcapp.task.internal.persistence;

import com.uit.sociusmvcapp.shared.request.SortRequest;
import com.uit.sociusmvcapp.task.dto.request.SearchTaskRequest;
import com.uit.sociusmvcapp.task.internal.domain.Task;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** MyBatis mapper interface for Task entity. */
@Mapper
public interface TaskMapper {

  /**
   * Insert a new task.
   *
   * @param task task to insert
   */
  void insert(Task task);

  /**
   * Update task information.
   *
   * @param task task to update
   */
  void update(Task task);

  /**
   * Soft delete a task by ID.
   *
   * @param id task ID
   */
  void softDelete(@Param("id") Integer id);

  /**
   * Find task by ID.
   *
   * @param id task ID
   * @return task entity
   */
  Task findById(@Param("id") Integer id);

  /**
   * Check if task exists by ID.
   *
   * @param id task ID
   * @return true if task exists
   */
  boolean existsById(@Param("id") Integer id);

  /**
   * Find all tasks by parent ID.
   *
   * @param parentId parent task ID
   * @return list of tasks
   */
  List<Task> findByParentId(@Param("parentId") Integer parentId);

  /**
   * Search tasks with filters and pagination.
   *
   * @param request search criteria
   * @param sorts sorting options
   * @param currentUserId current user ID
   * @param offset pagination offset
   * @param limit pagination limit
   * @return list of task entities
   */
  List<Task> search(
      @Param("request") SearchTaskRequest request,
      @Param("sorts") List<SortRequest> sorts,
      @Param("currentUserId") String currentUserId,
      @Param("offset") int offset,
      @Param("limit") int limit);

  /**
   * Count tasks matching search criteria.
   *
   * @param request search criteria
   * @param currentUserId current user ID
   * @return total count
   */
  long count(
      @Param("request") SearchTaskRequest request, @Param("currentUserId") String currentUserId);

  /**
   * Update task status.
   *
   * @param id task ID
   * @param statusCode new status code
   */
  void updateStatus(@Param("id") Integer id, @Param("statusCode") int statusCode);

  /**
   * Mark overdue tasks (scheduled job).
   *
   * @return number of tasks marked as overdue
   */
  int markOverdueTasks();

  /**
   * Check if task has children.
   *
   * @param taskId task ID
   * @return true if has children
   */
  boolean hasChildren(@Param("taskId") Integer taskId);

  /**
   * Count children by parent ID.
   *
   * @param parentId parent task ID
   * @return count of children
   */
  int countChildren(@Param("parentId") Integer parentId);

  /**
   * Check if all children are approved.
   *
   * @param parentId parent task ID
   * @return true if all children are approved
   */
  boolean areAllChildrenApproved(@Param("parentId") Integer parentId);

  /**
   * Cascade soft delete children.
   *
   * @param parentId parent task ID
   */
  void cascadeDeleteChildren(@Param("parentId") Integer parentId);

  /**
   * Cascade cancel children.
   *
   * @param parentId parent task ID
   */
  void cascadeCancelChildren(@Param("parentId") Integer parentId);
}
