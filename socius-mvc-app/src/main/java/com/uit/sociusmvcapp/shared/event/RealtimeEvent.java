package com.uit.sociusmvcapp.shared.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.uit.sociusmvcapp.shared.enums.RealtimeDomain;
import com.uit.sociusmvcapp.shared.enums.RealtimeEventType;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Generic wrapper for realtime events sent to Ktor via RabbitMQ. This wrapper is used for both
 * messages and notifications.
 *
 * @param <T> the type of the payload
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RealtimeEvent<T> {

  /** Unique event identifier. */
  @Builder.Default private String eventId = UUID.randomUUID().toString();

  /** The domain this event belongs to (MESSAGE, NOTIFICATION, SYSTEM). */
  private RealtimeDomain domain;

  /** The type of event (NEW_MESSAGE, MESSAGE_UPDATED, etc.). Optional for some events. */
  private RealtimeEventType eventType;

  /** The event payload - can be MessageDto, NotificationDto, or any other DTO. */
  private T payload;

  /** Timestamp when the event was created. */
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  @Builder.Default
  private LocalDateTime timestamp = LocalDateTime.now();

  /**
   * Creates a new MESSAGE domain event.
   *
   * @param eventType the event type
   * @param payload the message payload
   * @param <T> the payload type
   * @return the realtime event
   */
  public static <T> RealtimeEvent<T> message(RealtimeEventType eventType, T payload) {
    return RealtimeEvent.<T>builder()
        .domain(RealtimeDomain.MESSAGE)
        .eventType(eventType)
        .payload(payload)
        .build();
  }

  /**
   * Creates a new NOTIFICATION domain event.
   *
   * @param eventType the event type
   * @param payload the notification payload
   * @param <T> the payload type
   * @return the realtime event
   */
  public static <T> RealtimeEvent<T> notification(RealtimeEventType eventType, T payload) {
    return RealtimeEvent.<T>builder()
        .domain(RealtimeDomain.NOTIFICATION)
        .eventType(eventType)
        .payload(payload)
        .build();
  }

  /**
   * Creates a new SYSTEM domain event.
   *
   * @param eventType the event type
   * @param payload the system payload
   * @param <T> the payload type
   * @return the realtime event
   */
  public static <T> RealtimeEvent<T> system(RealtimeEventType eventType, T payload) {
    return RealtimeEvent.<T>builder()
        .domain(RealtimeDomain.SYSTEM)
        .eventType(eventType)
        .payload(payload)
        .build();
  }
}
