package com.uit.sociusmvcapp.azure.graph;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Azure Graph configuration properties. This is a POJO that holds Azure AD credentials and
 * settings.
 */
@Getter
@Builder
@AllArgsConstructor
public class AzureGraphProperties {

  /** Azure Active Directory tenant ID. */
  private final String tenantId;

  /** Azure Active Directory application (client) ID. */
  private final String clientId;

  /** Azure Active Directory application client secret. */
  private final String clientSecret;

  /**
   * Microsoft Graph API scope. Default: <a
   * href="https://graph.microsoft.com/.default">https://graph.microsoft.com/.default</a>
   */
  private final String scope;

  /** Azure domain name for user principal name construction. */
  private final String domainName;
}
