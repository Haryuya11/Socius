package com.uit.sociusmvcapp.shared.service;

/**
 * Service interface for encryption and decryption operations. Provides AES-256-GCM encryption for
 * sensitive data before database storage.
 */
public interface EncryptionService {

  /**
   * Encrypts plaintext using AES-256-GCM.
   *
   * @param plaintext the text to encrypt
   * @return the encrypted text as a Base64-encoded string, or null if input is null
   */
  String encrypt(String plaintext);

  /**
   * Decrypts ciphertext using AES-256-GCM.
   *
   * @param ciphertext the Base64-encoded encrypted text
   * @return the decrypted plaintext, or null if input is null
   */
  String decrypt(String ciphertext);
}
