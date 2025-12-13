package com.uit.sociuscoremodules.team.converter;

import com.uit.sociuscoremodules.employee.domain.Employee;
import com.uit.sociuscoremodules.team.domain.Team;
import com.uit.sociuscoremodules.team.dto.SearchTeamDto;
import com.uit.sociuscoremodules.team.dto.TeamDto;
import com.uit.sociuscoremodules.team.dto.TeamMemberDto;
import com.uit.sociuscoremodules.team.request.TeamCreateRequest;
import com.uit.sociuscoremodules.team.request.TeamUpdateRequest;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

/** Converter for Team entity and DTO objects. */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamConverter {

  /**
   * Converts a Team entity to a TeamDto.
   *
   * @param team the Team entity
   * @return the corresponding TeamDto
   */
  TeamDto entityToDto(Team team);

  /**
   * Converts a list of Team entities to a list of TeamDtos.
   *
   * @param teams the list of Team entities
   * @return the corresponding list of TeamDtos
   */
  List<TeamDto> entitiesToDtos(List<Team> teams);

  /**
   * Converts a Team entity to SearchTeamDto.
   *
   * @param team the Team entity
   * @return the corresponding SearchTeamDto
   */
  @Mapping(target = "teamLead", source = "teamLead", qualifiedByName = "convertTeamLead")
  SearchTeamDto entityToSearchDto(Team team);

  /**
   * Converts a list of Team entities to a list of SearchTeamDto.
   *
   * @param teams the list of Team entities
   * @return the corresponding list of SearchTeamDto
   */
  List<SearchTeamDto> entitiesToSearchDtos(List<Team> teams);

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
  Team createRequestToEntity(TeamCreateRequest request);

  /**
   * Converts a TeamUpdateRequest to a Team entity.
   *
   * @param teamUpdateRequest the TeamUpdateRequest
   * @return the corresponding Team entity
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "teamCode", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "deleteFlag", ignore = true)
  Team updateRequestToEntity(TeamUpdateRequest teamUpdateRequest);

  /**
   * Converts an Employee entity to a TeamMemberDto.
   *
   * @param employee the Employee entity
   * @return the corresponding TeamMemberDto
   */
  @Named("convertMembers")
  default TeamMemberDto convertMembers(Employee employee) {
    if (employee == null) {
      return null;
    }
    return TeamMemberDto.builder()
        .clientId(employee.getClientId())
        .userId(employee.getUserId())
        .firstName(employee.getFirstName())
        .lastName(employee.getLastName())
        .imageUrl(employee.getImageUrl())
        .build();
  }

  /**
   * Converts an Employee entity to a TeamMemberDto for team lead.
   *
   * @param employee the Employee entity
   * @return the corresponding TeamMemberDto
   */
  @Named("convertTeamLead")
  default TeamMemberDto convertTeamLead(Employee employee) {
    if (employee == null) {
      return null;
    }
    return TeamMemberDto.builder()
        .clientId(employee.getClientId())
        .userId(employee.getUserId())
        .firstName(employee.getFirstName())
        .lastName(employee.getLastName())
        .imageUrl(employee.getImageUrl())
        .build();
  }
}
