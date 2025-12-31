package com.uit.sociusmvcapp.department;

import com.uit.sociusmvcapp.department.enums.DepartmentActionType;

/** Guard interface for validating actions on departments. */
public interface DepartmentActionGuard {

  /**
   * Validates whether the specified action can be performed on the department.
   *
   * @param action the action to be validated
   * @param departmentCode the code of the department
   */
  void validate(DepartmentActionType action, String departmentCode);

  /**
   * Gets the module name associated with this guard.
   *
   * @return the module name
   */
  default String getModuleName() {
    return "Department Module";
  }
}
