package com.uit.sociuscoremodules.shared.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Generic class representing a paginated response.
 *
 * @param <T> the type of items in the page
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResponse<T> {
  private List<T> data;
  private long totalItems;
  private int totalPages;
  private int currentPage;
  private boolean hasNext;
  private boolean hasPrevious;

  /**
   * Static factory method to create a PageResponse.
   *
   * @param data the list of items on the current page
   * @param totalItems the total number of items across all pages
   * @param offset the offset of the first item in the current page
   * @param limit the maximum number of items per page
   * @param <T> the type of items in the page
   * @return a PageResponse object
   */
  public static <T> PageResponse<T> of(List<T> data, long totalItems, int offset, int limit) {

    // Calculate total pages
    int totalPages = (limit <= 0) ? 1 : (int) Math.ceil((double) totalItems / limit);

    // Calculate current page
    int currentPage = (limit <= 0) ? 1 : (offset / limit) + 1;

    // Determine if there are next and previous pages
    boolean hasNext = currentPage < totalPages;
    boolean hasPrevious = currentPage > 1;

    return PageResponse.<T>builder()
        .data(data)
        .totalItems(totalItems)
        .currentPage(currentPage)
        .totalPages(totalPages)
        .hasNext(hasNext)
        .hasPrevious(hasPrevious)
        .build();
  }

  /**
   * Static factory method to create an empty PageResponse.
   *
   * @param <T> the type of items in the page
   * @return an empty PageResponse object
   */
  public static <T> PageResponse<T> empty() {
    return PageResponse.<T>builder()
        .data(List.of())
        .totalItems(0)
        .currentPage(1)
        .totalPages(1)
        .hasNext(false)
        .hasPrevious(false)
        .build();
  }
}
