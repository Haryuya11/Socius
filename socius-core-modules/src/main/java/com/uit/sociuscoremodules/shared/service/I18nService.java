package com.uit.sociuscoremodules.shared.service;

/** Service interface for internationalization (i18n) messages. */
public interface I18nService {
  /**
   * Get the localized message for the given code.
   *
   * @param code the message code
   * @return the localized message
   */
  String getMessage(String code);

  /**
   * Get the localized message for the given code with arguments.
   *
   * @param code the message code
   * @param args the arguments to format the message
   * @return the localized message
   */
  String getMessage(String code, Object[] args);
}
