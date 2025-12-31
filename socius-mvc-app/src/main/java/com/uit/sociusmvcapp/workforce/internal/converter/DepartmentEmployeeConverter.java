package com.uit.sociusmvcapp.workforce.internal.converter;

import com.uit.sociusmvcapp.iam.dto.UserDepartmentInfo;
import com.uit.sociusmvcapp.shared.converter.BaseConverter;
import com.uit.sociusmvcapp.workforce.dto.DepartmentEmployeeDto;
import com.uit.sociusmvcapp.workforce.dto.request.AssignEmployeeToDepartmentRequest;
import com.uit.sociusmvcapp.workforce.dto.request.TransferEmployeeRequest;
import com.uit.sociusmvcapp.workforce.internal.domain.DepartmentEmployee;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** Converter interface for DepartmentEmployee entity and related DTOs/requests. */
@Mapper(componentModel = "spring")
public interface DepartmentEmployeeConverter
    extends BaseConverter<DepartmentEmployee, DepartmentEmployeeDto> {

  /**
   * Converts a {@link DepartmentEmployee} entity to a {@link DepartmentEmployee}.
   *
   * @param entity the {@link DepartmentEmployee} entity
   * @return the corresponding {@link DepartmentEmployee}
   */
  @Override
  @Mapping(target = "departmentCode", source = "department.departmentCode")
  @Mapping(target = "departmentName", source = "department.departmentName")
  @Mapping(target = "clientId", source = "employee.clientId")
  @Mapping(target = "userId", source = "employee.userId")
  @Mapping(target = "firstName", source = "employee.firstName")
  @Mapping(target = "lastName", source = "employee.lastName")
  @Mapping(target = "systemRole", source = "employee.systemRole")
  @Mapping(target = "imageUrl", source = "employee.imageUrl")
  @Mapping(target = "salary", source = "employee.salary")
  DepartmentEmployeeDto entityToDto(DepartmentEmployee entity);

  @Override
  @Mapping(target = "department", ignore = true)
  @Mapping(target = "employee", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "deleteFlag", ignore = true)
  DepartmentEmployee dtoToEntity(DepartmentEmployeeDto dto);

  /**
   * Converts an EmployeeAddRequest to a DepartmentEmployees entity for adding an employee to a
   * department.
   *
   * @param request The employee addition request DTO.
   * @return The corresponding DepartmentEmployees entity.
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "employee.clientId", source = "request.employeeId")
  @Mapping(target = "roleCode", source = "request.roleCode")
  @Mapping(target = "isPrimary", source = "request.isPrimary")
  @Mapping(target = "department.departmentCode", source = "departmentCode")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "deleteFlag", ignore = true)
  DepartmentEmployee toEntity(AssignEmployeeToDepartmentRequest request, String departmentCode);

  /**
   * Converts a TransferEmployeeRequest to an AssignEmployeeToDepartmentRequest.
   *
   * @param request the transfer employee request
   * @return the corresponding assign employee to department request
   */
  AssignEmployeeToDepartmentRequest toAssignEmployeeToDepartmentRequest(
      TransferEmployeeRequest request);

  /**
   * Convert DepartmentEmployeeDto to UserDepartmentInfo.
   *
   * @param dto the DepartmentEmployeeDto
   * @return the corresponding UserDepartmentInfo
   */
  UserDepartmentInfo toUserDepartmentInfo(DepartmentEmployeeDto dto);

  /**
   * Convert list of DepartmentEmployeeDto to list of UserDepartmentInfo.
   *
   * @param departmentEmployees the list of DepartmentEmployeeDto
   * @return the corresponding list of UserDepartmentInfo
   */
  List<UserDepartmentInfo> toUserDepartmentInfos(List<DepartmentEmployeeDto> departmentEmployees);
}
