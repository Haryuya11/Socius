package com.uit.sociusmvcapp.message.internal.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.uit.sociusmvcapp.message.dto.FileMetadataDto;
import com.uit.sociusmvcapp.message.dto.MessageDto;
import com.uit.sociusmvcapp.message.dto.request.SendMessageRequest;
import com.uit.sociusmvcapp.message.enums.MessageType;
import com.uit.sociusmvcapp.message.internal.component.MessageContentEncryptor;
import com.uit.sociusmvcapp.message.internal.domain.Message;
import com.uit.sociusmvcapp.shared.enums.DeleteFlagEnums;
import com.uit.sociusmvcapp.shared.utils.CommonUtils;
import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

/**
 * Converter interface for transforming between Message entities and DTOs. Uses AES-256-GCM
 * encryption for secure content storage.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MessageConverter {

  /**
   * Converts a {@link Message} entity to a {@link MessageDto}. The content is decrypted using
   * AES-256-GCM.
   *
   * @param entity the message entity
   * @param encryptor the content encryptor for AES-256-GCM decryption
   * @return the corresponding {@link MessageDto}
   */
  @Mapping(target = "metadata", source = "metadataJson", qualifiedByName = "jsonToMetadata")
  @Mapping(target = "content", source = "content", qualifiedByName = "decryptContent")
  MessageDto entityToDto(Message entity, @Context MessageContentEncryptor encryptor);

  /**
   * Converts a list of {@link Message} entities to a list of {@link MessageDto}s. The content is
   * decrypted using AES-256-GCM for each message.
   *
   * @param entities the list of message entities
   * @param encryptor the content encryptor for AES-256-GCM decryption
   * @return the list of corresponding {@link MessageDto}s
   */
  List<MessageDto> entitiesToDtos(
      List<Message> entities, @Context MessageContentEncryptor encryptor);

  /**
   * Converts a {@link MessageDto} to a {@link Message} entity.
   *
   * @param dto the message DTO
   * @return the corresponding {@link Message} entity
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "deleteFlag", ignore = true)
  @Mapping(target = "metadataJson", source = "metadata", qualifiedByName = "metadataToJson")
  Message dtoToEntity(MessageDto dto);

  /**
   * Converts a {@link SendMessageRequest} to a {@link Message} entity. The content is encrypted
   * using AES-256-GCM before storage.
   *
   * @param request the send message request
   * @param messageId the unique message ID
   * @param senderId the sender ID
   * @param encryptor the content encryptor for AES-256-GCM encryption
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
  @Mapping(target = "content", source = "request.content", qualifiedByName = "encryptContent")
  Message sendRequestToEntity(
      SendMessageRequest request,
      String messageId,
      String senderId,
      @Context MessageContentEncryptor encryptor);

  /**
   * Encrypts message content for secure database storage.
   *
   * @param content the plaintext content
   * @param encryptor the encryption component
   * @return the encrypted content string
   */
  @Named("encryptContent")
  default String encryptContent(String content, @Context MessageContentEncryptor encryptor) {
    return encryptor.encryptContent(content);
  }

  /**
   * Decrypts message content from the database.
   *
   * @param encryptedContent the encrypted content string
   * @param encryptor the encryption component
   * @return the decrypted plaintext content
   */
  @Named("decryptContent")
  default String decryptContent(
      String encryptedContent, @Context MessageContentEncryptor encryptor) {
    return encryptor.decryptContent(encryptedContent);
  }

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

  /**
   * Post-mapping hook to transform deleted messages into placeholder messages. For deleted
   * messages, sets messageType to DELETED and clears content and metadata.
   *
   * @param entity the source message entity
   * @param dto the target message DTO
   */
  @AfterMapping
  default void handleDeletedMessage(Message entity, @MappingTarget MessageDto dto) {
    if (entity != null
        && entity.getDeleteFlag() != null
        && entity.getDeleteFlag().equals(DeleteFlagEnums.DELETED.getValue().shortValue())) {
      dto.setMessageType(MessageType.DELETED.getCode());
      dto.setContent(null);
      dto.setMetadata(null);
    }
  }
}
