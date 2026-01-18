package com.uit.sociusmvcapp.department;

import com.uit.sociusmvcapp.department.dto.DepartmentDto;
import com.uit.sociusmvcapp.department.dto.SearchDepartmentDto;
import com.uit.sociusmvcapp.department.dto.request.CreateDepartmentRequest;
import com.uit.sociusmvcapp.department.dto.request.SearchDepartmentRequest;
import com.uit.sociusmvcapp.department.dto.request.UpdateDepartmentRequest;
import com.uit.sociusmvcapp.shared.request.PaginationSearchRequest;
import com.uit.sociusmvcapp.shared.response.PageResponse;

/** Service interface for Department operations. */
public interface DepartmentService {
  /**
   * Get department information by department code.
   *
   * @param departmentCode the code of the department
   * @return DepartmentDto representing the department information
   */
  DepartmentDto findByDepartmentCode(String departmentCode);

  /**
   * Create a new department.
   *
   * @param request the request containing department creation details
   */
  void create(CreateDepartmentRequest request);

  /**
   * Update an existing department.
   *
   * @param request the request containing department update details
   * @param departmentCode the code of the department to be updated
   */
  void update(UpdateDepartmentRequest request, String departmentCode);

  /**
   * Deactivate a department.
   *
   * @param departmentCode the code of the department to be deactivated
   */
  void deactivate(String departmentCode);

  /**
   * Validate if a department exists by its code.
   *
   * @param departmentCode the code of the department
   */
  void validateExists(String departmentCode);

  /**
   * Search for departments based on given criteria with pagination.
   *
   * @param request the pagination search request containing search criteria
   * @return a paginated response of SearchDepartmentDto matching the search criteria
   */
  PageResponse<SearchDepartmentDto> search(
      PaginationSearchRequest<SearchDepartmentRequest> request);
}
