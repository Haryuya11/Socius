package com.uit.sociusmvcapp.employee.internal.service;

import com.uit.sociusmvcapp.employee.WorkforceGateway;
import com.uit.sociusmvcapp.employee.dto.EmployeeDto;
import com.uit.sociusmvcapp.employee.internal.converter.EmployeeConverter;
import com.uit.sociusmvcapp.employee.internal.repository.EmployeeRepository;
import com.uit.sociusmvcapp.iam.AuthorizationService;
import com.uit.sociusmvcapp.iam.IamGateway;
import com.uit.sociusmvcapp.iam.dto.ScopedPermissionDto;
import com.uit.sociusmvcapp.iam.dto.UserDepartmentInfo;
import com.uit.sociusmvcapp.iam.dto.UserPrincipal;
import com.uit.sociusmvcapp.iam.dto.UserTeamInfo;
import com.uit.sociusmvcapp.shared.constants.AuthConstant;
import com.uit.sociusmvcapp.shared.service.ExceptionFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Implementation of AuthorizationService for user authorization. */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {

  /** Repository for accessing employee data. */
  private final EmployeeRepository employeeRepository;

  /** Converter for transforming Employee entities to DTOs. */
  private final EmployeeConverter employeeConverter;

  /** Repository for accessing role and permission data. */
  private final WorkforceGateway workforceGateway;

  /** Gateway for interacting with IAM services. */
  private final IamGateway iamGateway;

  /**
   * Authorizes a user by their user ID.
   *
   * @param clientId the user ID
   * @return the UserPrincipal containing user details and permissions
   */
  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "user-principal", key = "#clientId", unless = "#result == null")
  public UserPrincipal authorize(String clientId) {
    // 1. Get user info
    EmployeeDto emp = employeeRepository.findByClientId(clientId);
    if (emp == null) {
      throw ExceptionFactory.notFound("User not found");
    }

    // 2. Get user's teams and departments
    List<UserTeamInfo> teams = workforceGateway.getTeamsByEmployeeId(clientId);
    List<UserDepartmentInfo> depts = workforceGateway.getDepartmentsByEmployeeId(clientId);

    // 3. Gather all roles assigned to the user
    Set<String> allRoles = new HashSet<>();

    // 3.1. System Role
    if (emp.getSystemRole() != null) {
      allRoles.add(emp.getSystemRole());
    }

    // 3.2. Team Roles
    if (teams != null) {
      teams.forEach(t -> allRoles.add(t.getRoleCode()));
    }

    // 3.3. Dept Roles
    if (depts != null) {
      depts.forEach(d -> allRoles.add(d.getRoleCode()));
    }

    // 4. Get permissions for all roles
    Map<String, List<String>> rolePermMap =
        iamGateway.getPermissionsMapByRoles(new ArrayList<>(allRoles));

    // 5. Assemble final permissions with scopes and resource codes
    List<ScopedPermissionDto> finalPermissions = new ArrayList<>();

    // --- A. System Role (Scope: GLOBAL) ---
    List<String> sysPerms = rolePermMap.getOrDefault(emp.getSystemRole(), Collections.emptyList());
    sysPerms.forEach(
        perm ->
            finalPermissions.add(
                ScopedPermissionDto.builder()
                    .permissionCode(perm)
                    .scope(AuthConstant.SCOPE_GLOBAL)
                    .resourceCode(AuthConstant.RESOURCE_ALL)
                    .build()));

    // --- B. Team Roles (Scope: TEAM) ---
    if (teams != null) {
      for (UserTeamInfo team : teams) {
        List<String> perms = rolePermMap.getOrDefault(team.getRoleCode(), Collections.emptyList());

        perms.forEach(
            perm ->
                finalPermissions.add(
                    ScopedPermissionDto.builder()
                        .permissionCode(perm)
                        .scope(AuthConstant.SCOPE_TEAM)
                        .resourceCode(team.getTeamCode())
                        .build()));
      }
    }

    // --- C. Xử lý Dept Roles (Scope: DEPARTMENT) ---
    if (depts != null) {
      for (UserDepartmentInfo dept : depts) {
        List<String> perms = rolePermMap.getOrDefault(dept.getRoleCode(), Collections.emptyList());

        perms.forEach(
            perm ->
                finalPermissions.add(
                    ScopedPermissionDto.builder()
                        .permissionCode(perm)
                        .scope(AuthConstant.SCOPE_DEPARTMENT)
                        .resourceCode(dept.getDepartmentCode())
                        .build()));
      }
    }

    UserPrincipal principal = employeeConverter.toUserPrincipal(emp);
    principal.setTeams(teams);
    principal.setDepartments(depts);
    principal.setPermissions(finalPermissions);

    return principal;
  }
}
