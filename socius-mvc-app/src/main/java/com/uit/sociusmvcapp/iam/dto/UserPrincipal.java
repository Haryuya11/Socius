package com.uit.sociusmvcapp.iam.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.uit.sociusmvcapp.shared.constants.AuthConstant;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

/** UserPrincipal class implementing UserDetails for Spring Security authentication. */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserPrincipal implements UserDetails, Serializable {

  @Serial private static final long serialVersionUID = 1L;
  private String clientId;
  private String userId;
  private String firstName;
  private String lastName;
  private String imageUrl;

  private String systemRole;
  private Long salary;
  private List<UserDepartmentInfo> departments;
  private List<UserTeamInfo> teams;
  private List<ScopedPermissionDto> permissions;

  /**
   * Get authorities granted to the user.
   *
   * @return collection of granted authorities
   */
  @Override
  @JsonIgnore
  public Collection<? extends GrantedAuthority> getAuthorities() {
    List<SimpleGrantedAuthority> authorities = new ArrayList<>();

    // 1. Map System Role
    if (StringUtils.hasText(systemRole)) {
      authorities.add(new SimpleGrantedAuthority("ROLE_" + systemRole));
    }

    // 2. Map Permissions
    if (permissions != null) {
      for (ScopedPermissionDto perm : permissions) {
        String authString;

        // Logic tạo Authority String
        if (AuthConstant.SCOPE_GLOBAL.equals(perm.getScope())
            || AuthConstant.SCOPE_SYSTEM.equals(perm.getScope())) {
          authString = perm.getPermissionCode();
        } else {
          authString =
              String.format(
                  "%s:%s:%s", perm.getScope(), perm.getResourceCode(), perm.getPermissionCode());
        }

        authorities.add(new SimpleGrantedAuthority(authString));
      }
    }

    return authorities;
  }

  /**
   * Empty password as authentication is handled via tokens.
   *
   * @return empty string
   */
  @Override
  public String getPassword() {
    return "";
  }

  /**
   * Get username.
   *
   * @return username
   */
  @Override
  public String getUsername() {
    return this.lastName + " " + this.firstName;
  }
}
