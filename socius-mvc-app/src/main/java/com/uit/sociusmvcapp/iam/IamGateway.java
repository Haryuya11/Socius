package com.uit.sociusmvcapp.iam;

import java.util.List;
import java.util.Map;

/** Gateway interface for IAM-related operations. */
public interface IamGateway {

  /**
   * Get a map of permissions associated with the given role codes.
   *
   * @param roleCodes list of role codes
   * @return map where key is role code and value is list of permissions
   */
  Map<String, List<String>> getPermissionsMapByRoles(List<String> roleCodes);
}
