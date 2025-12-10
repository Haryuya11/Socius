package com.uit.sociusmvcapp.endpoint;

import com.uit.sociuscoremodules.shared.request.PaginationSearchRequest;
import com.uit.sociuscoremodules.shared.response.Response;
import com.uit.sociuscoremodules.shared.service.I18nService;
import com.uit.sociuscoremodules.teamemployee.constants.TeamEmployeeConstant;
import com.uit.sociuscoremodules.teamemployee.dto.TeamEmployeeBatchResult;
import com.uit.sociuscoremodules.teamemployee.dto.TeamEmployeeDto;
import com.uit.sociuscoremodules.teamemployee.request.TeamEmployeeBatchAddRequest;
import com.uit.sociuscoremodules.teamemployee.request.TeamEmployeeSearchCondition;
import com.uit.sociuscoremodules.teamemployee.service.TeamEmployeeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
   * Add employee(s) to a team. Supports both single employee and batch addition.
   *
   * <p>POST /teams/{teamCode}/employees
   *
   * <p>For single employee: { "employees": [{ "clientId": "...", "roleCode": "...", "isLeader":
   * false }] }
   *
   * <p>For multiple employees: { "employees": [{ "clientId": "..." }, { "clientId": "..." }] }
   *
   * @param teamCode the team code
   * @param request the request containing employee(s) to add
   * @return ResponseEntity containing the result
   */
  @PostMapping("/{teamCode}/employees")
  public ResponseEntity<Response> addEmployeesToTeam(
      @PathVariable @NotBlank String teamCode,
      @Valid @RequestBody TeamEmployeeBatchAddRequest request) {
    log.info("Adding {} employee(s) to team: {}", request.getEmployees().size(), teamCode);

    TeamEmployeeBatchResult result = teamEmployeeService.addEmployeesToTeam(teamCode, request);

    // If single employee and successful, return single object for backward compatibility
    if (request.isSingleEmployee() && !result.getSuccessful().isEmpty()) {
      Response response =
          Response.builder()
              .success(true)
              .status(HttpStatus.CREATED.value())
              .code(TeamEmployeeConstant.EMPLOYEE_ADDED)
              .message(i18nService.getMessage("team.employee.added"))
              .data(result.getSuccessful().get(0))
              .build();
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // For batch or if there are failures, return batch result
    Response response =
        Response.builder()
            .success(result.getFailed().isEmpty())
            .status(
                result.getFailed().isEmpty() ? HttpStatus.CREATED.value() : HttpStatus.OK.value())
            .code("team.employees.added")
            .message(
                String.format(
                    "Successfully added %d employee(s), %d failed",
                    result.getSuccessful().size(), result.getFailed().size()))
            .data(result)
            .build();

    return result.getFailed().isEmpty()
        ? ResponseEntity.status(HttpStatus.CREATED).body(response)
        : ResponseEntity.ok(response);
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
      @PathVariable @NotBlank String teamCode, @PathVariable @NotBlank String employeeId) {
    log.info("Removing employee {} from team: {}", employeeId, teamCode);
    teamEmployeeService.removeEmployeeFromTeam(teamCode, employeeId);

    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(TeamEmployeeConstant.EMPLOYEE_REMOVED)
            .message(i18nService.getMessage("team.employee.removed"))
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
      @PathVariable @NotBlank String teamCode, @RequestParam @NotBlank String newLeadClientId) {
    log.info("Changing team lead for team {} to {}", teamCode, newLeadClientId);
    TeamEmployeeDto newTeamLead = teamEmployeeService.changeTeamLead(teamCode, newLeadClientId);

    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(TeamEmployeeConstant.TEAM_LEAD_CHANGED)
            .message(i18nService.getMessage("team.lead.changed"))
            .data(newTeamLead)
            .build();

    return ResponseEntity.ok(response);
  }

  /**
   * Search employees in a team with filters, sorting, and pagination.
   *
   * <p>Supports both query parameters and request body. Request body takes precedence if both are
   * provided.
   *
   * <p>Query params: GET /api/teams/{teamCode}/employees?userId=xxx&firstName=xxx
   * &lastName=xxx&roleCode=xxx&pageNumber=1&pageSize=10&sortBy=first_name&sortDirection=ASC
   *
   * <p>Request body: GET /api/teams/{teamCode}/employees with JSON body: { "condition": { "userId":
   * "xxx", "firstName": "xxx", "lastName": "xxx", "roleCode": "xxx" }, "pageRequest": {
   * "pageNumber": 1, "pageSize": 10 }, "sortRequests": [{ "sortBy": "first_name", "sortDirection":
   * "ASC" }] }
   *
   * @param teamCode the team code
   * @param requestBody optional request body with search criteria
   * @param userId optional user ID (email) filter (query param)
   * @param firstName optional first name filter (query param)
   * @param lastName optional last name filter (query param)
   * @param roleCode optional role code filter (query param)
   * @param pageNumber page number (query param)
   * @param pageSize page size (query param)
   * @param sortBy sort field (query param)
   * @param sortDirection sort direction (query param)
   * @return ResponseEntity containing the list of employees and pagination info
   */
  @org.springframework.web.bind.annotation.GetMapping("/{teamCode}/employees")
  public ResponseEntity<Response> searchEmployeesInTeam(
      @PathVariable @NotBlank String teamCode,
      @org.springframework.web.bind.annotation.RequestBody(required = false)
          PaginationSearchRequest<TeamEmployeeSearchCondition> requestBody,
      @org.springframework.web.bind.annotation.RequestParam(required = false) String userId,
      @org.springframework.web.bind.annotation.RequestParam(required = false) String firstName,
      @org.springframework.web.bind.annotation.RequestParam(required = false) String lastName,
      @org.springframework.web.bind.annotation.RequestParam(required = false) String roleCode,
      @org.springframework.web.bind.annotation.RequestParam(required = false) Integer pageNumber,
      @org.springframework.web.bind.annotation.RequestParam(required = false) Integer pageSize,
      @org.springframework.web.bind.annotation.RequestParam(required = false) String sortBy,
      @org.springframework.web.bind.annotation.RequestParam(required = false)
          String sortDirection) {
    log.info("Searching employees in team: {}", teamCode);

    PaginationSearchRequest<TeamEmployeeSearchCondition> request;

    // If request body is provided, use it; otherwise build from query parameters
    if (requestBody != null && requestBody.getCondition() != null) {
      request = requestBody;
    } else {
      // Build search request from query parameters
      TeamEmployeeSearchCondition condition = new TeamEmployeeSearchCondition();
      condition.setUserId(userId);
      condition.setFirstName(firstName);
      condition.setLastName(lastName);
      condition.setRoleCode(roleCode);

      com.uit.sociuscoremodules.shared.request.PageRequest pageRequest =
          new com.uit.sociuscoremodules.shared.request.PageRequest();
      pageRequest.setPageNumber(pageNumber);
      pageRequest.setPageSize(pageSize);

      java.util.List<com.uit.sociuscoremodules.shared.request.SortRequest> sortRequests =
          new java.util.ArrayList<>();
      if (sortBy != null) {
        com.uit.sociuscoremodules.shared.request.SortRequest sortRequest =
            new com.uit.sociuscoremodules.shared.request.SortRequest();
        sortRequest.setSortBy(sortBy);
        sortRequest.setSortDirection(sortDirection);
        sortRequests.add(sortRequest);
      }

      request = new PaginationSearchRequest<>();
      request.setCondition(condition);
      request.setPageRequest(pageRequest);
      request.setSortRequests(sortRequests);
    }

    java.util.Map<String, Object> result =
        teamEmployeeService.searchEmployeesInTeam(teamCode, request);

    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code("team.employees.found")
            .message(i18nService.getMessage("team.employees.found"))
            .data(result)
            .build();

    return ResponseEntity.ok(response);
  }
}
