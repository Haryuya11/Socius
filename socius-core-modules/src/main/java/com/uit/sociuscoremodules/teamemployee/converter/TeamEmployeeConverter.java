package com.uit.sociuscoremodules.teamemployee.converter;

import com.uit.sociuscoremodules.employee.dto.EmployeeDto;
import com.uit.sociuscoremodules.teamemployee.domain.TeamEmployee;
import com.uit.sociuscoremodules.teamemployee.dto.SearchTeamEmployeeDto;
import com.uit.sociuscoremodules.teamemployee.dto.TeamEmployeeDto;
import com.uit.sociuscoremodules.teamemployee.request.TeamEmployeeAddRequest;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** Converter for TeamEmployee entity and DTO transformations. */
@Mapper(componentModel = "spring")
public interface TeamEmployeeConverter {

  // ==================== ENTITY TO DTO (READ) ====================

  /**
   * Convert TeamEmployee entity to TeamEmployeeDto.
   *
   * @param teamEmployee the TeamEmployee entity
   * @return the corresponding TeamEmployeeDto
   */
  @Mapping(target = "employeeId", source = "employeeId")
  @Mapping(target = "teamCode", source = "teamCode")
  @Mapping(target = "roleCode", source = "roleCode")
  @Mapping(target = "isLeader", source = "isLeader")
  @Mapping(target = "firstName", source = "employeeInfo.firstName")
  @Mapping(target = "lastName", source = "employeeInfo.lastName")
  @Mapping(target = "imageUrl", source = "employeeInfo.imageUrl")
  @Mapping(target = "departmentCode", source = "teamInfo.departmentCode")
  TeamEmployeeDto entityToDto(TeamEmployee teamEmployee);

  /**
   * Convert list of TeamEmployee entities to list of TeamEmployeeDto.
   *
   * @param entities the list of TeamEmployee entities
   * @return the corresponding list of TeamEmployeeDto
   */
  List<TeamEmployeeDto> entitiesToDtos(List<TeamEmployee> entities);

  // ==================== DTO TO DTO (CONVERSION) ====================

  /**
   * Convert TeamEmployeeDto to SearchTeamEmployeeDto.
   *
   * @param dto the TeamEmployeeDto
   * @return the corresponding SearchTeamEmployeeDto
   */
  @Mapping(target = "teamCode", source = "teamCode")
  @Mapping(target = "employeeId", source = "employeeId")
  @Mapping(target = "firstName", source = "firstName")
  @Mapping(target = "lastName", source = "lastName")
  @Mapping(target = "roleCode", source = "roleCode")
  SearchTeamEmployeeDto dtoToSearchDto(TeamEmployeeDto dto);

  /**
   * Convert list of TeamEmployeeDto to list of SearchTeamEmployeeDto.
   *
   * @param dtos the list of TeamEmployeeDto
   * @return the corresponding list of SearchTeamEmployeeDto
   */
  List<SearchTeamEmployeeDto> dtosToSearchDtos(List<TeamEmployeeDto> dtos);

  /**
   * Convert TeamEmployeeDto to EmployeeDto.
   *
   * @param teamEmployeeDto the TeamEmployeeDto
   * @return the corresponding EmployeeDto
   */
  @Mapping(target = "clientId", source = "employeeId")
  @Mapping(target = "firstName", source = "firstName")
  @Mapping(target = "lastName", source = "lastName")
  @Mapping(target = "imageUrl", source = "imageUrl")
  @Mapping(target = "userId", ignore = true)
  @Mapping(target = "systemRole", ignore = true)
  @Mapping(target = "salary", ignore = true)
  @Mapping(target = "departments", ignore = true)
  @Mapping(target = "teams", ignore = true)
  EmployeeDto toEmployeeDto(TeamEmployeeDto teamEmployeeDto);

  /**
   * Convert list of TeamEmployeeDto to list of EmployeeDto.
   *
   * @param teamEmployeeDtos the list of TeamEmployeeDto
   * @return the corresponding list of EmployeeDto
   */
  List<EmployeeDto> toEmployeeDtos(List<TeamEmployeeDto> teamEmployeeDtos);

  // ==================== DTO/REQUEST TO ENTITY (WRITE) ====================

  /**
   * Convert TeamEmployeeAddRequest to TeamEmployee entity for insertion.
   *
   * @param teamCode the team code
   * @param request the TeamEmployeeAddRequest
   * @return the corresponding TeamEmployee entity
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "teamCode", source = "teamCode")
  @Mapping(target = "employeeId", source = "request.employeeId")
  @Mapping(target = "roleCode", source = "request.roleCode")
  @Mapping(target = "isLeader", source = "request.isLeader")
  @Mapping(target = "employeeInfo", ignore = true)
  @Mapping(target = "teamInfo", ignore = true)
  @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
  @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "deleteFlag", constant = "0")
  TeamEmployee toEntity(String teamCode, TeamEmployeeAddRequest request);
}
