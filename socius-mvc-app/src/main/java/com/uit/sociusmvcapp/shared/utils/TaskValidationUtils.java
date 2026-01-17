package com.uit.sociusmvcapp.shared.utils;

import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.service.ExceptionFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/** Utility class for task-related validations. */
public final class TaskValidationUtils {

  private TaskValidationUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  /**
   * Validates that the due date is today or in the future.
   *
   * @param dueDate the due date to validate
   * @throws BusinessException if due date is in the past
   */
  public static void validateDueDateInFuture(LocalDate dueDate) {
    if (dueDate.isBefore(LocalDate.now())) {
      throw ExceptionFactory.badRequest(MessageConstant.E_TASK_001);
    }
  }

  /**
   * Validate that start date is before or equal to due date.
   *
   * @param startDate start date
   * @param dueDate due date
   * @throws BusinessException if start date is after due date
   */
  public static void validateStartDateBeforeDueDate(LocalDate startDate, LocalDate dueDate) {
    if (startDate.isAfter(dueDate)) {
      throw ExceptionFactory.badRequest(MessageConstant.E_TASK_002);
    }
  }

  /**
   * Validates that child task dates are within parent task date range.
   *
   * @param childStartDate child task start date
   * @param childDueDate child task due date
   * @param parentStartDate parent task start date
   * @param parentDueDate parent task due date
   * @throws BusinessException if child dates are outside parent range
   */
  public static void validateChildDatesWithinParentRange(
      LocalDate childStartDate,
      LocalDate childDueDate,
      LocalDateTime parentStartDate,
      LocalDateTime parentDueDate) {

    LocalDateTime childStartDateTime = normalizeToStartOfDay(childStartDate);
    LocalDateTime childDueDateTime = normalizeToEndOfDay(childDueDate);

    if (childStartDateTime.isBefore(parentStartDate)) {
      throw ExceptionFactory.badRequest(MessageConstant.E_TASK_004);
    }

    if (childDueDateTime.isAfter(parentDueDate)) {
      throw ExceptionFactory.badRequest(MessageConstant.E_TASK_005);
    }
  }

  /**
   * Normalize LocalDate to start of day (00:00:01).
   *
   * @param date the date to normalize
   * @return LocalDateTime at 00:00:01
   */
  public static LocalDateTime normalizeToStartOfDay(LocalDate date) {
    return date.atTime(LocalTime.of(0, 0, 1));
  }

  /**
   * Normalize LocalDate to end of day (23:59:59).
   *
   * @param date the date to normalize
   * @return LocalDateTime at 23:59:59
   */
  public static LocalDateTime normalizeToEndOfDay(LocalDate date) {
    return date.atTime(LocalTime.of(23, 59, 59));
  }
}
