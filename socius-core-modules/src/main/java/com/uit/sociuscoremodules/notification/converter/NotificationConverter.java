package com.uit.sociuscoremodules.notification.converter;

import com.uit.sociuscoremodules.notification.domain.Notification;
import com.uit.sociuscoremodules.notification.dto.NotificationDto;
import com.uit.sociuscoremodules.notification.dto.PayloadDto;
import com.uit.sociuscoremodules.notification.request.NotificationCreateRequest;
import com.uit.sociuscoremodules.shared.utils.CommonUtils;
import java.time.LocalDateTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/** Converter interface for transforming between Notification entities and DTOs. */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationConverter {
  /**
   * Converts a {@link NotificationCreateRequest} to a {@link Notification} entity.
   *
   * @param request the notification creation request
   * @param dateTime the creation timestamp
   * @return the corresponding {@link Notification} entity
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", source = "dateTime")
  @Mapping(target = "payload", expression = "java(payloadToJson(request.getPayload()))")
  @Mapping(
      target = "deliveryType",
      expression =
          "java(com.uit.sociuscoremodules.notification.enums.DeliveryType.NOTIFICATION.getCode())")
  @Mapping(
      target = "isRead",
      expression =
          "java(com.uit.sociuscoremodules.notification.enums.NotificationStatus.UNREAD.getCode())")
  Notification fromCreateRequest(NotificationCreateRequest request, LocalDateTime dateTime);

  /**
   * Converts a {@link Notification} entity to a {@link NotificationDto}.
   *
   * @param request the Notification entity
   * @return the corresponding {@link NotificationDto}
   */
  @Mapping(target = "payload", expression = "java(jsonToPayload(request.getPayload()))")
  NotificationDto toDto(Notification request);

  /**
   * Converts the {@link PayloadDto} to its JSON string representation.
   *
   * @param payloadDto the {@link PayloadDto} to be converted
   * @return the JSON string representation of the payload
   */
  default String payloadToJson(PayloadDto payloadDto) {
    return CommonUtils.serializeToJson(payloadDto);
  }

  /**
   * Converts a JSON string representation of the payload to a {@link PayloadDto}.
   *
   * @param payload the JSON string representation of the payload
   * @return the corresponding {@link PayloadDto}
   */
  default PayloadDto jsonToPayload(String payload) {
    return CommonUtils.deserializeFromJson(payload, PayloadDto.class);
  }
}
