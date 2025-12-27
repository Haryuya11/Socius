package com.uit.sociusmvcapp.workforce.internal.component;

import com.uit.sociusmvcapp.department.DepartmentActionGuard;
import com.uit.sociusmvcapp.department.enums.DepartmentActionType;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.service.ExceptionFactory;
import com.uit.sociusmvcapp.workforce.internal.repository.DepartmentEmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** Guard component to validate department actions in the Workforce module. */
@Component
@Slf4j
@RequiredArgsConstructor
public class WorkforceDepartmentGuard implements DepartmentActionGuard {

  private final DepartmentEmployeeRepository departmentEmployeeRepository;

  /**
   * Validates the specified action for the given department code.
   *
   * @param action the action to be validated
   * @param departmentCode the code of the department
   */
  @Override
  public void validate(DepartmentActionType action, String departmentCode) {
    switch (action) {
      case DEACTIVATE -> {
        log.info("Validating DEACTIVATE action for department: {}", departmentCode);
        if (departmentEmployeeRepository.hasEmployees(departmentCode)) {
          log.error(
              "Cannot deactivate department {} because it has assigned employees.", departmentCode);
          throw ExceptionFactory.badRequest(MessageConstant.E_DEP_007);
        }
      }
      case ACTIVATE -> log.info("Validating ACTIVATE action for department: {}", departmentCode);

      case UPDATE_INFO ->
          log.info("Validating UPDATE_INFO action for department: {}", departmentCode);

      default -> log.warn("No validation implemented for action: {}", action);
    }
  }

  /**
   * Gets the module name associated with this guard.
   *
   * @return the module name
   */
  @Override
  public String getModuleName() {
    return "Workforce Module";
  }
}
