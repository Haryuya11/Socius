package com.uit.sociuswebfluxapp.config;

import com.uit.sociuswebfluxapp.constants.WebSocketConstant;
import com.uit.sociuswebfluxapp.websocket.NotificationWebSocketHandler;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

/**
 * WebSocketConfig configures WebSocket settings for the web application, including handler
 * mappings.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig {
  /** NotificationWebSocketHandler for handling notification WebSocket connections. */
  private final NotificationWebSocketHandler notificationHandler;

  /**
   * Configures the WebSocket handler mapping.
   *
   * @return the HandlerMapping for WebSocket endpoints
   */
  @Bean
  public HandlerMapping webSocketMapping() {
    Map<String, Object> map = new HashMap<>();
    map.put("/ws/notifications", notificationHandler);
    map.put("/ws/notifications/**", notificationHandler);

    SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
    mapping.setUrlMap(map);
    mapping.setOrder(WebSocketConstant.WEBSOCKET_HANDLER_ORDER);
    return mapping;
  }

  /**
   * Configures the WebSocket handler adapter.
   *
   * @return the WebSocketHandlerAdapter
   */
  @Bean
  public WebSocketHandlerAdapter handlerAdapter() {
    return new WebSocketHandlerAdapter();
  }
}
