package com.uit.sociusmvcapp.task.internal.persistence;

import com.uit.sociusmvcapp.task.dto.TaskActivityDto;
import com.uit.sociusmvcapp.task.internal.domain.TaskActivity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** MyBatis mapper interface for TaskActivity entity. */
@Mapper
public interface TaskActivityMapper {

  /**
   * Insert a new task activity.
   *
   * @param activity activity to insert
   */
  void insert(TaskActivity activity);

  /**
   * Find all activities for a task.
   *
   * @param taskId task ID
   * @return list of activity DTOs
   */
  List<TaskActivityDto> findByTaskId(@Param("taskId") Integer taskId);
}
