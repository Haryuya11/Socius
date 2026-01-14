package com.uit.sociusmvcapp.message.internal.converter;

import com.uit.sociusmvcapp.message.dto.ConversationDto;
import com.uit.sociusmvcapp.message.internal.domain.Conversation;
import com.uit.sociusmvcapp.shared.converter.BaseConverter;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/** Converter interface for transforming between Conversation entities and DTOs. */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConversationConverter extends BaseConverter<Conversation, ConversationDto> {}
