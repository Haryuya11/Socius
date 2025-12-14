package com.uit.sociuscoremodules.department.converter;

import com.uit.sociuscoremodules.department.domain.Department;
import com.uit.sociuscoremodules.department.domain.DepartmentEmployees;
import com.uit.sociuscoremodules.department.dto.DepartmentDto;
import com.uit.sociuscoremodules.department.dto.DepartmentEmployeesDto;
import com.uit.sociuscoremodules.department.request.DepartmentCreateRequest;
import com.uit.sociuscoremodules.department.request.EmployeeAddRequest;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/** Converter for Department entity, DTO, and request objects. */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DepartmentConverter {

  /**
   * Converts a Department entity to a DepartmentDto.
   *
   * @param department the Department entity
   * @return the corresponding DepartmentDto
   */
  DepartmentDto entityToDto(Department department);

  /**
   * Converts a list of Department entities to a list of DepartmentDtos.
   *
   * @param departments the list of Department entities
   * @return the corresponding list of DepartmentDtos
   */
  List<DepartmentDto> entityListToDto(List<Department> departments);

  /**
   * Converts a DepartmentCreateRequest to a Department entity for persistence (creation).
   *
   * @param request The department creation request DTO.
   * @return The corresponding Department entity.
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "deleteFlag", ignore = true)
  Department requestToEntity(DepartmentCreateRequest request);

  /**
   * Converts a DepartmentCreateRequest to an existing Department entity for updating.
   *
   * <p>Note: You may need a separate update method in a real-world scenario if the update request
   * requires an ID or if certain fields (like departmentCode) should not be updated. For
   * simplicity, this uses the create request structure.
   *
   * @param request The department update request DTO.
   * @param department The Department entity to update.
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "deleteFlag", ignore = true)
  void updateEntityFromRequest(
      DepartmentCreateRequest request, @MappingTarget Department department);

  /**
   * Converts a list of Employee entities to a list of DepartmentEmployeesDto.
   *
   * @param employees the list of DepartmentEmployees entities
   * @return the corresponding list of DepartmentEmployeesDto
   */
  List<DepartmentEmployeesDto> entityListToEmployeeDto(List<DepartmentEmployees> employees);

  /**
   * Converts a DepartmentCreateRequest to a Department entity for creation.
   *
   * @param request The department creation request DTO.
   * @return The corresponding Department entity.
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "deleteFlag", ignore = true)
  Department createRequestToEntity(DepartmentCreateRequest request);

  /**
   * Converts an EmployeeAddRequest to a DepartmentEmployees entity for adding an employee to a
   * department.
   *
   * @param request The employee addition request DTO.
   * @return The corresponding DepartmentEmployees entity.
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "departmentCode", source = "departmentCode")
  @Mapping(target = "employeeId", source = "employeeId")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "deleteFlag", ignore = true)
  DepartmentEmployees createEmployeeRequestToEntity(
      EmployeeAddRequest request, String departmentCode, String employeeId);

  /**
   * Converts roleCode and isPrimary to an EmployeeAddRequest.
   *
   * @param roleCode the role code of the employee
   * @param isPrimary whether the employee is primary in the department
   * @return the corresponding EmployeeAddRequest
   */
  @Mapping(target = "roleCode", source = "roleCode")
  @Mapping(target = "isPrimary", source = "isPrimary")
  EmployeeAddRequest toEmployeeAddRequest(String roleCode, Boolean isPrimary);

  /**
   * Converts a DepartmentEmployees entity to a DepartmentEmployeesDto.
   *
   * @param departmentEmployees the DepartmentEmployees entity
   * @return the corresponding DepartmentEmployeesDto
   */
  DepartmentEmployeesDto entityToDepartmentEmployeesDto(DepartmentEmployees departmentEmployees);
}
