package com.uit.sociusmvcapp.shared.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for sorting parameters. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SortRequest {
  private String sortBy;
  private String sortDirection;
}
