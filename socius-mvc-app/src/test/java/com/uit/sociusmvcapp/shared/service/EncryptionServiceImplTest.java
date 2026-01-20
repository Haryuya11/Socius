package com.uit.sociusmvcapp.shared.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Base64;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Unit tests for EncryptionServiceImpl using AES-256-GCM. */
class EncryptionServiceImplTest {

  private EncryptionService encryptionService;
  private String testEncryptionKey;

  @BeforeEach
  void setUp() throws Exception {
    // Generate a valid 256-bit AES key for testing
    KeyGenerator keyGen = KeyGenerator.getInstance("AES");
    keyGen.init(256);
    SecretKey secretKey = keyGen.generateKey();
    testEncryptionKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());

    encryptionService = new EncryptionServiceImpl(testEncryptionKey);
  }

  @Test
  @DisplayName("Should encrypt and decrypt text correctly")
  void testEncryptDecryptRoundTrip() {
    String plaintext = "Hello, this is a secret message!";

    String encrypted = encryptionService.encrypt(plaintext);
    String decrypted = encryptionService.decrypt(encrypted);

    assertEquals(plaintext, decrypted);
  }

  @Test
  @DisplayName("Should produce different ciphertext for same plaintext (due to random IV)")
  void testEncryptionProducesDifferentCiphertexts() {
    String plaintext = "Same message encrypted twice";

    String encrypted1 = encryptionService.encrypt(plaintext);
    String encrypted2 = encryptionService.encrypt(plaintext);

    assertNotEquals(encrypted1, encrypted2, "Ciphertexts should differ due to random IV");

    // Both should decrypt to the same plaintext
    assertEquals(plaintext, encryptionService.decrypt(encrypted1));
    assertEquals(plaintext, encryptionService.decrypt(encrypted2));
  }

  @Test
  @DisplayName("Should handle null input for encryption")
  void testEncryptNull() {
    String result = encryptionService.encrypt(null);
    assertNull(result);
  }

  @Test
  @DisplayName("Should handle null input for decryption")
  void testDecryptNull() {
    String result = encryptionService.decrypt(null);
    assertNull(result);
  }

  @Test
  @DisplayName("Should handle empty string")
  void testEncryptDecryptEmptyString() {
    String plaintext = "";

    String encrypted = encryptionService.encrypt(plaintext);
    assertNotNull(encrypted);

    String decrypted = encryptionService.decrypt(encrypted);
    assertEquals(plaintext, decrypted);
  }

  @Test
  @DisplayName("Should handle Unicode characters")
  void testEncryptDecryptUnicode() {
    String plaintext = "Xin chào! 你好! مرحبا! 🎉";

    String encrypted = encryptionService.encrypt(plaintext);
    String decrypted = encryptionService.decrypt(encrypted);

    assertEquals(plaintext, decrypted);
  }

  @Test
  @DisplayName("Should handle long text")
  void testEncryptDecryptLongText() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 1000; i++) {
      sb.append("This is a long message that will be encrypted. ");
    }
    String plaintext = sb.toString();

    String encrypted = encryptionService.encrypt(plaintext);
    String decrypted = encryptionService.decrypt(encrypted);

    assertEquals(plaintext, decrypted);
  }

  @Test
  @DisplayName("Should handle JSON payload")
  void testEncryptDecryptJson() {
    String jsonPayload =
        "{\"title\":\"Test\",\"content\":\"New message\",\"linkUrl\":\"/msg/123\"}";

    String encrypted = encryptionService.encrypt(jsonPayload);
    String decrypted = encryptionService.decrypt(encrypted);

    assertEquals(jsonPayload, decrypted);
  }

  @Test
  @DisplayName("Should throw exception for invalid key length")
  void testInvalidKeyLength() {
    // 128-bit key (16 bytes) instead of 256-bit (32 bytes)
    String invalidKey = Base64.getEncoder().encodeToString(new byte[16]);

    assertThrows(
        IllegalArgumentException.class,
        () -> new EncryptionServiceImpl(invalidKey),
        "Should throw exception for non-256-bit key");
  }

  @Test
  @DisplayName("Should throw exception for tampered ciphertext")
  void testTamperedCiphertext() {
    String plaintext = "Sensitive data";
    String encrypted = encryptionService.encrypt(plaintext);

    // Decode, tamper, and re-encode
    byte[] encryptedBytes = Base64.getDecoder().decode(encrypted);
    encryptedBytes[encryptedBytes.length - 1] ^= 0xFF; // Flip last byte
    String tampered = Base64.getEncoder().encodeToString(encryptedBytes);

    // Should throw EncryptionException due to authentication tag mismatch
    assertThrows(
        EncryptionServiceImpl.EncryptionException.class,
        () -> encryptionService.decrypt(tampered),
        "Should fail for tampered ciphertext");
  }
}
