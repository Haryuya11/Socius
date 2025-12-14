package com.uit.sociuscoremodules.department.service.impl;

import com.uit.sociuscoremodules.department.constants.DepartmentConstant;
import com.uit.sociuscoremodules.department.converter.DepartmentConverter;
import com.uit.sociuscoremodules.department.dto.BatchErrorDto;
import com.uit.sociuscoremodules.department.dto.DepartmentDto;
import com.uit.sociuscoremodules.department.dto.DepartmentEmployeeBatchResultDto;
import com.uit.sociuscoremodules.department.dto.DepartmentEmployeesDto;
import com.uit.sociuscoremodules.department.repository.DepartmentRepository;
import com.uit.sociuscoremodules.department.request.DepartmentCreateRequest;
import com.uit.sociuscoremodules.department.request.EmployeeAddManyRequest;
import com.uit.sociuscoremodules.department.request.EmployeeAddRequest;
import com.uit.sociuscoremodules.department.request.TransferEmployeeRequest;
import com.uit.sociuscoremodules.department.service.DepartmentService;
import com.uit.sociuscoremodules.employee.dto.EmployeeDto;
import com.uit.sociuscoremodules.employee.repository.EmployeeRepository;
import com.uit.sociuscoremodules.shared.constants.CommonConstant;
import com.uit.sociuscoremodules.shared.constants.MessageConstant;
import com.uit.sociuscoremodules.shared.service.I18nService;
import com.uit.sociuscoremodules.shared.service.impl.BaseServiceImpl;
import java.util.ArrayList;
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

  /** Service for internationalization (i18n) support. */
  private final I18nService i18nService;

  /** Converter for transforming department data. */
  private final DepartmentConverter departmentConverter;

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
      throw notFound(MessageConstant.E_DEP_003);
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
      throw notFound(MessageConstant.E_DEP_005);
    }
    DepartmentDto deletedDepartment =
        departmentRepository.findDeletedByDepartmentCode(request.getDepartmentCode());

    if (deletedDepartment == null) {
      departmentRepository.create(request);
    } else {
      departmentRepository.activate(request);
    }

    return Map.of(DepartmentConstant.DEPARTMENT_CODE, request.getDepartmentCode());
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
      throw notFound(MessageConstant.W_DEP_001);
    }
    departmentRepository.update(request);
    return Map.of(DepartmentConstant.DEPARTMENT_CODE, request.getDepartmentCode());
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
      throw notFound(MessageConstant.W_DEP_001);
    }
    if (departmentRepository.countEmployeesInDepartment(departmentCode)
        > CommonConstant.INIT_INDEX) {
      throw badRequest(MessageConstant.E_DEP_007);
    }
    departmentRepository.deactivate(departmentCode);
    return Map.of(DepartmentConstant.DEPARTMENT_CODE, departmentCode);
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
      throw notFound(MessageConstant.W_DEP_001);
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
  public EmployeeDto addEmployeeToDepartment(
      EmployeeAddRequest request, String departmentCode, String employeeId) {
    DepartmentDto existingDepartment = departmentRepository.findByDepartmentCode(departmentCode);
    if (existingDepartment == null) {
      throw notFound(MessageConstant.W_DEP_001);
    }
    EmployeeDto employee = employeeRepository.findByClientId(employeeId);
    if (employee == null) {
      throw notFound(MessageConstant.W_EMP_002);
    }
    departmentRepository.addEmployeeToDepartment(request, departmentCode, employeeId);
    return employeeRepository.findByClientId(employeeId);
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
      throw notFound(MessageConstant.W_DEP_001);
    }
    EmployeeDto employee = employeeRepository.findByClientId(employeeId);
    if (employee == null) {
      throw notFound(MessageConstant.W_EMP_002);
    }
    departmentRepository.removeEmployeeFromDepartment(departmentCode, employeeId);
    return employeeRepository.findByClientId(employeeId);
  }

  /**
   * Transfer an employee from one department to another.
   *
   * @param request the request containing transfer details
   * @return Map containing transfer details
   */
  @Override
  public Map<String, String> transferEmployee(TransferEmployeeRequest request) {
    DepartmentDto fromDepartment =
        departmentRepository.findByDepartmentCode(request.getFromDepartmentCode());

    if (fromDepartment == null) {
      throw notFound(MessageConstant.E_DEP_009);
    }
    DepartmentDto toDepartment =
        departmentRepository.findByDepartmentCode(request.getToDepartmentCode());
    if (toDepartment == null) {
      throw notFound(MessageConstant.E_DEP_009);
    }

    DepartmentEmployeesDto existingEmployeeInFromDepartment =
        departmentRepository.findEmployeeInDepartment(
            request.getFromDepartmentCode(), request.getEmployeeId());
    if (existingEmployeeInFromDepartment == null) {
      throw notFound(MessageConstant.E_DEP_010);
    }

    DepartmentEmployeesDto existingEmployeeInToDepartment =
        departmentRepository.findEmployeeInDepartment(
            request.getToDepartmentCode(), request.getEmployeeId());
    if (existingEmployeeInToDepartment != null) {
      throw badRequest(MessageConstant.E_DEP_011);
    }

    EmployeeAddRequest addRequest =
        departmentConverter.toEmployeeAddRequest(request.getRoleCode(), request.getIsPrimary());
    departmentRepository.removeEmployeeFromDepartment(
        request.getFromDepartmentCode(), request.getEmployeeId());
    departmentRepository.addEmployeeToDepartment(
        addRequest, request.getToDepartmentCode(), request.getEmployeeId());
    return Map.of(
        DepartmentConstant.EMPLOYEE_ID,
        request.getEmployeeId(),
        DepartmentConstant.FROM_DEPARTMENT_CODE,
        request.getFromDepartmentCode(),
        DepartmentConstant.TO_DEPARTMENT_CODE,
        request.getToDepartmentCode());
  }

  /**
   * Add multiple employees to a department in batch.
   *
   * @param requests the list of requests containing employee addition details
   * @param departmentCode the code of the department
   * @return DepartmentEmployeeBatchResultDto containing batch operation results
   */
  @Override
  public DepartmentEmployeeBatchResultDto addEmployeesToDepartmentBatch(
      List<EmployeeAddManyRequest> requests, String departmentCode) {
    DepartmentDto existingDepartment = departmentRepository.findByDepartmentCode(departmentCode);
    if (existingDepartment == null) {
      throw notFound(MessageConstant.W_DEP_001);
    }

    List<String> successful = new ArrayList<>();
    List<BatchErrorDto> failed = new ArrayList<>();

    for (EmployeeAddManyRequest request : requests) {
      try {
        EmployeeDto employee = employeeRepository.findByClientId(request.getEmployeeId());
        if (employee == null) {
          throw notFound(MessageConstant.W_EMP_002);
        }

        DepartmentEmployeesDto existingEmployeeInDepartment =
            departmentRepository.findEmployeeInDepartment(departmentCode, request.getEmployeeId());
        if (existingEmployeeInDepartment != null) {
          throw badRequest(MessageConstant.E_DEP_011);
        }

        EmployeeAddRequest addRequest =
            departmentConverter.toEmployeeAddRequest(request.getRoleCode(), request.getIsPrimary());
        departmentRepository.addEmployeeToDepartment(
            addRequest, departmentCode, request.getEmployeeId());
        successful.add(request.getEmployeeId());
      } catch (Exception e) {
        String errorMessage = i18nService.getMessage(e.getMessage());
        failed.add(new BatchErrorDto(request.getEmployeeId(), errorMessage));
      }
    }
    return new DepartmentEmployeeBatchResultDto(successful, failed);
  }

  /**
   * Remove multiple employees from a department in batch.
   *
   * @param employeeIds the list of employee client IDs to be removed
   * @param departmentCode the code of the department
   * @return DepartmentEmployeeBatchResultDto containing batch operation results
   */
  @Override
  public DepartmentEmployeeBatchResultDto removeEmployeesFromDepartmentBatch(
      List<String> employeeIds, String departmentCode) {
    DepartmentDto existingDepartment = departmentRepository.findByDepartmentCode(departmentCode);
    if (existingDepartment == null) {
      throw notFound(MessageConstant.W_DEP_001);
    }

    List<String> successful = new ArrayList<>();
    List<BatchErrorDto> failed = new ArrayList<>();

    for (String employeeId : employeeIds) {
      try {
        EmployeeDto employee = employeeRepository.findByClientId(employeeId);
        if (employee == null) {
          throw notFound(MessageConstant.W_EMP_002);
        }

        DepartmentEmployeesDto existingEmployeeInDepartment =
            departmentRepository.findEmployeeInDepartment(departmentCode, employeeId);
        if (existingEmployeeInDepartment == null) {
          throw notFound(MessageConstant.E_DEP_010);
        }

        departmentRepository.removeEmployeeFromDepartment(departmentCode, employeeId);
        successful.add(employeeId);
      } catch (Exception e) {
        String errorMessage = i18nService.getMessage(e.getMessage());
        failed.add(new BatchErrorDto(employeeId, errorMessage));
      }
    }
    return new DepartmentEmployeeBatchResultDto(successful, failed);
  }

  /**
   * Change an employee's role in a department.
   *
   * @param departmentCode the code of the department
   * @param employeeId the client ID of the employee whose role is to be changed
   * @return Map containing role change confirmation details
   */
  @Override
  public Map<String, String> changeEmployeeRoleInDepartment(
      String departmentCode, String employeeId, String roleCode) {
    DepartmentDto existingDepartment = departmentRepository.findByDepartmentCode(departmentCode);
    if (existingDepartment == null) {
      throw notFound(MessageConstant.W_DEP_001);
    }
    EmployeeDto employee = employeeRepository.findByClientId(employeeId);
    if (employee == null) {
      throw notFound(MessageConstant.W_EMP_002);
    }
    departmentRepository.changeEmployeeRole(departmentCode, employeeId, roleCode);
    return Map.of(
        DepartmentConstant.EMPLOYEE_ID,
        employeeId,
        DepartmentConstant.DEPARTMENT_CODE,
        departmentCode,
        DepartmentConstant.ROLE_CODE,
        roleCode);
  }
}
