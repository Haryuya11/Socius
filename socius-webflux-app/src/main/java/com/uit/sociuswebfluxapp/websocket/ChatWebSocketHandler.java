package com.uit.sociuswebfluxapp.websocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

/**
 * ChatWebSocketHandler handles WebSocket connections for a simple chat application. This
 * demonstrates session management and message broadcasting using WebFlux native WebSocket support.
 */
@Component
@Slf4j
public class ChatWebSocketHandler implements WebSocketHandler {

  /** Map to store active WebSocket sessions. */
  private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

  /** Sink for broadcasting messages to all connected clients. */
  private final Sinks.Many<String> messageSink = Sinks.many().multicast().onBackpressureBuffer();

  /**
   * Handles WebSocket session for chat functionality.
   *
   * @param session the WebSocket session
   * @return Mono representing the completion of the WebSocket handling
   */
  @Override
  public Mono<Void> handle(WebSocketSession session) {
    String sessionId = session.getId();
    log.info("Chat WebSocket connection established: sessionId={}", sessionId);

    // Add session to active sessions
    sessions.put(sessionId, session);

    // Create a flux that broadcasts messages to this session
    Flux<WebSocketMessage> output =
        messageSink.asFlux().map(message -> session.textMessage(message));

    // Handle incoming messages
    Mono<Void> input =
        session
            .receive()
            .map(WebSocketMessage::getPayloadAsText)
            .doOnNext(
                payload -> {
                  log.info("Received chat message: sessionId={}, payload={}", sessionId, payload);
                  // Broadcast to all connected clients
                  String broadcastMessage =
                      String.format("[%s]: %s", sessionId.substring(0, 8), payload);
                  messageSink.tryEmitNext(broadcastMessage);
                })
            .then();

    // Cleanup on disconnect
    return Mono.zip(input, session.send(output))
        .doFinally(
            signalType -> {
              sessions.remove(sessionId);
              log.info(
                  "Chat WebSocket connection closed: sessionId={}, signal={}",
                  sessionId,
                  signalType);
            })
        .then();
  }

  /**
   * Gets the count of active sessions.
   *
   * @return number of active WebSocket sessions
   */
  public int getActiveSessionCount() {
    return sessions.size();
  }
}
