package com.uit.sociusmvcapp.azure.graph.internal.factory;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.OnBehalfOfCredential;
import com.azure.identity.OnBehalfOfCredentialBuilder;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.uit.sociusmvcapp.azure.graph.internal.domain.GraphClientProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** Factory class for creating Azure Graph clients with different authentication methods. */
@Component
@Slf4j
public class GraphClientFactory {

  /**
   * Create a GraphServiceClient using client credentials flow.
   *
   * @param properties the properties for building the client
   * @return GraphServiceClient instance
   */
  public GraphServiceClient createClientCredentialClient(GraphClientProperties properties) {
    log.debug("Creating new client credential GraphServiceClient");

    ClientSecretCredential credential =
        new ClientSecretCredentialBuilder()
            .clientId(properties.getClientId())
            .clientSecret(properties.getClientSecret())
            .tenantId(properties.getTenantId())
            .build();

    return new GraphServiceClient(credential, properties.getScope());
  }

  /**
   * Create a GraphServiceClient using On-Behalf-Of flow.
   *
   * @param properties the properties for building the client (must include userAssertion)
   * @return GraphServiceClient instance
   */
  public GraphServiceClient createOnBehalfOfClient(GraphClientProperties properties) {
    if (properties.getUserAssertion() == null) {
      throw new IllegalArgumentException("User assertion token is required for OBO flow");
    }

    log.debug("Creating new OBO GraphServiceClient");
    String authorityUrl =
        String.format("https://login.microsoftonline.com/%s", properties.getTenantId());

    OnBehalfOfCredential credential =
        new OnBehalfOfCredentialBuilder()
            .authorityHost(authorityUrl)
            .tenantId(properties.getTenantId())
            .clientId(properties.getClientId())
            .clientSecret(properties.getClientSecret())
            .userAssertion(properties.getUserAssertion())
            .build();

    return new GraphServiceClient(credential, properties.getScope());
  }

  /**
   * Create ClientSecretCredential for direct use.
   *
   * @param properties the properties for building the credential
   * @return ClientSecretCredential instance
   */
  public ClientSecretCredential createClientSecretCredential(GraphClientProperties properties) {
    log.debug("Creating new ClientSecretCredential");

    return new ClientSecretCredentialBuilder()
        .clientId(properties.getClientId())
        .clientSecret(properties.getClientSecret())
        .tenantId(properties.getTenantId())
        .build();
  }
}
