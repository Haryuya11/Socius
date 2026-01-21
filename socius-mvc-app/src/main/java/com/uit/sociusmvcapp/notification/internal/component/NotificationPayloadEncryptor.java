package com.uit.sociusmvcapp.notification.internal.component;

import com.uit.sociusmvcapp.notification.dto.PayloadDto;
import com.uit.sociusmvcapp.shared.service.EncryptionService;
import com.uit.sociusmvcapp.shared.utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Component for encrypting and decrypting notification payloads. Handles
 * serialization/deserialization combined with AES-256-GCM encryption.
 */
@Component
@RequiredArgsConstructor
public class NotificationPayloadEncryptor {

  private final EncryptionService encryptionService;

  /**
   * Converts a PayloadDto to an encrypted JSON string for database storage.
   *
   * @param payloadDto the payload to encrypt
   * @return the encrypted payload as a string, or null if input is null
   */
  public String encryptPayload(PayloadDto payloadDto) {
    if (payloadDto == null) {
      return null;
    }
    String json = CommonUtils.serializeToJson(payloadDto);
    return encryptionService.encrypt(json);
  }

  /**
   * Decrypts an encrypted payload string and converts it to a PayloadDto.
   *
   * @param encryptedPayload the encrypted payload string from database
   * @return the decrypted PayloadDto, or null if input is null
   */
  public PayloadDto decryptPayload(String encryptedPayload) {
    if (encryptedPayload == null) {
      return null;
    }
    String json = encryptionService.decrypt(encryptedPayload);
    return CommonUtils.deserializeFromJson(json, PayloadDto.class);
  }
}
