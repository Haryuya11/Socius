package com.uit.sociusmvcapp.team.internal.converter;

import com.uit.sociusmvcapp.team.dto.TeamDto;
import com.uit.sociusmvcapp.team.dto.request.TeamCreateRequest;
import com.uit.sociusmvcapp.team.dto.request.TeamUpdateRequest;
import com.uit.sociusmvcapp.team.internal.domain.Team;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** Converter for Team entity and DTO objects. */
@Mapper(componentModel = "spring")
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
}
