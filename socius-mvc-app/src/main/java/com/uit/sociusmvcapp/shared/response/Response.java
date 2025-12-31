package com.uit.sociusmvcapp.shared.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Standard response structure for API responses. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Response {
  Boolean success;
  int status;
  String code;
  String message;
  Object data;
}
