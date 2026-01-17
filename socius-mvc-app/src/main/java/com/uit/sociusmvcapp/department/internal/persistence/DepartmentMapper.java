package com.uit.sociusmvcapp.department.internal.persistence;

import com.uit.sociusmvcapp.department.dto.request.SearchDepartmentRequest;
import com.uit.sociusmvcapp.department.internal.domain.Department;
import com.uit.sociusmvcapp.shared.request.SortRequest;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** MyBatis Mapper interface for Department entity. */
@Mapper
public interface DepartmentMapper {
  /**
   * Find a Department by its department code.
   *
   * @param departmentCode the department code
   * @return the Department entity
   */
  Department findByDepartmentCode(String departmentCode);

  /**
   * Create a new Department.
   *
   * @param request the department to be created
   */
  void create(Department request);

  /**
   * Update an existing Department.
   *
   * @param request the department update request
   */
  void update(Department request);

  /**
   * Soft delete a Department by its department code.
   *
   * @param departmentCode the department code
   */
  void deactivate(String departmentCode);

  /**
   * Reactivate a previously deleted Department.
   *
   * @param departmentCode the department code
   * @param departmentName the department name
   */
  void activate(String departmentCode, String departmentName);

  /**
   * Find a deleted Department by its department code.
   *
   * @param departmentCode the department code
   * @return the deleted Department entity
   */
  Department findDeletedByDepartmentCode(String departmentCode);

  /**
   * Check if a Department exists by its department code.
   *
   * @param departmentCode the department code
   * @return true if the department exists, false otherwise
   */
  boolean existsByDepartmentCode(String departmentCode);

  /**
   * Search for Departments based on criteria, sorting, pagination.
   *
   * @param criteria the search criteria
   * @param sorts the sorting options
   * @param limit the maximum number of records to return
   * @param offset the starting point for records to return
   * @return list of Department entities matching the search criteria
   */
  List<Department> search(
      @Param("criteria") SearchDepartmentRequest criteria,
      @Param("sorts") List<SortRequest> sorts,
      @Param("limit") int limit,
      @Param("offset") int offset);

  /**
   * Count the number of Departments matching the search criteria.
   *
   * @param criteria the search criteria
   * @return the count of matching Departments
   */
  int count(@Param("criteria") SearchDepartmentRequest criteria);
}
