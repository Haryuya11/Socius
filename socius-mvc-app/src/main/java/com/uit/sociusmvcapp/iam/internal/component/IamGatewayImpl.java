package com.uit.sociusmvcapp.iam.internal.component;

import com.uit.sociusmvcapp.iam.IamGateway;
import com.uit.sociusmvcapp.iam.internal.dto.RolePermissionDto;
import com.uit.sociusmvcapp.iam.internal.repository.RolePermissionRepository;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Implementation of IamGateway interface. */
@Component
@RequiredArgsConstructor
class IamGatewayImpl implements IamGateway {

  /** Repository for Role entity. */
  private final RolePermissionRepository repository;

  /**
   * Get a map of permissions associated with the given role codes.
   *
   * @param roleCodes list of role codes
   * @return map where key is role code and value is list of permissions
   */
  @Override
  public Map<String, List<String>> getPermissionsMapByRoles(List<String> roleCodes) {
    if (roleCodes == null || roleCodes.isEmpty()) {
      return Collections.emptyMap();
    }

    List<RolePermissionDto> mappings = repository.findPermissionsByRoleCodes(roleCodes);

    return mappings.stream()
        .collect(
            Collectors.groupingBy(
                RolePermissionDto::getRoleCode,
                Collectors.mapping(RolePermissionDto::getPermissionCode, Collectors.toList())));
  }
}
