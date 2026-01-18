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

  /** Converts a {@link DepartmentEmployee} entity to a {@link DepartmentEmployeeDto}. */
  @Override
  DepartmentEmployeeDto entityToDto(DepartmentEmployee entity);

  @Override
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "deleteFlag", ignore = true)
  DepartmentEmployee dtoToEntity(DepartmentEmployeeDto dto);

  /** Converts an EmployeeAddRequest to a DepartmentEmployees entity. */
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
   * Converts roleCode and isPrimary to an AssignEmployeeToDepartmentRequest.
   *
   * @param request the TransferEmployeeRequest
   * @return the corresponding AssignEmployeeToDepartmentRequest
   */
  AssignEmployeeToDepartmentRequest toAssignEmployeeToDepartmentRequest(
      TransferEmployeeRequest request);

  /**
   * Convert DepartmentEmployeeDto to UserDepartmentInfo.
   *
   * <p>Added mappings because DTO is now nested, but UserDepartmentInfo likely expects flat fields.
   */
  @Mapping(target = "departmentCode", source = "department.departmentCode")
  @Mapping(target = "departmentName", source = "department.departmentName")
  UserDepartmentInfo toUserDepartmentInfo(DepartmentEmployeeDto dto);

  /**
   * Convert list of DepartmentEmployeeDto to list of UserDepartmentInfo.
   *
   * @param departmentEmployees the list of DepartmentEmployeeDto
   * @return the corresponding list of UserDepartmentInfo
   */
  List<UserDepartmentInfo> toUserDepartmentInfos(List<DepartmentEmployeeDto> departmentEmployees);
}
