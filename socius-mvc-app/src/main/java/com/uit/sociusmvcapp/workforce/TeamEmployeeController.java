package com.uit.sociusmvcapp.workforce;

import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.response.Response;
import com.uit.sociusmvcapp.shared.service.I18nService;
import com.uit.sociusmvcapp.workforce.dto.TeamEmployeeBatchResultDto;
import com.uit.sociusmvcapp.workforce.dto.TeamEmployeeDto;
import com.uit.sociusmvcapp.workforce.dto.request.TeamEmployeeBatchAddRequest;
import com.uit.sociusmvcapp.workforce.dto.request.TransferTeamEmployeeRequest;
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
   * Transfer an employee between teams.
   *
   * <p>POST /teams/transfer
   *
   * @param request the request containing transfer details
   * @return ResponseEntity indicating the result of the transfer operation
   */
  @PostMapping("/transfer")
  public ResponseEntity<Response> transferEmployee(
      @RequestBody TransferTeamEmployeeRequest request) {

    teamEmployeeService.transferEmployee(request);

    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_TEAM_004)
            .message(i18nService.getMessage(MessageConstant.S_TEAM_004))
            .build();
    return ResponseEntity.ok(response);
  }
}
