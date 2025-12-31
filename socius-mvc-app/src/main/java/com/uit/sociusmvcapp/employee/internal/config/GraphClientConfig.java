package com.uit.sociusmvcapp.employee.internal.config;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configuration class for Azure Graph Client credentials. */
@Configuration
public class GraphClientConfig {

  /** Tenant ID for Azure Active Directory. */
  @Value("${spring.cloud.azure.active-directory.profile.tenant-id}")
  private String tenantId;

  /** Client ID for Azure Active Directory. */
  @Value("${spring.cloud.azure.active-directory.credential.client-id}")
  private String clientId;

  /** Client Secret for Azure Active Directory. */
  @Value("${spring.cloud.azure.active-directory.credential.client-secret}")
  private String clientSecret;

  /**
   * Bean definition for ClientSecretCredential used to authenticate with Azure services.
   *
   * @return ClientSecretCredential instance
   */
  @Bean
  public ClientSecretCredential clientSecretCredential() {
    return new ClientSecretCredentialBuilder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .tenantId(tenantId)
        .build();
  }

  /**
   * Bean definition for GraphServiceClient used to interact with Microsoft Graph API.
   *
   * @param credential the ClientSecretCredential for authentication
   * @return GraphServiceClient instance
   */
  @Bean
  public GraphServiceClient graphServiceClient(ClientSecretCredential credential) {
    return new GraphServiceClient(credential);
  }
}
