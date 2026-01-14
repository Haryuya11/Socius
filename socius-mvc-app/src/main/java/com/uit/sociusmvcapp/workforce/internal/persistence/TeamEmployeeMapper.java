package com.uit.sociusmvcapp.workforce.internal.persistence;

import com.uit.sociusmvcapp.workforce.internal.domain.TeamEmployee;
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
   * Find all team-employee relationships in a team.
   *
   * @param teamCode the team code
   * @return list of TeamEmployeeDto
   */
  List<TeamEmployee> findEmployeesByTeamCode(String teamCode);

  /**
   * Find team lead team-employee relationship.
   *
   * @param teamCode the team code
   * @return the TeamEmployee entity
   */
  TeamEmployee findTeamLeadByTeamCode(String teamCode);

  /**
   * Insert a new team-employee relationship.
   *
   * @param teamEmployee the TeamEmployee entity to insert
   */
  void insert(TeamEmployee teamEmployee);

  /**
   * Batch insert team-employee relationships.
   *
   * @param teamEmployees the list of TeamEmployee entities to insert
   */
  void batchInsert(@Param("teamEmployees") List<TeamEmployee> teamEmployees);

  /**
   * Batch reactivate soft-deleted team-employee relationships.
   *
   * @param teamEmployees the list of TeamEmployee entities to reactivate
   */
  void batchReactivate(@Param("teamEmployees") List<TeamEmployee> teamEmployees);

  /**
   * Find soft-deleted team-employee relationships by team code and employee IDs.
   *
   * @param teamCode the team code
   * @param employeeIds the list of employee IDs
   * @return list of soft-deleted TeamEmployee entities
   */
  List<TeamEmployee> findSoftDeletedByTeamCodeAndEmployeeIds(
      @Param("teamCode") String teamCode, @Param("employeeIds") List<String> employeeIds);

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
   * Update role code for an employee in a team.
   *
   * @param teamCode the team code
   * @param employeeId the employee ID
   * @param roleCode the new role code
   */
  void updateRoleCode(
      @Param("teamCode") String teamCode,
      @Param("employeeId") String employeeId,
      @Param("roleCode") String roleCode);

  /**
   * Soft delete team-employee relationship.
   *
   * @param teamCode the team code
   * @param employeeId the employee ID (user_id)
   */
  void softDelete(@Param("teamCode") String teamCode, @Param("employeeId") String employeeId);

  /**
   * Batch soft delete team-employee relationships.
   *
   * @param teamCode the team code
   * @param employeeIds the list of employee IDs to remove
   */
  void batchSoftDelete(
      @Param("teamCode") String teamCode, @Param("employeeIds") List<String> employeeIds);

  /**
   * Reactivate soft-deleted team-employee relationship.
   *
   * @param teamEmployee the TeamEmployee entity to reactivate
   */
  void reactivate(TeamEmployee teamEmployee);

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
  Boolean existsTeamLeadByTeamCode(String teamCode);

  /**
   * Count team leaders in a team.
   *
   * @param teamCode the team code
   * @return count of team leaders
   */
  Integer countTeamLeadersByTeamCode(String teamCode);

  /**
   * Check if a team has any employees.
   *
   * @param teamCode the team code
   * @return true if the team has employees, false otherwise
   */
  boolean hasEmployees(String teamCode);

  /**
   * Delete all team-employee relationships for a given employee ID.
   *
   * @param employeeId the employee ID
   */
  void deleteByEmployeeId(String employeeId);

  /**
   * Find all team-employee relationships for a given employee ID.
   *
   * @param employeeId the employee ID
   * @return list of TeamEmployee entities
   */
  List<TeamEmployee> findByEmployeeId(String employeeId);

  /**
   * Find all team-employee relationships for a list of employee IDs.
   *
   * @param employeeIds the list of employee IDs
   * @return list of TeamEmployee entities
   */
  List<TeamEmployee> findByEmployeeIdIn(List<String> employeeIds);
}
