package com.uit.sociuscoremodules.team.converter;

import com.uit.sociuscoremodules.employee.domain.Employee;
import com.uit.sociuscoremodules.team.domain.Team;
import com.uit.sociuscoremodules.team.dto.TeamDto;
import com.uit.sociuscoremodules.team.dto.TeamMemberDto;
import com.uit.sociuscoremodules.team.request.TeamCreateRequest;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/** Converter for Team entity and DTO objects. */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TeamConverter {

  /**
   * Converts a Team entity to a TeamDto.
   *
   * @param team the Team entity
   * @return the corresponding TeamDto
   */
  public abstract TeamDto entityToDto(Team team);

  /**
   * Converts a list of Team entities to a list of TeamDtos.
   *
   * @param teams the list of Team entities
   * @return the corresponding list of TeamDtos
   */
  public abstract List<TeamDto> entityToDto(List<Team> teams);

  /**
   * Converts a TeamCreateRequest to a Team entity.
   *
   * @param request the TeamCreateRequest
   * @return the corresponding Team entity
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
  @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "deleteFlag", constant = "0")
  public abstract Team requestToEntity(TeamCreateRequest request);

  /**
   * Converts Employee to TeamMemberDto (excluding sensitive information).
   *
   * @param employee the Employee entity
   * @return the TeamMemberDto
   */
  public TeamMemberDto employeeToTeamMemberDto(Employee employee) {
    if (employee == null) {
      return null;
    }
    TeamMemberDto dto = new TeamMemberDto();
    dto.setClientId(employee.getClientId());
    dto.setUserId(employee.getUserId());
    dto.setFirstName(employee.getFirstName());
    dto.setLastName(employee.getLastName());
    dto.setImageUrl(employee.getImageUrl());
    return dto;
  }

  /**
   * Enriches TeamDto with team lead and members information.
   *
   * @param teamDto the TeamDto to enrich
   * @param teamLead the team lead employee
   * @param members the list of team members
   * @return the enriched TeamDto
   */
  public TeamDto enrichTeamDto(TeamDto teamDto, Employee teamLead, List<Employee> members) {
    if (teamLead != null) {
      teamDto.setTeamLead(employeeToTeamMemberDto(teamLead));
    }
    if (members != null && !members.isEmpty()) {
      teamDto.setMembers(
          members.stream().map(this::employeeToTeamMemberDto).collect(Collectors.toList()));
    }
    return teamDto;
  }
}
