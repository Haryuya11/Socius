package com.uit.sociusmvcapp.workforce.internal.converter;

import com.uit.sociusmvcapp.iam.dto.UserTeamInfo;
import com.uit.sociusmvcapp.shared.converter.BaseConverter;
import com.uit.sociusmvcapp.workforce.dto.TeamEmployeeDto;
import com.uit.sociusmvcapp.workforce.dto.request.AddEmployeeToTeamRequest;
import com.uit.sociusmvcapp.workforce.dto.request.TransferTeamEmployeeRequest;
import com.uit.sociusmvcapp.workforce.internal.domain.TeamEmployee;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** Converter for TeamEmployee entity and DTO transformations. */
@Mapper(componentModel = "spring")
public interface TeamEmployeeConverter extends BaseConverter<TeamEmployee, TeamEmployeeDto> {

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
  TeamEmployee toEntity(AddEmployeeToTeamRequest request, String teamCode);

  /**
   * Converts roleCode and isLeader to a TeamEmployeeAddRequest.
   *
   * @param request the TransferTeamEmployeeRequest
   * @return the corresponding TeamEmployeeAddRequest
   */
  AddEmployeeToTeamRequest toTeamEmployeeAddRequest(TransferTeamEmployeeRequest request);

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
  @Mapping(target = "teamCode", source = "team.teamCode")
  @Mapping(target = "teamName", source = "team.teamName")
  UserTeamInfo toUserTeamInfo(TeamEmployeeDto dto);
}
