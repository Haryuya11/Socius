package com.uit.sociuswebfluxapp.constants;

/** Constants related to RabbitMQ operations. */
public final class WebSocketConstant {

  /** Private constructor to prevent instantiation. */
  private WebSocketConstant() {}

  /** Minimum number of subscribers required to complete a notification broadcast. */
  public static final int MIN_SUBSCRIBERS_TO_COMPLETE = 1;

  /** Buffer size for the Sinks. */
  public static final int BUFFER_SIZE = 256;

  /** Constant representing no subscribers. */
  public static final int NO_SUBSCRIBERS = 0;

  /** Order for the WebSocket handler mapping. */
  public static final int WEBSOCKET_HANDLER_ORDER = -1;
}
