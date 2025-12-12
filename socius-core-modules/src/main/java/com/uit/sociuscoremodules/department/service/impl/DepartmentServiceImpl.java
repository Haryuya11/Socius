package com.uit.sociuscoremodules.department.service.impl;

import com.uit.sociuscoremodules.department.dto.DepartmentDto;
import com.uit.sociuscoremodules.department.dto.DepartmentEmployeesDto;
import com.uit.sociuscoremodules.department.repository.DepartmentRepository;
import com.uit.sociuscoremodules.department.request.DepartmentCreateRequest;
import com.uit.sociuscoremodules.department.request.EmployeeAddRequest;
import com.uit.sociuscoremodules.department.service.DepartmentService;
import com.uit.sociuscoremodules.employee.dto.EmployeeDto;
import com.uit.sociuscoremodules.employee.repository.EmployeeRepository;
import com.uit.sociuscoremodules.shared.constants.MessageConstant;
import com.uit.sociuscoremodules.shared.service.impl.BaseServiceImpl;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Implementation of DepartmentService for department-related operations. */
@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl extends BaseServiceImpl implements DepartmentService {
  /** Repository for accessing department data. */
  private final DepartmentRepository departmentRepository;

  /** Repository for accessing employee data. */
  private final EmployeeRepository employeeRepository;

  // ========================= DEPARTMENT SERVICE MAIN METHODS =========================
  /**
   * Get department information by department ID.
   *
   * @param departmentId the ID of the department
   * @return DepartmentDto representing the department information
   */
  @Override
  public DepartmentDto departmentInfo(String departmentId) {
    DepartmentDto department = departmentRepository.findByDepartmentCode(departmentId);
    if (department == null) {
      throw badRequest(MessageConstant.E_DEP_003);
    }
    return department;
  }

  /**
   * Create a new department.
   *
   * @param request the request containing department creation details
   * @return Map containing the created department code
   */
  @Override
  public Map<String, String> createDepartment(DepartmentCreateRequest request) {
    DepartmentDto existingDepartment =
        departmentRepository.findByDepartmentCode(request.getDepartmentCode());
    if (existingDepartment != null) {
      throw badRequest(MessageConstant.E_DEP_005);
    }
    departmentRepository.create(request);

    return Map.of("departmentCode", request.getDepartmentCode());
  }

  /**
   * Update an existing department.
   *
   * @param request the request containing department update details
   * @param departmentCode the code of the department to update
   * @return Map containing the updated department code
   */
  @Override
  public Map<String, String> updateDepartment(
      DepartmentCreateRequest request, String departmentCode) {
    DepartmentDto existingDepartment = departmentRepository.findByDepartmentCode(departmentCode);
    if (existingDepartment == null) {
      throw badRequest(MessageConstant.W_DEP_001);
    }
    departmentRepository.update(request);
    return Map.of("departmentCode", request.getDepartmentCode());
  }

  /**
   * Deactivate a department.
   *
   * @param departmentCode the code of the department to deactivate
   * @return Map containing the deactivated department code
   */
  @Override
  public Map<String, String> deactivateDepartment(String departmentCode) {
    DepartmentDto existingDepartment = departmentRepository.findByDepartmentCode(departmentCode);
    if (existingDepartment == null) {
      throw badRequest(MessageConstant.W_DEP_001);
    }
    if (departmentRepository.countEmployeesInDepartment(departmentCode) > 0) {
      throw badRequest(MessageConstant.E_DEP_007);
    }
    departmentRepository.deactivate(departmentCode);
    return Map.of("departmentCode", departmentCode);
  }

  /**
   * Activate a department.
   *
   * @param departmentCode the code of the department to activate
   * @return Map containing the activated department code
   */
  @Override
  public Map<String, String> activateDepartment(String departmentCode) {
    DepartmentDto existingDepartment = departmentRepository.findByDepartmentCode(departmentCode);
    if (existingDepartment == null) {
      throw badRequest(MessageConstant.W_DEP_001);
    }
    departmentRepository.activate(departmentCode);
    return Map.of("departmentCode", departmentCode);
  }

  /**
   * Get all departments.
   *
   * @return List of DepartmentDto representing all departments
   */
  @Override
  public List<DepartmentDto> getAllDepartments() {
    return departmentRepository.getAllDepartments();
  }

  /**
   * Get all employees in a department by department code.
   *
   * @param departmentCode the code of the department
   * @return List of DepartmentEmployeesDto representing employees in the department
   */
  @Override
  public List<DepartmentEmployeesDto> getEmployeesByDepartmentCode(String departmentCode) {
    DepartmentDto existingDepartment = departmentRepository.findByDepartmentCode(departmentCode);
    if (existingDepartment == null) {
      throw badRequest(MessageConstant.W_DEP_001);
    }
    return departmentRepository.getEmployeesByDepartmentCode(departmentCode);
  }

  /**
   * Add an employee to a department.
   *
   * @param request the request containing employee addition details
   * @param departmentCode the code of the department
   * @return EmployeeDto representing the added employee
   */
  @Override
  public EmployeeDto addEmployeeToDepartment(EmployeeAddRequest request, String departmentCode) {
    DepartmentDto existingDepartment = departmentRepository.findByDepartmentCode(departmentCode);
    if (existingDepartment == null) {
      throw badRequest(MessageConstant.W_DEP_001);
    }
    EmployeeDto employee = employeeRepository.findByClientId(request.getEmployeeId());
    if (employee == null) {
      throw badRequest(MessageConstant.W_EMP_002);
    }
    departmentRepository.addEmployeeToDepartment(request, departmentCode);
    return employeeRepository.findByClientId(request.getEmployeeId());
  }

  /**
   * Remove an employee from a department.
   *
   * @param departmentCode the code of the department
   * @param employeeId the client ID of the employee to remove
   * @return EmployeeDto representing the removed employee
   */
  @Override
  public EmployeeDto removeEmployeeFromDepartment(String departmentCode, String employeeId) {
    DepartmentDto existingDepartment = departmentRepository.findByDepartmentCode(departmentCode);
    if (existingDepartment == null) {
      throw badRequest(MessageConstant.W_DEP_001);
    }
    EmployeeDto employee = employeeRepository.findByClientId(employeeId);
    if (employee == null) {
      throw badRequest(MessageConstant.W_EMP_002);
    }
    departmentRepository.removeEmployeeFromDepartment(departmentCode, employeeId);
    return employeeRepository.findByClientId(employeeId);
  }

  /**
   * Transfer an employee from one department to another.
   *
   * @param fromDepartmentCode the code of the department to transfer from
   * @param request the request containing employee transfer details
   * @param toDepartmentCode the code of the department to transfer to
   * @return Map containing transfer details
   */
  @Override
  public Map<String, String> transferEmployee(
      String fromDepartmentCode, EmployeeAddRequest request, String toDepartmentCode) {
    DepartmentDto fromDepartment = departmentRepository.findByDepartmentCode(fromDepartmentCode);
    if (fromDepartment == null) {
      throw badRequest(MessageConstant.E_DEP_009);
    }
    DepartmentDto toDepartment = departmentRepository.findByDepartmentCode(toDepartmentCode);
    if (toDepartment == null) {
      throw badRequest(MessageConstant.E_DEP_009);
    }
    EmployeeDto employee = employeeRepository.findByClientId(request.getEmployeeId());
    if (employee == null) {
      throw badRequest(MessageConstant.W_EMP_002);
    }
    departmentRepository.removeEmployeeFromDepartment(fromDepartmentCode, request.getEmployeeId());
    departmentRepository.addEmployeeToDepartment(request, toDepartmentCode);
    return Map.of(
        "employeeId",
        request.getEmployeeId(),
        "fromDepartmentCode",
        fromDepartmentCode,
        "toDepartmentCode",
        toDepartmentCode);
  }
}
