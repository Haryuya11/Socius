package com.uit.sociusmvcapp.employee;

import com.uit.sociusmvcapp.iam.dto.UserDepartmentInfo;
import com.uit.sociusmvcapp.iam.dto.UserTeamInfo;
import java.util.List;
import java.util.Map;

/** Gateway interface for workforce-related operations. */
public interface WorkforceGateway {

  /**
   * Get list of Teams by Employee ID.
   *
   * @param employeeId the employee ID
   * @return list of UserTeamInfo
   */
  List<UserTeamInfo> getTeamsByEmployeeId(String employeeId);

  /**
   * Get list of Departments by Employee ID.
   *
   * @param employeeId the employee ID
   * @return list of UserDepartmentInfo
   */
  List<UserDepartmentInfo> getDepartmentsByEmployeeId(String employeeId);

  /**
   * Get map of Teams by Employee IDs.
   *
   * @param employeeIds the list of employee IDs
   * @return map of employee ID to list of UserTeamInfo
   */
  Map<String, List<UserTeamInfo>> getTeamsByEmployeeIds(List<String> employeeIds);

  /**
   * Get map of Departments by Employee IDs.
   *
   * @param employeeIds the list of employee IDs
   * @return map of employee ID to list of UserDepartmentInfo
   */
  Map<String, List<UserDepartmentInfo>> getDepartmentsByEmployeeIds(List<String> employeeIds);
}
