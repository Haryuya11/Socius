package com.uit.sociusmvcapp.department.internal.persistence;

import com.uit.sociusmvcapp.department.dto.request.CreateDepartmentRequest;
import com.uit.sociusmvcapp.department.internal.domain.Department;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

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
   * @param request the department reactivation request
   */
  void activate(CreateDepartmentRequest request);

  /**
   * Get all Departments.
   *
   * @return list of Department entities
   */
  List<Department> findAll();

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
}
