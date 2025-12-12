package com.uit.sociuscoremodules.teamemployee.converter;

import com.uit.sociuscoremodules.employee.domain.Employee;
import com.uit.sociuscoremodules.team.domain.Team;
import com.uit.sociuscoremodules.teamemployee.domain.TeamEmployee;
import com.uit.sociuscoremodules.teamemployee.dto.SearchTeamEmployeeDto;
import com.uit.sociuscoremodules.teamemployee.dto.TeamEmployeeDto;
import java.util.List;
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
   * Converts a list of TeamEmployeeDto to a list of SearchTeamEmployeeDto.
   *
   * @param dtos the list of TeamEmployeeDto
   * @return the corresponding list of SearchTeamEmployeeDto
   */
  List<SearchTeamEmployeeDto> dtosToSearchDtos(List<TeamEmployeeDto> dtos);

  /**
   * Converts a TeamEmployeeDto to SearchTeamEmployeeDto.
   *
   * @param dto the TeamEmployeeDto
   * @return the corresponding SearchTeamEmployeeDto
   */
  SearchTeamEmployeeDto dtoToSearchDto(TeamEmployeeDto dto);

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

  /** Convert to DTO from both TeamEmployee and Employee entities. */
  @Mapping(source = "teamEmployee.id", target = "id")
  @Mapping(source = "teamEmployee.teamCode", target = "teamCode")
  @Mapping(source = "teamEmployee.roleCode", target = "roleCode")
  @Mapping(source = "teamEmployee.isLeader", target = "isLeader")
  @Mapping(source = "employee.clientId", target = "employeeId")
  @Mapping(source = "employee.userId", target = "userId")
  @Mapping(source = "employee.firstName", target = "firstName")
  @Mapping(source = "employee.lastName", target = "lastName")
  @Mapping(source = "employee.imageUrl", target = "imageUrl")
  @Mapping(source = "team.departmentCode", target = "departmentCode")
  TeamEmployeeDto toDto(TeamEmployee teamEmployee, Employee employee, Team team);
}
