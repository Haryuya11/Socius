package com.uit.sociusmvcapp.shared.utils;

import java.security.SecureRandom;

/**
 * Utility class for generating secure random passwords. Generated passwords meet Azure AD
 * requirements: at least 12 characters with lowercase, uppercase, numbers, and special characters.
 */
public final class PasswordGenerator {

  /** Minimum password length required by Azure AD. */
  private static final int MIN_PASSWORD_LENGTH = 12;

  /** Lowercase characters for password generation. */
  private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";

  /** Uppercase characters for password generation. */
  private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

  /** Numeric characters for password generation. */
  private static final String DIGITS = "0123456789";

  /** Special characters for password generation. */
  private static final String SPECIAL = "!@#$%^&*()-_+=";

  /** All characters combined for random selection. */
  private static final String ALL_CHARS = LOWERCASE + UPPERCASE + DIGITS + SPECIAL;

  /** Secure random generator for cryptographically strong passwords. */
  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  /** Private constructor to prevent instantiation. */
  private PasswordGenerator() {}

  /**
   * Generates a secure random password that meets Azure AD requirements. The password will be at
   * least 12 characters and contain at least one character from each category: lowercase,
   * uppercase, digits, and special characters.
   *
   * @return a secure random password string
   */
  public static String generateSecurePassword() {
    return generateSecurePassword(MIN_PASSWORD_LENGTH);
  }

  /**
   * Generates a secure random password with the specified length. The password will contain at
   * least one character from each category: lowercase, uppercase, digits, and special characters.
   *
   * @param length the desired password length (minimum 12)
   * @return a secure random password string
   * @throws IllegalArgumentException if length is less than 12
   */
  public static String generateSecurePassword(int length) {
    if (length < MIN_PASSWORD_LENGTH) {
      throw new IllegalArgumentException(
          "Password length must be at least " + MIN_PASSWORD_LENGTH + " characters");
    }

    StringBuilder password = new StringBuilder(length);

    // Ensure at least one character from each category
    password.append(getRandomChar(LOWERCASE));
    password.append(getRandomChar(UPPERCASE));
    password.append(getRandomChar(DIGITS));
    password.append(getRandomChar(SPECIAL));

    // Fill the remaining characters randomly from all categories
    for (int i = 4; i < length; i++) {
      password.append(getRandomChar(ALL_CHARS));
    }

    // Shuffle the password to avoid predictable patterns
    return shuffleString(password.toString());
  }

  /**
   * Gets a random character from the provided character set.
   *
   * @param chars the character set to choose from
   * @return a random character from the set
   */
  private static char getRandomChar(String chars) {
    return chars.charAt(SECURE_RANDOM.nextInt(chars.length()));
  }

  /**
   * Shuffles the characters in a string using Fisher-Yates algorithm.
   *
   * @param input the string to shuffle
   * @return the shuffled string
   */
  private static String shuffleString(String input) {
    char[] chars = input.toCharArray();
    for (int i = chars.length - 1; i > 0; i--) {
      int j = SECURE_RANDOM.nextInt(i + 1);
      char temp = chars[i];
      chars[i] = chars[j];
      chars[j] = temp;
    }
    return new String(chars);
  }
}
