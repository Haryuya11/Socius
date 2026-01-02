package com.uit.sociusmvcapp.shared.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Generic class representing a cursor-based paginated response.
 *
 * @param <T> the type of items in the response
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CursorResponse<T> {
  private List<T> data;
  private String nextCursor;
  private boolean hasNext;
}
