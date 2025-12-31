package com.uit.sociusmvcapp.shared.converter;

import com.uit.sociusmvcapp.shared.domain.BaseEntity;
import java.util.List;
import org.mapstruct.Mapping;

/** Generic converter interface for converting between Entity and DTO objects. */
public interface BaseConverter<E extends BaseEntity, D> {

  /**
   * Convert Entity -> DTO.
   *
   * @param entity the entity to convert
   * @return the converted DTO
   */
  D entityToDto(E entity);

  /**
   * Convert DTO -> Entity.
   *
   * @param dto the DTO to convert
   * @return the converted Entity
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "deleteFlag", ignore = true)
  E dtoToEntity(D dto);

  /**
   * Converts a list of entities to a list of DTOs.
   *
   * @param entities the list of entities to convert
   * @return the list of converted DTOs
   */
  List<D> entitiesToDtos(List<E> entities);

  /**
   * Converts a list of DTOs to a list of Entities.
   *
   * @param dtos the list of DTOs to convert
   * @return the list of converted Entities
   */
  List<E> dtosToEntities(List<D> dtos);
}
