package com.uit.sociusmvcapp.iam;

import com.uit.sociusmvcapp.iam.dto.RoleDto;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.response.Response;
import com.uit.sociusmvcapp.shared.service.I18nService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** RoleController handles HTTP requests related to role operations. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/roles")
public class RoleController {
  /** I18nService for internationalization messages. */
  private final I18nService i18nService;

  /** RoleService for role-related operations. */
  private final RoleService roleService;

  /**
   * Find all roles.
   *
   * @return ResponseEntity containing the list of all roles
   */
  @GetMapping
  public ResponseEntity<Response> findAll() {
    List<RoleDto> roles = roleService.findAll();
    Response response =
        Response.builder()
            .success(true)
            .status(200)
            .code(MessageConstant.S_ROLE_001)
            .message(i18nService.getMessage(MessageConstant.S_ROLE_001))
            .data(roles)
            .build();

    return ResponseEntity.ok(response);
  }

  /**
   * Find a role by its code.
   *
   * @param roleCode the role code
   * @return ResponseEntity containing the role details
   */
  @GetMapping("/{roleCode}")
  public ResponseEntity<Response> findByRoleCode(@PathVariable String roleCode) {
    RoleDto role = roleService.findByRoleCode(roleCode);
    Response response =
        Response.builder()
            .success(true)
            .status(200)
            .code(MessageConstant.S_ROLE_002)
            .message(i18nService.getMessage(MessageConstant.S_ROLE_002))
            .data(role)
            .build();

    return ResponseEntity.ok(response);
  }

  /**
   * Find roles by type.
   *
   * @param roleType the role type
   * @return ResponseEntity containing the list of roles matching the type
   */
  @GetMapping("/type/{roleType}")
  public ResponseEntity<Response> findByRoleType(@PathVariable String roleType) {
    List<RoleDto> roles = roleService.findByRoleType(roleType);
    Response response =
        Response.builder()
            .success(true)
            .status(200)
            .code(MessageConstant.S_ROLE_002)
            .message(i18nService.getMessage(MessageConstant.S_ROLE_002))
            .data(roles)
            .build();
    return ResponseEntity.ok(response);
  }
}
