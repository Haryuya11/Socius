package com.uit.sociuswebfluxapp.config;

import com.uit.sociuswebfluxapp.websocket.ChatWebSocketHandler;
import com.uit.sociuswebfluxapp.websocket.EchoWebSocketHandler;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

/**
 * WebSocketConfig configures WebSocket endpoints using WebFlux native WebSocket support (not
 * RSocket).
 */
@Configuration
@RequiredArgsConstructor
public class WebSocketConfig {

  /** EchoWebSocketHandler for handling WebSocket connections. */
  private final EchoWebSocketHandler echoWebSocketHandler;

  /** ChatWebSocketHandler for handling chat WebSocket connections. */
  private final ChatWebSocketHandler chatWebSocketHandler;

  /**
   * Configures WebSocket handler mappings.
   *
   * @return HandlerMapping for WebSocket endpoints
   */
  @Bean
  public HandlerMapping webSocketHandlerMapping() {
    Map<String, WebSocketHandler> map = new HashMap<>();
    map.put("/ws/echo", echoWebSocketHandler);
    map.put("/ws/chat", chatWebSocketHandler);

    SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
    handlerMapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
    handlerMapping.setUrlMap(map);
    return handlerMapping;
  }

  /**
   * WebSocketHandlerAdapter to enable WebSocket support.
   *
   * @return WebSocketHandlerAdapter instance
   */
  @Bean
  public WebSocketHandlerAdapter handlerAdapter() {
    return new WebSocketHandlerAdapter();
  }
}
