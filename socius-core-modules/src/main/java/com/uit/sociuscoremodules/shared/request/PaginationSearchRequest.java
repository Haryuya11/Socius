package com.uit.sociuscoremodules.shared.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for pagination and search criteria. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaginationSearchRequest<T> {
  private T condition;
  private PageRequest pageRequest;
  private List<SortRequest> sortRequests;
}
