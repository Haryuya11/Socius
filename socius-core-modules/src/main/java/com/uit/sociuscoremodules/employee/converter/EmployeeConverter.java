package com.uit.sociuscoremodules.employee.converter;

import com.uit.sociuscoremodules.employee.domain.Employee;
import com.uit.sociuscoremodules.employee.dto.EmployeeDto;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/** Converter for Employee entity and EmployeeDto. */
@Mapper(componentModel = "spring")
public interface EmployeeConverter {
  /** Singleton instance of the EmployeeConverter. */
  EmployeeConverter INSTANCE = Mappers.getMapper(EmployeeConverter.class);

  /**
   * Convert an Employee entity to an EmployeeDto.
   *
   * @param employee the Employee entity
   * @return the corresponding EmployeeDto
   */
  EmployeeDto entityToDto(Employee employee);

  /**
   * Convert a list of Employee entities to a list of EmployeeDtos.
   *
   * @param employees the list of Employee entities
   * @return the corresponding list of EmployeeDtos
   */
  List<EmployeeDto> entityToDto(List<Employee> employees);
}
