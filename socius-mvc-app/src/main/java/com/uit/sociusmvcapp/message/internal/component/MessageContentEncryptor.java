package com.uit.sociusmvcapp.message.internal.component;

import com.uit.sociusmvcapp.shared.service.EncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Component for encrypting and decrypting message content. Uses AES-256-GCM encryption for secure
 * message storage.
 */
@Component
@RequiredArgsConstructor
public class MessageContentEncryptor {

  private final EncryptionService encryptionService;

  /**
   * Encrypts message content for secure database storage.
   *
   * @param content the plaintext content to encrypt
   * @return the encrypted content as a Base64-encoded string, or null if input is null
   */
  public String encryptContent(String content) {
    if (content == null) {
      return null;
    }
    return encryptionService.encrypt(content);
  }

  /**
   * Decrypts message content from the database.
   *
   * @param encryptedContent the encrypted content from database
   * @return the decrypted plaintext content, or null if input is null
   */
  public String decryptContent(String encryptedContent) {
    if (encryptedContent == null) {
      return null;
    }
    return encryptionService.decrypt(encryptedContent);
  }
}
