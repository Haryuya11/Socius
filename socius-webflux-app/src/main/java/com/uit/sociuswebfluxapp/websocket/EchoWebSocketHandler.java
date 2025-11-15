package com.uit.sociuswebfluxapp.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

/**
 * EchoWebSocketHandler handles WebSocket connections and echoes received messages back to the
 * client. This is a simple example demonstrating WebFlux native WebSocket support.
 */
@Component
@Slf4j
public class EchoWebSocketHandler implements WebSocketHandler {

  /**
   * Handles WebSocket session by echoing received messages back to the client.
   *
   * @param session the WebSocket session
   * @return Mono representing the completion of the WebSocket handling
   */
  @Override
  public Mono<Void> handle(WebSocketSession session) {
    log.info("WebSocket connection established: sessionId={}", session.getId());

    return session
        .send(
            session
                .receive()
                .map(
                    message -> {
                      String payload = message.getPayloadAsText();
                      log.info(
                          "Received message: sessionId={}, payload={}", session.getId(), payload);
                      return session.textMessage("Echo: " + payload);
                    }))
        .doOnTerminate(
            () -> log.info("WebSocket connection closed: sessionId={}", session.getId()));
  }
}
