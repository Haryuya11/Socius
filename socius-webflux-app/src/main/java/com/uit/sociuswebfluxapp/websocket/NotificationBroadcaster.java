package com.uit.sociuswebfluxapp.websocket;

import com.uit.sociuscoremodules.notification.dto.NotificationDto;
import com.uit.sociuswebfluxapp.constants.WebSocketConstant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/** Component responsible for broadcasting notifications to connected WebSocket clients. */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationBroadcaster {

  /** Map of client IDs to their corresponding notification sinks. */
  private final Map<String, Sinks.Many<NotificationDto>> userSinks = new ConcurrentHashMap<>();

  /**
   * Get or create a sink for the given client ID.
   *
   * @param clientId the unique identifier of the client
   * @return the sink associated with the client ID
   */
  private Sinks.Many<NotificationDto> getOrCreateSinkForStream(String clientId) {
    return userSinks.computeIfAbsent(
        clientId,
        id -> Sinks.many().multicast().onBackpressureBuffer(WebSocketConstant.BUFFER_SIZE, false));
  }

  /**
   * Get the existing sink for the given client ID.
   *
   * @param clientId the unique identifier of the client
   * @return the existing sink associated with the client ID, or null if none exists
   */
  private Sinks.Many<NotificationDto> getExistingSink(String clientId) {
    return userSinks.get(clientId);
  }

  /**
   * Publish a notification to the appropriate client's sink.
   *
   * @param notification the notification to be published
   */
  public void publish(NotificationDto notification) {
    try {
      if (notification == null || notification.getReceiverId() == null) {
        return;
      }
      String clientId = notification.getReceiverId();
      Sinks.Many<NotificationDto> sink = getExistingSink(clientId);
      if (sink == null) {
        // No active subscribers for this client; drop or persist elsewhere as needed.
        log.debug(
            "No active sink for clientId={}, dropping notification id={}",
            clientId,
            notification.getId());
        return;
      }
      Sinks.EmitResult result = sink.tryEmitNext(notification);
      if (result.isFailure()) {
        log.warn("Emit failed for clientId={}, result={}", clientId, result);
      }
    } catch (Exception e) {
      log.error("Error while publishing notification: {}", e.getMessage(), e);
    }
  }

  /**
   * Stream notifications for the given client ID.
   *
   * @param clientId the unique identifier of the client
   * @return a Flux stream of notifications for the client
   */
  public Flux<NotificationDto> streamFor(String clientId) {
    return getOrCreateSinkForStream(clientId)
        .asFlux()
        .doFinally(signalType -> tryCleanup(clientId));
  }

  /**
   * Attempt to clean up the sink for the given client ID if there are no subscribers.
   *
   * @param clientId the unique identifier of the client
   */
  private void tryCleanup(String clientId) {
    Sinks.Many<NotificationDto> sink = userSinks.get(clientId);
    if (sink == null) {
      return;
    }
    boolean noSubscribers = sink.currentSubscriberCount() == WebSocketConstant.NO_SUBSCRIBERS;
    boolean removed = noSubscribers && userSinks.remove(clientId, sink);
    if (removed) {
      sink.tryEmitComplete();
      log.debug("Cleaned up sink for clientId={}", clientId);
    }
  }
}
