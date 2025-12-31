package com.uit.sociusmvcapp.department;

import com.uit.sociusmvcapp.department.dto.DepartmentDto;
import com.uit.sociusmvcapp.department.dto.request.CreateDepartmentRequest;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.response.Response;
import com.uit.sociusmvcapp.shared.service.I18nService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** DepartmentController handles HTTP requests related to department operations. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/departments")
public class DepartmentController {
  /** I18nService for internationalization messages. */
  private final I18nService i18nService;

  /** DepartmentService for department-related operations. */
  private final DepartmentService departmentService;

  /**
   * Get department information by department code.
   *
   * @return ResponseEntity containing the department information
   */
  @GetMapping("/{departmentCode}")
  public ResponseEntity<Response> findByDepartmentCode(@PathVariable String departmentCode) {
    DepartmentDto department = departmentService.findByDepartmentCode(departmentCode);

    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_DEP_003)
            .message(i18nService.getMessage(MessageConstant.S_DEP_003))
            .data(department)
            .build();

    return ResponseEntity.ok(response);
  }

  /**
   * Create a new department.
   *
   * @param request the request containing department creation details
   * @return ResponseEntity containing the created department code
   */
  @PostMapping
  public ResponseEntity<Response> create(@RequestBody CreateDepartmentRequest request) {
    departmentService.create(request);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_DEP_001)
            .message(i18nService.getMessage(MessageConstant.S_DEP_001))
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Update an existing department.
   *
   * @param request the request containing department update details
   * @param departmentCode the code of the department to update
   * @return ResponseEntity containing the updated department code
   */
  @PutMapping("/{departmentCode}")
  public ResponseEntity<Response> update(
      @RequestBody CreateDepartmentRequest request, @PathVariable String departmentCode) {
    departmentService.update(request, departmentCode);
    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_DEP_002)
            .message(i18nService.getMessage(MessageConstant.S_DEP_002))
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Deactivate a department.
   *
   * @param departmentCode the code of the department to deactivate
   * @return ResponseEntity indicating the result of the operation
   */
  @DeleteMapping("/{departmentCode}")
  public ResponseEntity<Response> deactivate(@PathVariable String departmentCode) {
    departmentService.deactivate(departmentCode);

    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_DEP_004)
            .message(i18nService.getMessage(MessageConstant.S_DEP_004))
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Get all departments.
   *
   * @return ResponseEntity containing the list of all departments
   */
  @GetMapping
  public ResponseEntity<Response> findAll() {
    List<DepartmentDto> departments = departmentService.findAll();

    Response response =
        Response.builder()
            .success(true)
            .status(HttpStatus.OK.value())
            .code(MessageConstant.S_DEP_005)
            .message(i18nService.getMessage(MessageConstant.S_DEP_005))
            .data(departments)
            .build();
    return ResponseEntity.ok(response);
  }
}
