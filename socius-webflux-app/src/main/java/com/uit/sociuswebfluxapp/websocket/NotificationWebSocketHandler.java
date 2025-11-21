package com.uit.sociuswebfluxapp.websocket;

import com.uit.sociuscoremodules.employee.dto.EmployeeDto;
import com.uit.sociuscoremodules.shared.utils.CommonUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

/** WebSocketHandler for handling notification WebSocket sessions. */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationWebSocketHandler implements WebSocketHandler {
  private final NotificationBroadcaster broadcaster;

  @Override
  @NonNull
  public Mono<Void> handle(@NonNull WebSocketSession session) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .switchIfEmpty(Mono.error(new RuntimeException("Unauthenticated")))
        .flatMap(auth -> handleAuthenticated(session, auth))
        .onErrorResume(
            err -> session.close(CloseStatus.NOT_ACCEPTABLE.withReason(err.getMessage())));
  }

  private Mono<Void> handleAuthenticated(WebSocketSession session, Authentication auth) {
    Object principal = auth.getPrincipal();
    if (!(principal instanceof EmployeeDto employee)) {
      return session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Unauthenticated"));
    }
    String clientId = employee.getClientId();
    if (StringUtils.isBlank(clientId)) {
      return session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Invalid principal"));
    }

    return session
        .send(
            broadcaster
                .streamFor(clientId)
                .map(CommonUtils::serializeToJson)
                .map(session::textMessage))
        .and(
            session
                .receive()
                .doOnError(e -> log.warn("WS receive error for {}: {}", clientId, e.toString()))
                .doFinally(
                    signal ->
                        log.debug("WS disconnected: clientId={}, signal={}", clientId, signal)));
  }
}
