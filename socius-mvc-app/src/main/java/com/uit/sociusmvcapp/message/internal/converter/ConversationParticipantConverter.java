package com.uit.sociusmvcapp.message.internal.converter;

import com.uit.sociusmvcapp.message.dto.ConversationParticipantDto;
import com.uit.sociusmvcapp.message.internal.domain.ConversationParticipant;
import com.uit.sociusmvcapp.shared.converter.BaseConverter;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/** Converter interface for transforming between ConversationParticipant entities and DTOs. */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConversationParticipantConverter
    extends BaseConverter<ConversationParticipant, ConversationParticipantDto> {}
