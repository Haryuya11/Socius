# WebFlux Native WebSocket Implementation

This document describes the WebFlux native WebSocket implementation in the Socius WebFlux application.

## Overview

The application now uses Spring WebFlux's native WebSocket support instead of RSocket for real-time communication. This provides a standards-based WebSocket implementation that is fully reactive and integrates seamlessly with the WebFlux architecture.

## WebSocket Endpoints

The following WebSocket endpoints are available:

### 1. Echo WebSocket (`/ws/echo`)

A simple echo service that returns any message sent to it with an "Echo: " prefix.

**Example usage with JavaScript:**
```javascript
const socket = new WebSocket('ws://localhost:8081/ws/echo');

socket.onopen = () => {
    socket.send('Hello WebFlux');
};

socket.onmessage = (event) => {
    console.log('Received:', event.data); // Output: "Echo: Hello WebFlux"
};
```

### 2. Chat WebSocket (`/ws/chat`)

A broadcast chat service that sends messages to all connected clients. Each message is prefixed with the sender's session ID (first 8 characters).

**Example usage with JavaScript:**
```javascript
const socket = new WebSocket('ws://localhost:8081/ws/chat');

socket.onopen = () => {
    socket.send('Hello everyone!');
};

socket.onmessage = (event) => {
    console.log('Received:', event.data); // Output: "[11fb7d3c]: Hello everyone!"
};
```

## Implementation Details

### WebSocket Configuration

The `WebSocketConfig` class configures WebSocket endpoints using:
- `SimpleUrlHandlerMapping`: Maps URLs to WebSocket handlers
- `WebSocketHandlerAdapter`: Enables WebSocket support in WebFlux

### Handlers

#### EchoWebSocketHandler
- Receives messages from clients
- Echoes them back with "Echo: " prefix
- Logs connection lifecycle events

#### ChatWebSocketHandler
- Maintains a map of active WebSocket sessions
- Uses Reactor Sinks for message broadcasting
- Broadcasts messages to all connected clients
- Automatically cleans up disconnected sessions

## Security

WebSocket endpoints are protected by Spring Security:
- `/ws/**` paths require authentication (configured in `SecurityConfig`)
- For testing, use `TestSecurityConfig` which permits all requests

## Testing

Integration tests are available in `WebSocketIntegrationTest`:
- Tests WebSocket connection establishment
- Tests message sending and receiving
- Tests echo functionality
- Tests broadcast/chat functionality

Run tests with:
```bash
./gradlew :socius-webflux-app:test --tests WebSocketIntegrationTest
```

## Architecture Notes

- **Fully Reactive**: Uses Project Reactor for non-blocking I/O
- **Scalable**: Reactor's backpressure handling ensures efficient resource usage
- **Standards-Based**: Uses WebSocket protocol, not proprietary messaging protocols
- **Integration**: Works seamlessly with Spring Security and WebFlux infrastructure

## Future Enhancements

Possible improvements:
1. Add authentication/authorization per message
2. Implement private messaging between specific clients
3. Add message persistence
4. Implement presence/typing indicators
5. Add rate limiting for message sending
