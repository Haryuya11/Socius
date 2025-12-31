package com.uit.sociusmvcapp.workforce.internal.converter;

import com.uit.sociusmvcapp.iam.dto.UserTeamInfo;
import com.uit.sociusmvcapp.workforce.dto.TeamEmployeeDto;
import com.uit.sociusmvcapp.workforce.dto.request.AssignEmployeeToTeamRequest;
import com.uit.sociusmvcapp.workforce.dto.request.TransferTeamEmployeeRequest;
import com.uit.sociusmvcapp.workforce.internal.domain.TeamEmployee;
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
  @Mapping(target = "teamCode", source = "team.teamCode")
  @Mapping(target = "teamName", source = "team.teamName")
  @Mapping(target = "departmentCode", source = "team.departmentCode")
  @Mapping(target = "clientId", source = "employee.clientId")
  @Mapping(target = "userId", source = "employee.userId")
  @Mapping(target = "firstName", source = "employee.firstName")
  @Mapping(target = "lastName", source = "employee.lastName")
  @Mapping(target = "systemRole", source = "employee.systemRole")
  @Mapping(target = "imageUrl", source = "employee.imageUrl")
  @Mapping(target = "salary", source = "employee.salary")
  TeamEmployeeDto entityToDto(TeamEmployee teamEmployee);

  /**
   * Convert list of TeamEmployee entities to list of TeamEmployeeDto.
   *
   * @param entities the list of TeamEmployee entities
   * @return the corresponding list of TeamEmployeeDto
   */
  List<TeamEmployeeDto> entitiesToDtos(List<TeamEmployee> entities);

  // ==================== DTO TO DTO (CONVERSION) ====================

  // ==================== DTO/REQUEST TO ENTITY (WRITE) ====================

  /**
   * Convert TeamEmployeeAddRequest to TeamEmployee entity for insertion.
   *
   * @param request the TeamEmployeeAddRequest
   * @param teamCode the team code
   * @return the corresponding TeamEmployee entity
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "employee.clientId", source = "request.employeeId")
  @Mapping(target = "roleCode", source = "request.roleCode")
  @Mapping(target = "isLeader", source = "request.isLeader")
  @Mapping(target = "team.teamCode", source = "teamCode")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "deleteFlag", ignore = true)
  TeamEmployee toEntity(AssignEmployeeToTeamRequest request, String teamCode);

  /**
   * Converts roleCode and isLeader to a TeamEmployeeAddRequest.
   *
   * @param request the TransferTeamEmployeeRequest
   * @return the corresponding TeamEmployeeAddRequest
   */
  AssignEmployeeToTeamRequest toTeamEmployeeAddRequest(TransferTeamEmployeeRequest request);

  /**
   * Convert list of TeamEmployeeDto to list of UserTeamInfo.
   *
   * @param teamEmployees the list of TeamEmployeeDto
   * @return the corresponding list of UserTeamInfo
   */
  List<UserTeamInfo> toUserTeamInfos(List<TeamEmployeeDto> teamEmployees);

  /**
   * Convert TeamEmployeeDto to UserTeamInfo.
   *
   * @param dto the TeamEmployeeDto
   * @return the corresponding UserTeamInfo
   */
  UserTeamInfo toUserTeamInfo(TeamEmployeeDto dto);
}
