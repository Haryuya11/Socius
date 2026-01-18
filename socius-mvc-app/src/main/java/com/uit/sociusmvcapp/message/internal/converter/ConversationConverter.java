package com.uit.sociusmvcapp.message.internal.converter;

import com.uit.sociusmvcapp.message.dto.ConversationDto;
import com.uit.sociusmvcapp.message.dto.request.UpdateConversationRequest;
import com.uit.sociusmvcapp.message.internal.domain.Conversation;
import com.uit.sociusmvcapp.shared.converter.BaseConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/** Converter interface for transforming between Conversation entities and DTOs. */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConversationConverter extends BaseConverter<Conversation, ConversationDto> {

  /**
   * Converts a {@link UpdateConversationRequest} to a {@link Conversation} entity.
   *
   * @param request the update conversation request
   * @return the corresponding {@link Conversation} entity
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "deleteFlag", ignore = true)
  @Mapping(target = "conversationId", ignore = true)
  @Mapping(target = "type", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "lastMessageId", ignore = true)
  @Mapping(target = "lastMessageAt", ignore = true)
  Conversation updateRequestToEntity(UpdateConversationRequest request);
}
