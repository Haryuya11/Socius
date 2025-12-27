package com.uit.sociusmvcapp.notification.internal.converter;

import com.uit.sociusmvcapp.notification.dto.NotificationDto;
import com.uit.sociusmvcapp.notification.dto.PayloadDto;
import com.uit.sociusmvcapp.notification.dto.request.NotificationCreateRequest;
import com.uit.sociusmvcapp.notification.internal.domain.Notification;
import com.uit.sociusmvcapp.shared.converter.BaseConverter;
import com.uit.sociusmvcapp.shared.utils.CommonUtils;
import java.time.LocalDateTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/** Converter interface for transforming between Notification entities and DTOs. */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationConverter extends BaseConverter<Notification, NotificationDto> {
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
          "java(com.uit.sociusmvcapp.notification.enums.DeliveryType.NOTIFICATION.getCode())")
  @Mapping(
      target = "isRead",
      expression =
          "java(com.uit.sociusmvcapp.notification.enums.NotificationStatus.UNREAD.getCode())")
  Notification fromCreateRequest(NotificationCreateRequest request, LocalDateTime dateTime);

  /**
   * Converts a {@link Notification} entity to a {@link NotificationDto}.
   *
   * @param entity the notification entity
   * @return the corresponding {@link NotificationDto}
   */
  @Override
  @Mapping(target = "payload", expression = "java(jsonToPayload(entity.getPayload()))")
  NotificationDto entityToDto(Notification entity);

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
