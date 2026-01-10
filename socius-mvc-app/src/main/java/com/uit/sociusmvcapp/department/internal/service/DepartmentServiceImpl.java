package com.uit.sociusmvcapp.department.internal.service;

import com.uit.sociusmvcapp.department.DepartmentActionGuard;
import com.uit.sociusmvcapp.department.DepartmentService;
import com.uit.sociusmvcapp.department.dto.DepartmentDto;
import com.uit.sociusmvcapp.department.dto.request.CreateDepartmentRequest;
import com.uit.sociusmvcapp.department.enums.DepartmentActionType;
import com.uit.sociusmvcapp.department.internal.repository.DepartmentRepository;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.service.ExceptionFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Implementation of DepartmentService for department-related operations. */
@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
  /** Repository for accessing department data. */
  private final DepartmentRepository departmentRepository;

  /** List of guards for department actions. */
  private final List<DepartmentActionGuard> guards;

  /**
   * Get department information by department code.
   *
   * @param departmentCode the code of the department
   * @return DepartmentDto representing the department information
   */
  @Override
  public DepartmentDto findByDepartmentCode(String departmentCode) {
    DepartmentDto department = departmentRepository.findByDepartmentCode(departmentCode);
    if (department == null) {
      throw ExceptionFactory.notFound(MessageConstant.E_DEP_003);
    }
    return department;
  }

  /**
   * Create a new department.
   *
   * @param request the request containing department creation details
   */
  @Override
  public void create(CreateDepartmentRequest request) {
    DepartmentDto existingDepartment =
        departmentRepository.findByDepartmentCode(request.getDepartmentCode());
    if (existingDepartment != null) {
      throw ExceptionFactory.notFound(MessageConstant.E_DEP_005);
    }
    DepartmentDto deletedDepartment =
        departmentRepository.findDeletedByDepartmentCode(request.getDepartmentCode());

    if (deletedDepartment == null) {
      departmentRepository.create(request);
    } else {
      departmentRepository.activate(request);
    }
  }

  /**
   * Update an existing department.
   *
   * @param request the request containing department update details
   * @param departmentCode the code of the department to update
   */
  @Override
  public void update(CreateDepartmentRequest request, String departmentCode) {
    if (request.getDepartmentCode() != null && !request.getDepartmentCode().equals(departmentCode)) {
      throw ExceptionFactory.badRequest(MessageConstant.E_DEP_002);
    }
    DepartmentDto existingDepartment = departmentRepository.findByDepartmentCode(departmentCode);
    if (existingDepartment == null) {
      throw ExceptionFactory.notFound(MessageConstant.W_DEP_001);
    }
    departmentRepository.update(request);
  }

  /**
   * Deactivate a department.
   *
   * @param departmentCode the code of the department to deactivate
   */
  @Override
  public void deactivate(String departmentCode) {
    this.validateExists(departmentCode);
    for (DepartmentActionGuard guard : guards) {
      guard.validate(DepartmentActionType.DEACTIVATE, departmentCode);
    }
    departmentRepository.deactivate(departmentCode);
  }

  /**
   * Get all departments.
   *
   * @return List of DepartmentDto representing all departments
   */
  @Override
  public List<DepartmentDto> findAll() {
    return departmentRepository.findAll();
  }

  /**
   * Validate if a department exists by its code.
   *
   * @param departmentCode the code of the department
   */
  @Override
  public void validateExists(String departmentCode) {
    if (!departmentRepository.existsByDepartmentCode(departmentCode)) {
      throw ExceptionFactory.notFound(MessageConstant.W_DEP_001);
    }
  }
}
