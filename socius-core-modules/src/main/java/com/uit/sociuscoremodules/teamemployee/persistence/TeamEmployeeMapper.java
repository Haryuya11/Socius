package com.uit.sociuscoremodules.teamemployee.persistence;

import com.uit.sociuscoremodules.employee.domain.Employee;
import com.uit.sociuscoremodules.teamemployee.domain.TeamEmployee;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** MyBatis Mapper interface for TeamEmployee entity. */
@Mapper
public interface TeamEmployeeMapper {

  /**
   * Find team-employee relationship by team code and employee ID.
   *
   * @param teamCode the team code
   * @param employeeId the employee ID (user_id)
   * @return the TeamEmployee entity
   */
  TeamEmployee findByTeamCodeAndEmployeeId(
      @Param("teamCode") String teamCode, @Param("employeeId") String employeeId);

  /**
   * Find soft-deleted team-employee relationship by team code and employee ID.
   *
   * @param teamCode the team code
   * @param employeeId the employee ID (user_id)
   * @return the soft-deleted TeamEmployee entity
   */
  TeamEmployee findSoftDeletedByTeamCodeAndEmployeeId(
      @Param("teamCode") String teamCode, @Param("employeeId") String employeeId);

  /**
   * Find all employees in a team.
   *
   * @param teamCode the team code
   * @return list of Employee entities
   */
  List<Employee> findEmployeesByTeamCode(@Param("teamCode") String teamCode);

  /**
   * Find team lead of a team.
   *
   * @param teamCode the team code
   * @return the Employee entity
   */
  Employee findTeamLeadByTeamCode(@Param("teamCode") String teamCode);

  /**
   * Insert a new team-employee relationship.
   *
   * @param teamEmployee the TeamEmployee entity to insert
   */
  void insert(@Param("teamEmployee") TeamEmployee teamEmployee);

  /**
   * Update leadership status for an employee in a team.
   *
   * @param teamCode the team code
   * @param employeeId the employee ID (user_id)
   * @param isLeader the leadership status
   */
  void updateLeadershipStatus(
      @Param("teamCode") String teamCode,
      @Param("employeeId") String employeeId,
      @Param("isLeader") Boolean isLeader);

  /**
   * Soft delete team-employee relationship.
   *
   * @param teamCode the team code
   * @param employeeId the employee ID (user_id)
   */
  void softDelete(@Param("teamCode") String teamCode, @Param("employeeId") String employeeId);

  /**
   * Reactivate soft-deleted team-employee relationship.
   *
   * @param teamCode the team code
   * @param employeeId the employee ID
   * @param roleCode the role code
   * @param isLeader the leadership status
   */
  void reactivate(
      @Param("teamCode") String teamCode,
      @Param("employeeId") String employeeId,
      @Param("roleCode") String roleCode,
      @Param("isLeader") Boolean isLeader);

  /**
   * Check if employee is already in team.
   *
   * @param teamCode the team code
   * @param employeeId the employee ID (user_id)
   * @return true if exists, false otherwise
   */
  Boolean existsByTeamCodeAndEmployeeId(
      @Param("teamCode") String teamCode, @Param("employeeId") String employeeId);

  /**
   * Check if team has a team lead.
   *
   * @param teamCode the team code
   * @return true if exists, false otherwise
   */
  Boolean existsTeamLeadByTeamCode(@Param("teamCode") String teamCode);

  /**
   * Count team leaders in a team.
   *
   * @param teamCode the team code
   * @return count of team leaders
   */
  Integer countTeamLeadersByTeamCode(@Param("teamCode") String teamCode);

  /**
   * Search employees in a team with filters, sorting, and pagination.
   *
   * @param request the search request with filters
   * @return list of TeamEmployeeDto
   */
  List<com.uit.sociuscoremodules.teamemployee.dto.TeamEmployeeDto> searchEmployeesInTeam(
      @Param("request")
          com.uit.sociuscoremodules.teamemployee.request.TeamEmployeeSearchRequest request);

  /**
   * Count employees in a team with filters.
   *
   * @param request the search request with filters
   * @return count of employees
   */
  Integer countEmployeesInTeam(
      @Param("request")
          com.uit.sociuscoremodules.teamemployee.request.TeamEmployeeSearchRequest request);
}
