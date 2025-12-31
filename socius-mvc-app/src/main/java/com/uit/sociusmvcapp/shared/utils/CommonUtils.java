package com.uit.sociusmvcapp.shared.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/** Common utility class for shared functions. */
@Slf4j
public class CommonUtils {

  /** ObjectMapper instance for JSON processing. */
  private static final ObjectMapper OBJECT_MAPPER =
      new ObjectMapper().registerModule(new JavaTimeModule());

  /** Private constructor to prevent instantiation. */
  private CommonUtils() {}

  /**
   * Serializes an object to a JSON string.
   *
   * @param value the object to serialize
   * @return the JSON string representation of the object or null if serialization fails
   */
  public static String serializeToJson(Object value) {
    try {
      return OBJECT_MAPPER.writeValueAsString(value);
    } catch (Exception e) {
      log.error("Error serializing notification: {}", e.getMessage(), e);
      return null;
    }
  }

  /**
   * Deserializes a JSON string to an object of the specified class.
   *
   * @param json the JSON string to deserialize
   * @param clazz the target class for deserialization
   * @param <T> the type of the target class
   * @return the deserialized object, or null if deserialization fails
   */
  public static <T> T deserializeFromJson(String json, Class<T> clazz) {
    if (StringUtils.isEmpty(json)) {
      return null;
    }
    try {
      return OBJECT_MAPPER.readValue(json, clazz);
    } catch (Exception e) {
      log.error("Error deserializing object of type {}: {}", clazz.getName(), e.getMessage(), e);
      return null;
    }
  }
}
