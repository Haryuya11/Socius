package com.uit.sociusmvcapp.workforce.internal.component;

import com.uit.sociusmvcapp.employee.WorkforceGateway;
import com.uit.sociusmvcapp.iam.dto.UserDepartmentInfo;
import com.uit.sociusmvcapp.iam.dto.UserTeamInfo;
import com.uit.sociusmvcapp.workforce.dto.DepartmentEmployeeDto;
import com.uit.sociusmvcapp.workforce.dto.TeamEmployeeDto;
import com.uit.sociusmvcapp.workforce.internal.converter.DepartmentEmployeeConverter;
import com.uit.sociusmvcapp.workforce.internal.converter.TeamEmployeeConverter;
import com.uit.sociusmvcapp.workforce.internal.repository.DepartmentEmployeeRepository;
import com.uit.sociusmvcapp.workforce.internal.repository.TeamEmployeeRepository;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Implementation of WorkforceGateway interface. */
@Component
@RequiredArgsConstructor
public class WorkforceGatewayImpl implements WorkforceGateway {

  private final TeamEmployeeRepository teamEmployeeRepo;
  private final TeamEmployeeConverter teamEmployeeConverter;

  private final DepartmentEmployeeRepository deptEmployeeRepo;
  private final DepartmentEmployeeConverter departmentEmployeeConverter;

  /** {@inheritDoc} */
  @Override
  public List<UserTeamInfo> getTeamsByEmployeeId(String employeeId) {
    List<TeamEmployeeDto> teamEmployees = teamEmployeeRepo.findByEmployeeId(employeeId);
    return teamEmployeeConverter.toUserTeamInfos(teamEmployees);
  }

  /** {@inheritDoc} */
  @Override
  public List<UserDepartmentInfo> getDepartmentsByEmployeeId(String employeeId) {
    List<DepartmentEmployeeDto> departmentEmployees = deptEmployeeRepo.findByEmployeeId(employeeId);
    return departmentEmployeeConverter.toUserDepartmentInfos(departmentEmployees);
  }

  /** {@inheritDoc} */
  @Override
  public Map<String, List<UserTeamInfo>> getTeamsByEmployeeIds(List<String> employeeIds) {
    if (employeeIds == null || employeeIds.isEmpty()) {
      return Collections.emptyMap();
    }

    List<TeamEmployeeDto> entities = teamEmployeeRepo.findByEmployeeIdIn(employeeIds);

    return entities.stream()
        .collect(
            Collectors.groupingBy(
                dto -> dto.getEmployee().getClientId(),
                Collectors.mapping(teamEmployeeConverter::toUserTeamInfo, Collectors.toList())));
  }

  /** {@inheritDoc} */
  @Override
  public Map<String, List<UserDepartmentInfo>> getDepartmentsByEmployeeIds(
      List<String> employeeIds) {
    if (employeeIds == null || employeeIds.isEmpty()) {
      return Collections.emptyMap();
    }

    List<DepartmentEmployeeDto> entities = deptEmployeeRepo.findByEmployeeIdIn(employeeIds);

    return entities.stream()
        .collect(
            Collectors.groupingBy(
                // SỬA Ở ĐÂY: Thay DepartmentEmployeeDto::getClientId bằng lambda
                dto -> dto.getEmployee().getClientId(),
                Collectors.mapping(
                    departmentEmployeeConverter::toUserDepartmentInfo, Collectors.toList())));
  }
}
