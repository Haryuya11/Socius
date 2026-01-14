package com.uit.sociusmvcapp.message.internal.converter;

import com.uit.sociusmvcapp.message.dto.MessageReactionDto;
import com.uit.sociusmvcapp.message.internal.domain.MessageReaction;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/** Converter interface for transforming between MessageReaction entities and DTOs. */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MessageReactionConverter {

  /**
   * Converts a {@link MessageReaction} entity to a {@link MessageReactionDto}.
   *
   * @param entity the message reaction entity
   * @return the corresponding {@link MessageReactionDto}
   */
  MessageReactionDto entityToDto(MessageReaction entity);

  /**
   * Converts a list of entities to a list of DTOs.
   *
   * @param entities the list of entities
   * @return the list of DTOs
   */
  List<MessageReactionDto> entitiesToDtos(List<MessageReaction> entities);
}
