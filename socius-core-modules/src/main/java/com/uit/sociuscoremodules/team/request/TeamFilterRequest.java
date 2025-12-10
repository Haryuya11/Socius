package com.uit.sociuscoremodules.team.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for filtering teams with pagination and sorting. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamFilterRequest {
  private String teamCode;
  private String teamName;
  private String departmentCode;
  private String search;
  private String sortBy;
  private String sortOrder;
  private Integer page;
  private Integer size;

  /** Get the sort field with default value. */
  public String getSortBy() {
    return sortBy != null && !sortBy.isEmpty() ? sortBy : "id";
  }

  /** Get the sort order with default value. */
  public String getSortOrder() {
    return sortOrder != null && sortOrder.equalsIgnoreCase("desc") ? "DESC" : "ASC";
  }

  /** Get the page number with default value. */
  public Integer getPage() {
    return page != null && page > 0 ? page : 1;
  }

  /** Get the page size with default value. */
  public Integer getSize() {
    return size != null && size > 0 ? size : 10;
  }

  /** Get the offset for pagination. */
  public Integer getOffset() {
    return (getPage() - 1) * getSize();
  }
}
