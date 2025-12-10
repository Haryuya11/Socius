package com.uit.sociuscoremodules.teamemployee.converter;

import com.uit.sociuscoremodules.teamemployee.domain.TeamEmployee;
import com.uit.sociuscoremodules.teamemployee.dto.TeamEmployeeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** Converter for TeamEmployee entity and DTO transformations. */
@Mapper(componentModel = "spring")
public interface TeamEmployeeConverter {

  /**
   * Convert TeamEmployee entity to DTO.
   *
   * @param teamEmployee the TeamEmployee entity
   * @return the TeamEmployeeDto
   */
  @Mapping(target = "departmentCode", ignore = true)
  @Mapping(target = "userId", ignore = true)
  @Mapping(target = "firstName", ignore = true)
  @Mapping(target = "lastName", ignore = true)
  @Mapping(target = "imageUrl", ignore = true)
  TeamEmployeeDto entityToDto(TeamEmployee teamEmployee);

  /**
   * Convert TeamEmployeeDto to entity.
   *
   * @param teamEmployeeDto the TeamEmployeeDto
   * @return the TeamEmployee entity
   */
  @Mapping(target = "roleCode", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "deleteFlag", ignore = true)
  TeamEmployee dtoToEntity(TeamEmployeeDto teamEmployeeDto);
}
