package com.uit.sociuscoremodules.shared.service.impl;

import com.uit.sociuscoremodules.shared.service.I18nService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

/** Implementation of I18nService for internationalization (i18n) messages. */
@Service
@RequiredArgsConstructor
public class I18nServiceImpl implements I18nService {

  /** Message source for retrieving localized messages. */
  private final MessageSource messageSource;

  /**
   * Get the localized message for the given code.
   *
   * @param code the message code
   * @return the localized message
   */
  @Override
  public String getMessage(String code) {
    return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
  }

  /**
   * Get the localized message for the given code with arguments.
   *
   * @param code the message code
   * @param args the arguments to format the message
   * @return the localized message
   */
  @Override
  public String getMessage(String code, Object[] args) {
    return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
  }
}
