package com.uit.sociusmvcapp.notification.internal.converter;

import com.uit.sociusmvcapp.notification.dto.NotificationDto;
import com.uit.sociusmvcapp.notification.dto.PayloadDto;
import com.uit.sociusmvcapp.notification.dto.request.NotificationCreateRequest;
import com.uit.sociusmvcapp.notification.internal.component.NotificationPayloadEncryptor;
import com.uit.sociusmvcapp.notification.internal.domain.Notification;
import java.time.LocalDateTime;
import java.util.List;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

/**
 * Converter interface for transforming between Notification entities and DTOs. Uses AES-256-GCM
 * encryption for secure payload storage.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationConverter {
  /**
   * Converts a {@link NotificationCreateRequest} to a {@link Notification} entity. The payload is
   * encrypted using AES-256-GCM before storage.
   *
   * @param request the notification creation request
   * @param dateTime the creation timestamp
   * @param encryptor the payload encryptor for AES-256-GCM encryption
   * @return the corresponding {@link Notification} entity
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", source = "dateTime")
  @Mapping(target = "payload", source = "request.payload", qualifiedByName = "encryptPayload")
  @Mapping(
      target = "deliveryType",
      expression =
          "java(com.uit.sociusmvcapp.notification.enums.DeliveryType.NOTIFICATION.getCode())")
  @Mapping(
      target = "isRead",
      expression =
          "java(com.uit.sociusmvcapp.notification.enums.NotificationStatus.UNREAD.getCode())")
  Notification fromCreateRequest(
      NotificationCreateRequest request,
      LocalDateTime dateTime,
      @Context NotificationPayloadEncryptor encryptor);

  /**
   * Converts a {@link Notification} entity to a {@link NotificationDto}. The payload is decrypted
   * using AES-256-GCM after retrieval.
   *
   * @param entity the notification entity
   * @param encryptor the payload encryptor for AES-256-GCM decryption
   * @return the corresponding {@link NotificationDto}
   */
  @Mapping(target = "payload", source = "payload", qualifiedByName = "decryptPayload")
  NotificationDto entityToDto(Notification entity, @Context NotificationPayloadEncryptor encryptor);

  /**
   * Converts a list of {@link Notification} entities to a list of {@link NotificationDto}s. The
   * payload is decrypted using AES-256-GCM for each notification.
   *
   * @param entities the list of notification entities
   * @param encryptor the payload encryptor for AES-256-GCM decryption
   * @return the list of corresponding {@link NotificationDto}s
   */
  List<NotificationDto> entitiesToDtos(
      List<Notification> entities, @Context NotificationPayloadEncryptor encryptor);

  /**
   * Encrypts a PayloadDto for secure database storage.
   *
   * @param payloadDto the payload to encrypt
   * @param encryptor the encryption component
   * @return the encrypted payload string
   */
  @Named("encryptPayload")
  default String encryptPayload(
      PayloadDto payloadDto, @Context NotificationPayloadEncryptor encryptor) {
    return encryptor.encryptPayload(payloadDto);
  }

  /**
   * Decrypts an encrypted payload from the database.
   *
   * @param encryptedPayload the encrypted payload string
   * @param encryptor the encryption component
   * @return the decrypted PayloadDto
   */
  @Named("decryptPayload")
  default PayloadDto decryptPayload(
      String encryptedPayload, @Context NotificationPayloadEncryptor encryptor) {
    return encryptor.decryptPayload(encryptedPayload);
  }
}
