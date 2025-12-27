package com.uit.sociusmvcapp.notification.internal.listener;

import com.uit.sociusmvcapp.notification.NotificationService;
import com.uit.sociusmvcapp.shared.event.NotificationSendEvent;
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
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/** Listener to handle sending and persisting notifications when a generic event is published. */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

  private final NotificationService notificationService;

  /**
   * Handles NotificationSendEvent to process notification logic asynchronously.
   *
   * @param event the notification send event containing message details
   */
  @Retryable(
      retryFor = {DataAccessException.class, CannotAcquireLockException.class},
      backoff = @Backoff(delay = 500, multiplier = 2))
  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void onNotificationSend(NotificationSendEvent event) {
    log.info(
        "Processing notification event for receiver: {}, title: {}",
        event.getReceiverId(),
        event.getTitle());

    notificationService.sendNotification(
        event.getReceiverId(), event.getTitle(), event.getContent(), event.getLinkUrl());
  }

  /**
   * Recovery method if notification processing fails after retries.
   *
   * @param ex the exception that caused the failure
   * @param event the notification send event
   */
  @Recover
  public void recover(Exception ex, NotificationSendEvent event) {
    log.error(
        "Failed to process notification permanently for receiver {}. Error: {}",
        event.getReceiverId(),
        ex.getMessage(),
        ex);
  }
}
