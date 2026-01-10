package com.uit.sociusmvcapp.team.internal.converter;

import com.uit.sociusmvcapp.shared.converter.BaseConverter;
import com.uit.sociusmvcapp.team.dto.SearchTeamDto;
import com.uit.sociusmvcapp.team.dto.TeamDto;
import com.uit.sociusmvcapp.team.dto.request.CreateTeamRequest;
import com.uit.sociusmvcapp.team.dto.request.UpdateTeamRequest;
import com.uit.sociusmvcapp.team.internal.domain.Team;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** Converter for Team entity and DTO objects. */
@Mapper(componentModel = "spring")
public interface TeamConverter extends BaseConverter<Team, TeamDto> {

  /**
   * Converts a Team entity to a SearchTeamDto.
   *
   * @param team the Team entity
   * @return the corresponding SearchTeamDto
   */
  SearchTeamDto entityToSearchDto(Team team);

  /**
   * Converts a list of Team entities to a list of SearchTeamDtos.
   *
   * @param teams the list of Team entities
   * @return the corresponding list of SearchTeamDtos
   */
  List<SearchTeamDto> entitiesToSearchDtos(List<Team> teams);

  /**
   * Converts a CreateTeamRequest to a Team entity.
   *
   * @param request the CreateTeamRequest
   * @return the corresponding Team entity
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "deleteFlag", ignore = true)
  Team createRequestToEntity(CreateTeamRequest request);

  /**
   * Converts an UpdateTeamRequest to a Team entity.
   *
   * @param request the UpdateTeamRequest
   * @return the corresponding Team entity
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "teamCode", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "deleteFlag", ignore = true)
  Team updateRequestToEntity(UpdateTeamRequest request);
}
