package com.uit.sociusmvcapp.shared.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Implementation of EmailService using JavaMailSender. Provides asynchronous email sending
 * capabilities for various notification types.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

  private final JavaMailSender mailSender;

  @Value("${mail.from}")
  private String fromEmail;

  /**
   * Sends a welcome email to a new user with their generated password. This method runs
   * asynchronously to avoid blocking the main request thread.
   *
   * @param to the recipient email address
   * @param fullName the user's full name
   * @param password the generated password for the new account
   */
  @Override
  @Async
  public void sendWelcomeEmail(String to, String fullName, String password) {
    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom(fromEmail);
      message.setTo(to);
      message.setSubject("Welcome to Socius - Your Account Details");
      message.setText(buildWelcomeEmailContent(fullName, to, password));

      mailSender.send(message);
      log.info("Welcome email sent successfully to: {}", to);
    } catch (Exception e) {
      log.error("Failed to send welcome email to {}: {}", to, e.getMessage(), e);
    }
  }

  /**
   * Builds the welcome email content with account details.
   *
   * @param fullName the user's full name
   * @param email the user's email address
   * @param password the generated password
   * @return the formatted email content
   */
  private String buildWelcomeEmailContent(String fullName, String email, String password) {
    return String.format(
        """
        Dear %s,

        Welcome to Socius! Your account has been created successfully.

        Here are your login credentials:
        Email: %s
        Temporary Password: %s

        For security reasons, you will be required to change your password upon first login.

        Please keep this information secure and do not share it with anyone.

        Best regards,
        Socius Team
        """,
        fullName, email, password);
  }
}
