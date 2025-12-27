package com.uit.sociusmvcapp.workforce.internal.listener;

import com.uit.sociusmvcapp.employee.EmployeeDeletedEvent;
import com.uit.sociusmvcapp.workforce.internal.repository.DepartmentEmployeeRepository;
import com.uit.sociusmvcapp.workforce.internal.repository.TeamEmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

/** Listener to handle cleanup of workforce data when an employee is deleted. */
@Component
@RequiredArgsConstructor
@Slf4j
public class WorkforceCleanupListener {
  private final TeamEmployeeRepository teamEmployeeRepository;
  private final DepartmentEmployeeRepository departmentEmployeeRepository;

  /**
   * Handles EmployeeDeletedEvent to clean up workforce data.
   *
   * @param event the employee deleted event
   */
  @Retryable(
      retryFor = {DataAccessException.class, CannotAcquireLockException.class},
      backoff = @Backoff(delay = 500, multiplier = 2))
  @Async
  @TransactionalEventListener
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void onEmployeeDeleted(EmployeeDeletedEvent event) {
    String employeeId = event.employeeId();
    log.info("Cleaning up workforce data for deleted employee: {}", employeeId);
    teamEmployeeRepository.deleteByEmployeeId(employeeId);
    departmentEmployeeRepository.deleteByEmployeeId(employeeId);
  }

  /**
   * Recovery method if cleanup fails after retries.
   *
   * @param ex the exception that caused the failure
   * @param event the employee deleted event
   */
  @Recover
  public void recover(Exception ex, EmployeeDeletedEvent event) {
    log.error("Cleanup workforce failed permanently for employee {}", event.employeeId(), ex);
  }
}
