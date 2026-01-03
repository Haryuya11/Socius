package com.uit.sociusmvcapp.azure.graph.internal.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/** Properties required to configure the Graph client. */
@Getter
@Builder
@EqualsAndHashCode
public class GraphClientProperties {
  private String tenantId;
  private String clientId;
  private String clientSecret;
  private String scope;
  private String userAssertion; // For OBO flow
}
