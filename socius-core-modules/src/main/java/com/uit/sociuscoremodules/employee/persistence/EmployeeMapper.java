package com.uit.sociuscoremodules.employee.persistence;

import com.uit.sociuscoremodules.employee.domain.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** MyBatis Mapper interface for Employee entity. */
@Mapper
public interface EmployeeMapper {

  /**
   * Get Employee by ID.
   *
   * @param id the employee ID
   * @return the Employee entity
   */
  Employee getEmployeeById(@Param("id") String id);
}
