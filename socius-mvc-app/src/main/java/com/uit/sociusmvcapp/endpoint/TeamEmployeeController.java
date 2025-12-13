package com.uit.sociusmvcapp.endpoint;

import com.uit.sociuscoremodules.shared.constants.MessageConstant;
import com.uit.sociuscoremodules.shared.request.PaginationSearchRequest;
import com.uit.sociuscoremodules.shared.response.PageResponse;
import com.uit.sociuscoremodules.shared.response.Response;
import com.uit.sociuscoremodules.shared.service.I18nService;
import com.uit.sociuscoremodules.teamemployee.dto.SearchTeamEmployeeDto;
import com.uit.sociuscoremodules.teamemployee.dto.TeamEmployeeBatchResultDto;
import com.uit.sociuscoremodules.teamemployee.dto.TeamEmployeeDto;
import com.uit.sociuscoremodules.teamemployee.request.SearchTeamEmployeeRequest;
import com.uit.sociuscoremodules.teamemployee.request.TeamEmployeeBatchAddRequest;
import com.uit.sociuscoremodules.teamemployee.service.TeamEmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** TeamEmployeeController handles HTTP requests related to team-employee operations. */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/teams")
public class TeamEmployeeController {

  private final I18nService i18nService;
  private final TeamEmployeeService teamEmployeeService;

  /**
   * Add employee(s) to a team.
   *
   * <p>POST /teams/{teamCode}/employees
   *
   * <p>Always returns a batch result structure containing successful and failed lists.
   *
   * @param teamCode the team code
   * @param request the request containing employee(s) to add
   * @return ResponseEntity containing the batch result
   */
  @PostMapping("/{teamCode}/employees")
  public ResponseEntity<Response> addEmployeesToTeam(
      @PathVariable String teamCode, @RequestBody TeamEmployeeBatchAddRequest request) {

    TeamEmployeeBatchResultDto result = teamEmployeeService.addEmployeesToTeam(teamCode, request);

    // the flag to check is 100% successful
    boolean success = result.getFailed().isEmpty();

    // For batch or if there are failures, return batch result
    Response response =
        Response.builder()
            .success(success)
            .status(success ? HttpStatus.CREATED.value() : HttpStatus.OK.value())
            .code(success ? MessageConstant.S_TEAM_EMP_001 : MessageConstant.S_TEAM_EMP_005)
            .message(
                success
                    ? i18nService.getMessage(MessageConstant.S_TEAM_EMP_001)
                    : String.format(
                        i18nService.getMessage(MessageConstant.S_TEAM_EMP_005),
                        result.getSuccessful().size(),
                        result.getFailed().size()))
            .data(result)
            .build();

    return ResponseEntity.status(response.getStatus()).body(response);
  }

  /**
   * Remove an employee from a team.
   *
   * <p>DELETE /teams/{teamCode}/employees/{employeeId}
   *
   * @param teamCode the team code
   * @param employeeId the employee ID (client_id) to remove
   * @return ResponseEntity with success message
   */
  @DeleteMapping("/{teamCode}/employees/{employeeId}")
  public ResponseEntity<Response> removeEmployeeFromTeam(
      @PathVariable String teamCode, @PathVariable String employeeId) {
    teamEmployeeService.removeEmployeeFromTeam(teamCode, employeeId);

    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_TEAM_EMP_002)
            .message(i18nService.getMessage(MessageConstant.S_TEAM_EMP_002))
            .build();

    return ResponseEntity.ok(response);
  }

  /**
   * Change team lead.
   *
   * <p>PUT /teams/{teamCode}/lead
   *
   * @param teamCode the team code
   * @param newLeadClientId the client ID of the new team lead
   * @return ResponseEntity containing the updated team lead information
   */
  @PutMapping("/{teamCode}/lead")
  public ResponseEntity<Response> changeTeamLead(
      @PathVariable String teamCode, @RequestParam String newLeadClientId) {
    TeamEmployeeDto newTeamLead = teamEmployeeService.changeTeamLead(teamCode, newLeadClientId);

    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_TEAM_EMP_003)
            .message(i18nService.getMessage(MessageConstant.S_TEAM_EMP_003))
            .data(newTeamLead)
            .build();

    return ResponseEntity.ok(response);
  }

  /**
   * Search employees in a team.
   *
   * <p>POST /api/teams/{teamCode}/employees/search
   *
   * @param teamCode the team code
   * @param request the search request containing condition, pagination, and sorting
   * @return ResponseEntity containing the list of employees and pagination info
   */
  @PostMapping("/{teamCode}/employees/search")
  public ResponseEntity<Response> searchEmployeesInTeam(
      @PathVariable String teamCode,
      @RequestBody PaginationSearchRequest<SearchTeamEmployeeRequest> request) {

    PageResponse<SearchTeamEmployeeDto> result =
        teamEmployeeService.searchEmployeesInTeam(teamCode, request);

    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_TEAM_EMP_004)
            .message(i18nService.getMessage(MessageConstant.S_TEAM_EMP_004))
            .data(result)
            .build();

    return ResponseEntity.ok(response);
  }
}
