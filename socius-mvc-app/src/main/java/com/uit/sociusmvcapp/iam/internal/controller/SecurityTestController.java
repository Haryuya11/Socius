package com.uit.sociusmvcapp.iam.internal.controller;

import com.uit.sociusmvcapp.iam.dto.UserPrincipal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for testing security features such as roles and permissions.
 *
 * <p>Includes endpoints to check current user info, authorities, and to test access control based
 * on system roles and scoped permissions (team/department).
 */
@RestController
@RequestMapping("/api/test-security")
@RequiredArgsConstructor
public class SecurityTestController {

  // ==========================================
  // 1. DEBUG INFO (Để xem User đang có quyền gì)
  // ==========================================

  /**
   * Xem toàn bộ thông tin UserPrincipal được load từ DB. Giúp check xem teams, departments,
   * permissions có data không.
   */
  @GetMapping("/me")
  public ResponseEntity<UserPrincipal> getCurrentUser(@AuthenticationPrincipal UserPrincipal user) {
    return ResponseEntity.ok(user);
  }

  /**
   * Xem danh sách Authority thô (String) mà Spring Security đang hiểu. Bạn sẽ thấy các chuỗi như:
   * "ROLE_USER", "TEAM:T01:task.create"...
   */
  @GetMapping("/my-authorities")
  public ResponseEntity<List<String>> getMyAuthorities() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    List<String> authorities =
        auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
    return ResponseEntity.ok(authorities);
  }

  // ==========================================
  // 2. TEST SYSTEM ROLE & GLOBAL PERMISSION
  // ==========================================

  /** Chỉ cho phép SYS_ADMIN truy cập. */
  @GetMapping("/admin-zone")
  @PreAuthorize("hasRole('SYS_ADMIN')")
  public ResponseEntity<String> testAdmin() {
    return ResponseEntity.ok("Chào mừng System Admin! Bạn có quyền tối cao.");
  }

  /** Cho phép ai có quyền xem profile. */
  @GetMapping("/profile-view")
  @PreAuthorize("hasAuthority('self.profile.view')")
  public ResponseEntity<String> testGlobalPerm() {
    return ResponseEntity.ok("Bạn có quyền xem profile cá nhân.");
  }

  // ==========================================
  // 3. TEST SCOPED PERMISSION (TEAM & DEPT)
  // ==========================================

  /**
   * * Test quyền tạo Task trong 1 Team cụ thể. Logic: User phải là TEAM_LEAD (hoặc role có quyền
   * task.create) TẠI teamCode này.
   */
  @PostMapping("/teams/{teamCode}/tasks")
  @PreAuthorize("@permissionService.hasTeamPermission(#teamCode, 'task.create')")
  public ResponseEntity<String> createTask(@PathVariable String teamCode) {
    return ResponseEntity.ok("Thành công! Bạn đã tạo task tại team: " + teamCode);
  }

  /**
   * Test quyền xem lương trong 1 Dept cụ thể. Logic: User phải là DEPT_DIR hoặc DEPT_MGR TẠI
   * deptCode này.
   */
  @GetMapping("/depts/{deptCode}/salary")
  @PreAuthorize("@permissionService.hasDeptPermission(#deptCode, 'employee.view.salary')")
  public ResponseEntity<String> viewSalary(@PathVariable String deptCode) {
    return ResponseEntity.ok("Thành công! Đang hiển thị bảng lương của phòng: " + deptCode);
  }
}
