package com.uit.sociusmvcapp.shared.service;

/**
 * Service interface for sending email notifications. Provides methods for sending various types of
 * email communications.
 */
public interface EmailService {

  /**
   * Sends a welcome email to a new user with their generated password.
   *
   * @param to the recipient email address
   * @param fullName the user's full name
   * @param password the generated password for the new account
   */
  void sendWelcomeEmail(String to, String fullName, String password);
}
