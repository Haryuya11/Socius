package com.uit.sociusmvcapp.workforce.internal.component;

import com.uit.sociusmvcapp.department.DepartmentEmployeeGateway;
import com.uit.sociusmvcapp.workforce.dto.DepartmentEmployeeDto;
import com.uit.sociusmvcapp.workforce.internal.repository.DepartmentEmployeeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Implementation of DepartmentEmployeeGateway for Department module.
 *
 * <p>This component provides department-employee relationship services to Department module without
 * creating direct dependency on Workforce module internals.
 */
@Component
@RequiredArgsConstructor
public class DepartmentEmployeeGatewayImpl implements DepartmentEmployeeGateway {

  private final DepartmentEmployeeRepository departmentEmployeeRepository;

  /**
   * Get all active member IDs in a department.
   *
   * @param departmentCode the department code
   * @return list of active employee client IDs
   */
  @Override
  public List<String> getActiveMemberIds(String departmentCode) {
    List<DepartmentEmployeeDto> activeMembers =
        departmentEmployeeRepository.getEmployeesByDepartmentCode(departmentCode);
    return activeMembers.stream().map(de -> de.getEmployee().getClientId()).toList();
  }
}
