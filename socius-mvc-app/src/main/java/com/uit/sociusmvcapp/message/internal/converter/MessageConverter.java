package com.uit.sociusmvcapp.message.internal.converter;

import com.uit.sociusmvcapp.message.dto.MessageDto;
import com.uit.sociusmvcapp.message.internal.domain.Message;
import com.uit.sociusmvcapp.shared.converter.BaseConverter;
import com.uit.sociusmvcapp.shared.utils.CommonUtils;
import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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
  @Mapping(target = "metadata", expression = "java(jsonToMetadata(entity.getMetadataJson()))")
  MessageDto entityToDto(Message entity);

  /**
   * Converts a JSON string to a metadata map.
   *
   * @param json the JSON string
   * @return the metadata map
   */
  @SuppressWarnings("unchecked")
  default Map<String, Object> jsonToMetadata(String json) {
    return CommonUtils.deserializeFromJson(json, Map.class);
  }

  /**
   * Converts a metadata object to a JSON string.
   *
   * @param metadata the metadata object
   * @return the JSON string
   */
  default String metadataToJson(Object metadata) {
    return CommonUtils.serializeToJson(metadata);
  }
}
