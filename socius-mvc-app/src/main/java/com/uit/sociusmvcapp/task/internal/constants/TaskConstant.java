package com.uit.sociusmvcapp.task.internal.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Constants for Task module. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TaskConstant {
  /** Default sort field for task search. */
  public static final String DEFAULT_SORT_BY = "createdAt";

  /** Default sort direction. */
  public static final String DEFAULT_SORT_DIRECTION = "DESC";

  /** Default page size for pagination. */
  public static final int DEFAULT_PAGE_SIZE = 10;

  /** Minimum page size for pagination. */
  public static final int MIN_PAGE_SIZE = 1;

  /** Maximum page size. */
  public static final int MAX_PAGE_SIZE = 100;

  /** Keyword for current user in search. */
  public static final String CURRENT_USER_KEYWORD = "me";
}
