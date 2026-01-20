package com.uit.sociusmvcapp.task.internal.scheduler;

import com.uit.sociusmvcapp.task.internal.persistence.TaskMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled tasks for Task module.
 *
 * <p>Handles periodic maintenance tasks such as marking overdue tasks.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskScheduledJobs {

  private final TaskMapper taskMapper;

  /**
   * Mark tasks as overdue if their due date has passed.
   *
   * <p>Runs daily at midnight (00:00:00) to check and update tasks that are past their due date but
   * still in IN_PROGRESS or PENDING status.
   *
   * <p>Cron expression: "0 0 0 * * *" = daily at midnight (00:00:00)
   */
  @Scheduled(cron = "0 0 0 * * *")
  public void markOverdueTasks() {
    log.info("Starting scheduled task: Mark overdue tasks");

    try {
      int updatedCount = taskMapper.markOverdueTasks();
      log.info("Completed scheduled task: Mark overdue tasks. Updated {} task(s)", updatedCount);
    } catch (Exception e) {
      log.error("Error occurred while marking overdue tasks", e);
    }
  }
}
