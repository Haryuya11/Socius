package com.uit.sociusmvcapp.department.internal.repository;

import com.uit.sociusmvcapp.department.dto.DepartmentDto;
import com.uit.sociusmvcapp.department.dto.SearchDepartmentDto;
import com.uit.sociusmvcapp.department.dto.request.CreateDepartmentRequest;
import com.uit.sociusmvcapp.department.dto.request.SearchDepartmentRequest;
import com.uit.sociusmvcapp.department.internal.converter.DepartmentConverter;
import com.uit.sociusmvcapp.department.internal.persistence.DepartmentMapper;
import com.uit.sociusmvcapp.shared.request.SortRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** Repository for Department entity. */
@Repository
@RequiredArgsConstructor
public class DepartmentRepository {
  /** MyBatis Mapper for Department entity. */
  private final DepartmentMapper mapper;

  /** Singleton instance of DepartmentConverter. */
  private final DepartmentConverter converter;

  /**
   * Get DepartmentDto by department code.
   *
   * @param departmentCode the department code
   * @return the corresponding DepartmentDto
   */
  public DepartmentDto findByDepartmentCode(String departmentCode) {
    return converter.entityToDto(mapper.findByDepartmentCode(departmentCode));
  }

  /**
   * Create a new department record.
   *
   * @param request the department creation request
   */
  public void create(CreateDepartmentRequest request) {
    mapper.create(converter.createRequestToEntity(request));
  }

  /**
   * Update an existing department record.
   *
   * @param request the department update request
   */
  public void update(CreateDepartmentRequest request) {
    mapper.update(converter.createRequestToEntity(request));
  }

  /**
   * Deactivate a department by its department code.
   *
   * @param departmentCode the department code
   */
  public void deactivate(String departmentCode) {
    mapper.deactivate(departmentCode);
  }

  /**
   * Activate a department by its department code.
   *
   * @param request the department activation request
   */
  public void activate(CreateDepartmentRequest request) {
    mapper.activate(request.getDepartmentCode(), request.getDepartmentName());
  }

  /**
   * Get deleted DepartmentDto by department code.
   *
   * @param departmentCode the department code
   * @return the corresponding deleted DepartmentDto
   */
  public DepartmentDto findDeletedByDepartmentCode(String departmentCode) {
    return converter.entityToDto(mapper.findDeletedByDepartmentCode(departmentCode));
  }

  /**
   * Check if a department exists by its department code.
   *
   * @param departmentCode the department code
   * @return true if the department exists, false otherwise
   */
  public boolean existsByDepartmentCode(String departmentCode) {
    return mapper.existsByDepartmentCode(departmentCode);
  }

  /**
   * Search for departments based on criteria, sorting, limit, and offset.
   *
   * @param criteria the search criteria
   * @param sorts the sorting options
   * @param limit the maximum number of records to return
   * @param offset the starting point for records to return
   * @return list of SearchDepartmentDto matching the search criteria
   */
  public List<SearchDepartmentDto> search(
      SearchDepartmentRequest criteria, List<SortRequest> sorts, int limit, int offset) {
    return converter.entitiesToSearchDto(mapper.search(criteria, sorts, limit, offset));
  }

  /**
   * Count the number of departments matching the search criteria.
   *
   * @param criteria the search criteria
   * @return the count of matching departments
   */
  public int count(SearchDepartmentRequest criteria) {
    return mapper.count(criteria);
  }
}
