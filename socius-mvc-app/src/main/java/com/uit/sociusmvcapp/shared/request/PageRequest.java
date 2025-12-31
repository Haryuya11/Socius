package com.uit.sociusmvcapp.shared.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for pagination parameters. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageRequest {
  private Integer pageNumber;
  private Integer pageSize;
}
