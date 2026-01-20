package com.uit.sociusmvcapp.shared.service;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Implementation of EncryptionService using AES-256-GCM. AES-256-GCM provides authenticated
 * encryption ensuring both confidentiality and integrity.
 */
@Service
@Slf4j
public class EncryptionServiceImpl implements EncryptionService {

  private static final String ALGORITHM = "AES/GCM/NoPadding";
  private static final int GCM_IV_LENGTH = 12;
  private static final int GCM_TAG_LENGTH = 128;

  private final SecretKey secretKey;

  /**
   * Constructor that initializes the encryption service with a secret key.
   *
   * @param encryptionKey Base64-encoded 256-bit AES key from environment configuration
   */
  public EncryptionServiceImpl(@Value("${app.encryption.key}") String encryptionKey) {
    byte[] keyBytes = Base64.getDecoder().decode(encryptionKey);
    if (keyBytes.length != 32) {
      throw new IllegalArgumentException("Encryption key must be 256 bits (32 bytes)");
    }
    this.secretKey = new SecretKeySpec(keyBytes, "AES");
  }

  /**
   * Encrypts plaintext using AES-256-GCM. The output format is: [IV (12 bytes)][ciphertext + auth
   * tag]
   *
   * @param plaintext the text to encrypt
   * @return the encrypted text as a Base64-encoded string, or null if input is null
   */
  @Override
  public String encrypt(String plaintext) {
    if (plaintext == null) {
      return null;
    }

    try {
      byte[] iv = new byte[GCM_IV_LENGTH];
      SecureRandom secureRandom = new SecureRandom();
      secureRandom.nextBytes(iv);

      Cipher cipher = Cipher.getInstance(ALGORITHM);
      GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
      cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

      byte[] plaintextBytes = plaintext.getBytes(StandardCharsets.UTF_8);
      byte[] ciphertext = cipher.doFinal(plaintextBytes);

      // Combine IV and ciphertext: [IV][ciphertext]
      ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + ciphertext.length);
      byteBuffer.put(iv);
      byteBuffer.put(ciphertext);

      return Base64.getEncoder().encodeToString(byteBuffer.array());
    } catch (Exception e) {
      log.error("Error encrypting data: {}", e.getMessage(), e);
      throw new RuntimeException("Encryption failed", e);
    }
  }

  /**
   * Decrypts ciphertext using AES-256-GCM. Expects input format: [IV (12 bytes)][ciphertext + auth
   * tag]
   *
   * @param ciphertext the Base64-encoded encrypted text
   * @return the decrypted plaintext, or null if input is null
   */
  @Override
  public String decrypt(String ciphertext) {
    if (ciphertext == null) {
      return null;
    }

    try {
      byte[] decodedBytes = Base64.getDecoder().decode(ciphertext);

      ByteBuffer byteBuffer = ByteBuffer.wrap(decodedBytes);
      byte[] iv = new byte[GCM_IV_LENGTH];
      byteBuffer.get(iv);
      byte[] encryptedData = new byte[byteBuffer.remaining()];
      byteBuffer.get(encryptedData);

      Cipher cipher = Cipher.getInstance(ALGORITHM);
      GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
      cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

      byte[] decryptedBytes = cipher.doFinal(encryptedData);
      return new String(decryptedBytes, StandardCharsets.UTF_8);
    } catch (Exception e) {
      log.error("Error decrypting data: {}", e.getMessage(), e);
      throw new RuntimeException("Decryption failed", e);
    }
  }
}
