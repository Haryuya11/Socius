package com.uit.sociusmvcapp.employee.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request object for changing user password. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
  private String currentPassword;
  private String newPassword;
  private String confirmPassword;
}
