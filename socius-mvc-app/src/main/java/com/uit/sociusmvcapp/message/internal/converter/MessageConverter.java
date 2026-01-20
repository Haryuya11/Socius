package com.uit.sociusmvcapp.message.internal.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.uit.sociusmvcapp.message.dto.FileMetadataDto;
import com.uit.sociusmvcapp.message.dto.MessageDto;
import com.uit.sociusmvcapp.message.dto.request.SendMessageRequest;
import com.uit.sociusmvcapp.message.internal.domain.Message;
import com.uit.sociusmvcapp.shared.converter.BaseConverter;
import com.uit.sociusmvcapp.shared.utils.CommonUtils;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

/** Converter interface for transforming between Message entities and DTOs. */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MessageConverter extends BaseConverter<Message, MessageDto> {

  /**
   * Converts a {@link Message} entity to a {@link MessageDto}.
   *
   * @param entity the message entity
   * @return the corresponding {@link MessageDto}
   */
  @Override
  @Mapping(target = "metadata", source = "metadataJson", qualifiedByName = "jsonToMetadata")
  MessageDto entityToDto(Message entity);

  @Override
  @Mapping(target = "metadataJson", source = "metadata", qualifiedByName = "metadataToJson")
  Message dtoToEntity(MessageDto dto);

  /**
   * Converts a {@link SendMessageRequest} to a {@link Message} entity.
   *
   * @param request the send message request
   * @param messageId the unique message ID
   * @param senderId the sender ID
   * @return the corresponding {@link Message} entity
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "deleteFlag", ignore = true)
  @Mapping(target = "messageId", source = "messageId")
  @Mapping(target = "senderId", source = "senderId")
  @Mapping(target = "metadataJson", source = "request.metadata", qualifiedByName = "metadataToJson")
  Message sendRequestToEntity(SendMessageRequest request, String messageId, String senderId);

  /**
   * Converts a JSON string to a list of file metadata.
   *
   * @param json the JSON string
   * @return the list of FileMetadataDto
   */
  @Named("jsonToMetadata")
  default List<FileMetadataDto> jsonToMetadata(String json) {
    return CommonUtils.deserializeFromJson(json, new TypeReference<List<FileMetadataDto>>() {});
  }

  /**
   * Converts a list of file metadata to a JSON string.
   *
   * @param metadata the list of file metadata
   * @return the JSON string
   */
  @Named("metadataToJson")
  default String metadataToJson(List<FileMetadataDto> metadata) {
    return CommonUtils.serializeToJson(metadata);
  }
}
