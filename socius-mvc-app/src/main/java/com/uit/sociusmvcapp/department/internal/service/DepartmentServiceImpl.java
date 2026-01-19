package com.uit.sociusmvcapp.department.internal.service;

import com.uit.sociusmvcapp.department.DepartmentActionGuard;
import com.uit.sociusmvcapp.department.DepartmentEmployeeGateway;
import com.uit.sociusmvcapp.department.DepartmentService;
import com.uit.sociusmvcapp.department.dto.DepartmentDto;
import com.uit.sociusmvcapp.department.dto.SearchDepartmentDto;
import com.uit.sociusmvcapp.department.dto.request.CreateDepartmentRequest;
import com.uit.sociusmvcapp.department.dto.request.SearchDepartmentRequest;
import com.uit.sociusmvcapp.department.dto.request.UpdateDepartmentRequest;
import com.uit.sociusmvcapp.department.enums.DepartmentActionType;
import com.uit.sociusmvcapp.department.internal.repository.DepartmentRepository;
import com.uit.sociusmvcapp.iam.UserContentProvider;
import com.uit.sociusmvcapp.notification.NotificationService;
import com.uit.sociusmvcapp.shared.constants.CommonConstant;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.event.NotificationSendEvent;
import com.uit.sociusmvcapp.shared.request.PaginationSearchRequest;
import com.uit.sociusmvcapp.shared.response.PageResponse;
import com.uit.sociusmvcapp.shared.service.ExceptionFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Implementation of DepartmentService for department-related operations. */
@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
  /** Repository for accessing department data. */
  private final DepartmentRepository departmentRepository;

  /** List of guards for department actions. */
  private final List<DepartmentActionGuard> guards;

  private final DepartmentEmployeeGateway departmentEmployeeGateway;

  private final ApplicationEventPublisher eventPublisher;

  private final UserContentProvider userContentProvider;

  private final NotificationService notificationService;

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
  @Transactional
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

    // Send notification to the user who created the department
    eventPublisher.publishEvent(
        new NotificationSendEvent(
            this,
            userContentProvider.getUserContent().getClientId(),
            "Department Created",
            String.format(
                "Department '%s' (%s) has been successfully created.",
                request.getDepartmentName(), request.getDepartmentCode()),
            "/departments/" + request.getDepartmentCode()));
  }

  /**
   * Update an existing department.
   *
   * @param request the request containing department update details
   * @param departmentCode the code of the department to update
   */
  @Override
  @Transactional
  public void update(UpdateDepartmentRequest request, String departmentCode) {
    if (StringUtils.isEmpty(departmentCode)) {
      throw ExceptionFactory.badRequest(MessageConstant.E_DEP_002);
    }
    DepartmentDto existingDepartment = departmentRepository.findByDepartmentCode(departmentCode);
    if (existingDepartment == null) {
      throw ExceptionFactory.notFound(MessageConstant.W_DEP_001);
    }
    departmentRepository.update(request, departmentCode);

    // Send batch notification to all department members about the update
    List<String> memberIds = departmentEmployeeGateway.getActiveMemberIds(departmentCode);
    if (!memberIds.isEmpty()) {
      notificationService.sendMultiNotification(
          memberIds,
          "Department Updated",
          String.format(
              "Department '%s' (%s) has been updated.",
              request.getDepartmentName(), departmentCode),
          "/departments/" + departmentCode);
    }
  }

  /**
   * Deactivate a department.
   *
   * @param departmentCode the code of the department to deactivate
   */
  @Override
  @Transactional
  public void deactivate(String departmentCode) {
    this.validateExists(departmentCode);
    for (DepartmentActionGuard guard : guards) {
      guard.validate(DepartmentActionType.DEACTIVATE, departmentCode);
    }
    DepartmentDto department = departmentRepository.findByDepartmentCode(departmentCode);
    departmentRepository.deactivate(departmentCode);

    // Send notification to the user who deleted the department
    eventPublisher.publishEvent(
        new NotificationSendEvent(
            this,
            userContentProvider.getUserContent().getClientId(),
            "Department Deleted",
            String.format(
                "Department '%s' (%s) has been deleted.",
                department.getDepartmentName(), departmentCode),
            "/departments"));
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

  @Override
  public PageResponse<SearchDepartmentDto> search(
      PaginationSearchRequest<SearchDepartmentRequest> request) {
    int limit = request.getPageRequest().getPageSize();
    int offset = (request.getPageRequest().getPageNumber() - 1) * limit;
    SearchDepartmentRequest criteria = request.getCondition();

    // 1. Đếm tổng số bản ghi thỏa điều kiện
    int total = departmentRepository.count(criteria);
    if (total == CommonConstant.INIT_INDEX) {
      log.info("No departments found matching the search criteria.");
      return PageResponse.empty();
    }

    // 2. Tìm kiếm chi tiết với phân trang và sắp xếp
    List<SearchDepartmentDto> result =
        departmentRepository.search(criteria, request.getSortRequests(), limit, offset);

    return PageResponse.of(result, total, offset, limit);
  }
}
