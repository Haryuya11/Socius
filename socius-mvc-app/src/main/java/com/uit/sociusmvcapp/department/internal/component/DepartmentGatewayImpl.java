package com.uit.sociusmvcapp.department.internal.component;

import com.uit.sociusmvcapp.department.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Implementation of DepartmentGateway interfaces for both Team and Task modules.
 *
 * <p>This component provides department validation services to Team and Task modules without
 * creating direct dependency on Department module internals. Implements both gateway interfaces
 * since they share the same contract.
 */
@Component
@RequiredArgsConstructor
public class DepartmentGatewayImpl
    implements com.uit.sociusmvcapp.team.DepartmentGateway,
        com.uit.sociusmvcapp.task.DepartmentGateway {

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
