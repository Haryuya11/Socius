package com.uit.sociusmvcapp.endpoint;

import com.uit.sociuscoremodules.shared.request.PaginationSearchRequest;
import com.uit.sociuscoremodules.shared.response.Response;
import com.uit.sociuscoremodules.shared.service.I18nService;
import com.uit.sociuscoremodules.team.dto.TeamDto;
import com.uit.sociuscoremodules.team.request.TeamCreateRequest;
import com.uit.sociuscoremodules.team.request.TeamSearchRequest;
import com.uit.sociuscoremodules.team.request.TeamUpdateRequest;
import com.uit.sociuscoremodules.team.service.TeamService;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** TeamController handles HTTP requests related to team operations. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/teams")
public class TeamController {

  private final I18nService i18nService;
  private final TeamService teamService;

  /**
   * Create a new team.
   *
   * @param request the team creation request
   * @return ResponseEntity containing the created team
   */
  @PostMapping
  public ResponseEntity<Response> createTeam(@Valid @RequestBody TeamCreateRequest request) {
    TeamDto team = teamService.createTeam(request);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.CREATED.value())
            .code("team.created")
            .message(i18nService.getMessage("team.created"))
            .data(team)
            .build();
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * Search teams with pagination and sorting.
   *
   * <p>Supports both query parameters and request body. Request body takes precedence if both are
   * provided.
   *
   * <p>Query params: GET /api/teams?teamCode=xxx&teamName=xxx&departmentCode=xxx
   * &pageNumber=1&pageSize=10&sortBy=id&sortDirection=ASC
   *
   * <p>Request body: GET /api/teams with JSON body: { "condition": { "teamCode": "xxx", "teamName":
   * "xxx", "departmentCode": "xxx" }, "pageRequest": { "pageNumber": 1, "pageSize": 10 },
   * "sortRequests": [{ "sortBy": "id", "sortDirection": "ASC" }] }
   *
   * @param requestBody optional request body with search criteria
   * @param teamCode optional team code filter (query param)
   * @param teamName optional team name filter (query param)
   * @param departmentCode optional department code filter (query param)
   * @param pageNumber page number (query param)
   * @param pageSize page size (query param)
   * @param sortBy sort field (query param)
   * @param sortDirection sort direction (query param)
   * @return ResponseEntity containing the list of teams and pagination info
   */
  @GetMapping
  public ResponseEntity<Response> searchTeams(
      @org.springframework.web.bind.annotation.RequestBody(required = false)
          PaginationSearchRequest<TeamSearchRequest> requestBody,
      @org.springframework.web.bind.annotation.RequestParam(required = false) String teamCode,
      @org.springframework.web.bind.annotation.RequestParam(required = false) String teamName,
      @org.springframework.web.bind.annotation.RequestParam(required = false) String departmentCode,
      @org.springframework.web.bind.annotation.RequestParam(required = false) Integer pageNumber,
      @org.springframework.web.bind.annotation.RequestParam(required = false) Integer pageSize,
      @org.springframework.web.bind.annotation.RequestParam(required = false) String sortBy,
      @org.springframework.web.bind.annotation.RequestParam(required = false)
          String sortDirection) {

    PaginationSearchRequest<TeamSearchRequest> request;

    // If request body is provided, use it; otherwise build from query parameters
    if (requestBody != null && requestBody.getCondition() != null) {
      request = requestBody;
    } else {
      // Build search request from query parameters
      TeamSearchRequest condition = new TeamSearchRequest();
      condition.setTeamCode(teamCode);
      condition.setTeamName(teamName);
      condition.setDepartmentCode(departmentCode);

      com.uit.sociuscoremodules.shared.request.PageRequest pageRequest =
          new com.uit.sociuscoremodules.shared.request.PageRequest();
      pageRequest.setPageNumber(pageNumber);
      pageRequest.setPageSize(pageSize);

      List<com.uit.sociuscoremodules.shared.request.SortRequest> sortRequests = new ArrayList<>();
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

    Map<String, Object> result = teamService.searchTeams(request);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code("teams.found")
            .message(i18nService.getMessage("teams.found"))
            .data(result)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Get team by team code.
   *
   * @param teamCode the team code
   * @return ResponseEntity containing the team
   */
  @GetMapping("/{teamCode}")
  public ResponseEntity<Response> getTeamByTeamCode(@PathVariable String teamCode) {
    TeamDto team = teamService.getTeamByTeamCode(teamCode);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code("team.found")
            .message(i18nService.getMessage("team.found"))
            .data(team)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Update team information.
   *
   * @param teamCode the team code
   * @param request the team update request
   * @return ResponseEntity containing the updated team
   */
  @PatchMapping("/{teamCode}")
  public ResponseEntity<Response> updateTeam(
      @PathVariable String teamCode, @Valid @RequestBody TeamUpdateRequest request) {
    TeamDto team = teamService.updateTeam(teamCode, request);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code("team.updated")
            .message(i18nService.getMessage("team.updated"))
            .data(team)
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Soft delete a team.
   *
   * @param teamCode the team code
   * @return ResponseEntity indicating the result
   */
  @DeleteMapping("/{teamCode}")
  public ResponseEntity<Response> deleteTeam(@PathVariable String teamCode) {
    teamService.deleteTeam(teamCode);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code("team.deleted")
            .message(i18nService.getMessage("team.deleted"))
            .build();
    return ResponseEntity.ok(response);
  }
}
