package com.uit.sociusmvcapp.department.internal.component;

import com.uit.sociusmvcapp.department.DepartmentService;
import com.uit.sociusmvcapp.team.DepartmentGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Implementation of DepartmentGateway interface.
 *
 * <p>This component provides department validation services to Team module without creating direct
 * dependency on Department module.
 */
@Component
@RequiredArgsConstructor
public class DepartmentGatewayImpl implements DepartmentGateway {

  private final DepartmentService departmentService;

  /**
   * Validate if a department exists by its code.
   *
   * @param departmentCode the department code to validate
   * @throws NotFoundException if department not found
   */
  @Override
  public void validateDepartmentExists(String departmentCode) {
    departmentService.validateExists(departmentCode);
  }
}
