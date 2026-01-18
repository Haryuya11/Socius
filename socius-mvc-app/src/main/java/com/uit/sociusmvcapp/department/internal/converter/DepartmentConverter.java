package com.uit.sociusmvcapp.department.internal.converter;

import com.uit.sociusmvcapp.department.dto.DepartmentDto;
import com.uit.sociusmvcapp.department.dto.SearchDepartmentDto;
import com.uit.sociusmvcapp.department.dto.request.CreateDepartmentRequest;
import com.uit.sociusmvcapp.department.dto.request.UpdateDepartmentRequest;
import com.uit.sociusmvcapp.department.internal.domain.Department;
import com.uit.sociusmvcapp.shared.converter.BaseConverter;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** Converter for Department entity, DTO, and request objects. */
@Mapper(componentModel = "spring")
public interface DepartmentConverter extends BaseConverter<Department, DepartmentDto> {
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
  Department createRequestToEntity(CreateDepartmentRequest request);

  /**
   * Converts a Department entity to a SearchDepartmentDto.
   *
   * @param department The Department entity.
   * @return The corresponding SearchDepartmentDto.
   */
  SearchDepartmentDto entityToSearchDto(Department department);

  /**
   * Converts a list of Department entities to a list of SearchDepartmentDtos.
   *
   * @param departments The list of Department entities.
   * @return The corresponding list of SearchDepartmentDtos.
   */
  List<SearchDepartmentDto> entitiesToSearchDto(List<Department> departments);

  /**
   * Converts a DepartmentUpdateRequest to a Department entity for updating.
   *
   * @param request The department update request DTO.
   * @param departmentCode The code of the department to be updated.
   * @return The corresponding Department entity.
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "deleteFlag", ignore = true)
  @Mapping(target = "departmentCode", source = "departmentCode")
  @Mapping(target = "departmentName", source = "request.departmentName")
  Department updateRequestToEntity(UpdateDepartmentRequest request, String departmentCode);
}
