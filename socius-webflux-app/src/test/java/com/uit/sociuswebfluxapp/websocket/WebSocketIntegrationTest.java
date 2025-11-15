package com.uit.sociuswebfluxapp.websocket;

import com.uit.sociuswebfluxapp.config.TestSecurityConfig;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/** Tests for WebSocket functionality using WebFlux native WebSocket support. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@Import(TestSecurityConfig.class)
class WebSocketIntegrationTest {

  @LocalServerPort private int port;

  /** Test the echo WebSocket endpoint by sending a message and verifying the echoed response. */
  @Test
  void testEchoWebSocket() {
    WebSocketClient client = new ReactorNettyWebSocketClient();
    URI uri = URI.create("ws://localhost:" + port + "/ws/echo");

    AtomicReference<String> receivedMessage = new AtomicReference<>();

    Mono<Void> result =
        client.execute(
            uri,
            session -> {
              // Setup receive before sending
              Flux<String> receive =
                  session.receive().take(1).map(WebSocketMessage::getPayloadAsText);

              // Send and capture response concurrently
              return session
                  .send(Mono.just(session.textMessage("Hello WebFlux")))
                  .thenMany(receive)
                  .doOnNext(receivedMessage::set)
                  .then();
            });

    StepVerifier.create(result.timeout(Duration.ofSeconds(5))).verifyComplete();

    // Verify the received message
    assert receivedMessage.get() != null : "No message received from WebSocket";
    assert receivedMessage.get().equals("Echo: Hello WebFlux")
        : "Expected 'Echo: Hello WebFlux' but got: " + receivedMessage.get();
  }

  /** Test the chat WebSocket endpoint by sending multiple messages and verifying broadcasting. */
  @Test
  void testChatWebSocket() {
    WebSocketClient client = new ReactorNettyWebSocketClient();
    URI uri = URI.create("ws://localhost:" + port + "/ws/chat");

    Flux<String> messages = Flux.just("Message 1", "Message 2", "Message 3");
    AtomicReference<String> receivedMessage = new AtomicReference<>();

    Mono<Void> result =
        client.execute(
            uri,
            session -> {
              // Setup receive before sending
              Flux<String> receive =
                  session.receive().take(1).map(WebSocketMessage::getPayloadAsText);

              // Send and capture response concurrently
              return session
                  .send(messages.map(session::textMessage))
                  .thenMany(receive)
                  .doOnNext(receivedMessage::set)
                  .then();
            });

    StepVerifier.create(result.timeout(Duration.ofSeconds(5))).verifyComplete();

    // Verify that we receive a message containing the first message
    assert receivedMessage.get() != null : "No message received from WebSocket";
    assert receivedMessage.get().contains("Message 1")
        : "Expected message containing 'Message 1' but got: " + receivedMessage.get();
  }
}
