# API Permission Testing Checklist

Tài liệu này liệt kê tất cả các test case để kiểm tra hệ thống phân quyền RBAC. Mỗi test case có checkbox để đánh dấu khi hoàn thành.

## Hướng Dẫn Sử Dụng

1. Sao chép file này hoặc tạo issue từ template
2. Thực hiện từng test case theo thứ tự
3. Đánh dấu `[x]` khi test case **PASS**
4. Ghi chú lỗi nếu test case **FAIL**

---

## Mục Lục

1. [Kiểm Tra Xác Thực (Authentication)](#1-kiểm-tra-xác-thực-authentication)
2. [Kiểm Tra Quyền Hệ Thống (SYS_ADMIN)](#2-kiểm-tra-quyền-hệ-thống-sys_admin)
3. [Kiểm Tra Quyền USER Cơ Bản](#3-kiểm-tra-quyền-user-cơ-bản)
4. [Kiểm Tra Scope Department](#4-kiểm-tra-scope-department)
5. [Kiểm Tra Scope Team](#5-kiểm-tra-scope-team)
6. [Kiểm Tra Task (Team Scoped)](#6-kiểm-tra-task-team-scoped)
7. [Kiểm Tra Cross-Scope Access (Rất Quan Trọng)](#7-kiểm-tra-cross-scope-access-rất-quan-trọng)
8. [Kiểm Tra Role Hierarchy](#8-kiểm-tra-role-hierarchy)
9. [Kiểm Tra Public Endpoints](#9-kiểm-tra-public-endpoints)
10. [Kiểm Tra Edge Cases](#10-kiểm-tra-edge-cases)

---

## Test Data Setup

Trước khi test, cần có các user với các role sau:

| User | System Role | Department | Dept Role | Team | Team Role |
|------|-------------|------------|-----------|------|-----------|
| admin | SYS_ADMIN | - | - | - | - |
| user1 | USER | DEPT01 | DEPT_DIR | T01 | TEAM_LEAD |
| user2 | USER | DEPT01 | DEPT_MEM | T01 | TEAM_MEM |
| user3 | USER | DEPT02 | DEPT_MGR | T02 | TEAM_LEAD |
| user4 | USER | - | - | - | - |

---

## 1. Kiểm Tra Xác Thực (Authentication)

### 1.1. Request Không Có Token

- [ ] **TC-AUTH-001**: `GET /api/employees/profile` không có token → `401 Unauthorized`
- [ ] **TC-AUTH-002**: `GET /api/departments/DEPT01` không có token → `401 Unauthorized`
- [ ] **TC-AUTH-003**: `GET /api/teams/T01/tasks` không có token → `401 Unauthorized`
- [ ] **TC-AUTH-004**: `POST /api/teams` không có token → `401 Unauthorized`
- [ ] **TC-AUTH-005**: `PUT /api/employees/abc123` không có token → `401 Unauthorized`

### 1.2. Request Với Token Không Hợp Lệ

- [ ] **TC-AUTH-006**: `GET /api/employees/profile` với token expired → `401 Unauthorized`
- [ ] **TC-AUTH-007**: `GET /api/employees/profile` với token sai signature → `401 Unauthorized`
- [ ] **TC-AUTH-008**: `GET /api/employees/profile` với token malformed → `401 Unauthorized`

### 1.3. Request Với Token Hợp Lệ Nhưng User Không Tồn Tại

- [ ] **TC-AUTH-009**: Token của user đã bị xóa khỏi DB → `401 Unauthorized` hoặc `403 Forbidden`

---

## 2. Kiểm Tra Quyền Hệ Thống (SYS_ADMIN)

> SYS_ADMIN có quyền `system.full` - bypass tất cả các kiểm tra phân quyền

### 2.1. SYS_ADMIN Truy Cập Tất Cả Resources

- [ ] **TC-ADMIN-001**: SYS_ADMIN `GET /api/employees/profile` → `200 OK`
- [ ] **TC-ADMIN-002**: SYS_ADMIN `GET /api/employees/{any_clientId}` → `200 OK`
- [ ] **TC-ADMIN-003**: SYS_ADMIN `PUT /api/employees/{any_clientId}` → `200 OK`
- [ ] **TC-ADMIN-004**: SYS_ADMIN `DELETE /api/employees/{any_clientId}` → `200 OK`

### 2.2. SYS_ADMIN Truy Cập Department Bất Kỳ

- [ ] **TC-ADMIN-005**: SYS_ADMIN `GET /api/departments/DEPT01` → `200 OK`
- [ ] **TC-ADMIN-006**: SYS_ADMIN `GET /api/departments/DEPT02` → `200 OK`
- [ ] **TC-ADMIN-007**: SYS_ADMIN `PUT /api/departments/DEPT01` → `200 OK`
- [ ] **TC-ADMIN-008**: SYS_ADMIN `DELETE /api/departments/DEPT01` → `200 OK`
- [ ] **TC-ADMIN-009**: SYS_ADMIN `POST /api/departments` → `200 OK` hoặc `201 Created`
- [ ] **TC-ADMIN-010**: SYS_ADMIN `GET /api/departments/DEPT01/employees` → `200 OK`
- [ ] **TC-ADMIN-011**: SYS_ADMIN `POST /api/departments/DEPT01/employees` → `200 OK`

### 2.3. SYS_ADMIN Truy Cập Team Bất Kỳ

- [ ] **TC-ADMIN-012**: SYS_ADMIN `GET /api/teams/T01` → `200 OK`
- [ ] **TC-ADMIN-013**: SYS_ADMIN `GET /api/teams/T02` → `200 OK`
- [ ] **TC-ADMIN-014**: SYS_ADMIN `PUT /api/teams/T01` → `200 OK`
- [ ] **TC-ADMIN-015**: SYS_ADMIN `DELETE /api/teams/T01` → `200 OK`
- [ ] **TC-ADMIN-016**: SYS_ADMIN `POST /api/teams` → `200 OK` hoặc `201 Created`
- [ ] **TC-ADMIN-017**: SYS_ADMIN `GET /api/teams/T01/employees` → `200 OK`
- [ ] **TC-ADMIN-018**: SYS_ADMIN `POST /api/teams/T01/employees` → `200 OK`

### 2.4. SYS_ADMIN Truy Cập Task Bất Kỳ

- [ ] **TC-ADMIN-019**: SYS_ADMIN `GET /api/teams/T01/tasks` → `200 OK`
- [ ] **TC-ADMIN-020**: SYS_ADMIN `GET /api/teams/T02/tasks` → `200 OK`
- [ ] **TC-ADMIN-021**: SYS_ADMIN `POST /api/teams/T01/tasks` → `200 OK`
- [ ] **TC-ADMIN-022**: SYS_ADMIN `PUT /api/teams/T01/tasks/1` → `200 OK`
- [ ] **TC-ADMIN-023**: SYS_ADMIN `DELETE /api/teams/T01/tasks/1` → `200 OK`
- [ ] **TC-ADMIN-024**: SYS_ADMIN `POST /api/teams/T01/tasks/1/approve` → `200 OK`
- [ ] **TC-ADMIN-025**: SYS_ADMIN `POST /api/teams/T01/tasks/1/assign` → `200 OK`

---

## 3. Kiểm Tra Quyền USER Cơ Bản

> USER có các quyền: `self.*`, `employee.view.basic`, `team.view`, `department.view`, `task.view.self`, `role.view`

### 3.1. USER Truy Cập Profile Cá Nhân

- [ ] **TC-USER-001**: USER `GET /api/employees/profile` → `200 OK`
- [ ] **TC-USER-002**: USER `PUT /api/employees/change-password` → `200 OK`
- [ ] **TC-USER-003**: USER `POST /api/employees/upload-avatar` → `200 OK`
- [ ] **TC-USER-004**: USER `GET /api/employees/avatar-url` → `200 OK`

### 3.2. USER Xem Employee Khác (Chỉ Basic Info)

- [ ] **TC-USER-005**: USER `GET /api/employees/{other_clientId}` → `200 OK` (không có salary)
- [ ] **TC-USER-006**: USER `POST /api/employees/search` → `200 OK`

### 3.3. USER Không Được Update/Delete Employee Khác

- [ ] **TC-USER-007**: USER `PUT /api/employees/{other_clientId}` → `403 Forbidden`
- [ ] **TC-USER-008**: USER `DELETE /api/employees/{other_clientId}` → `403 Forbidden`
- [ ] **TC-USER-009**: USER `POST /api/employees` (create) → `403 Forbidden`

### 3.4. USER Xem Roles

- [ ] **TC-USER-010**: USER `GET /api/roles` → `200 OK`
- [ ] **TC-USER-011**: USER `GET /api/roles/TEAM_LEAD` → `200 OK`
- [ ] **TC-USER-012**: USER `GET /api/roles/type/TEAM` → `200 OK`

### 3.5. USER Xem Notifications Của Mình

- [ ] **TC-USER-013**: USER `GET /api/notifications` → `200 OK`
- [ ] **TC-USER-014**: USER `PUT /api/notifications/1/read` → `200 OK`
- [ ] **TC-USER-015**: USER `PUT /api/notifications/read-all` → `200 OK`

---

## 4. Kiểm Tra Scope Department

> **Quy tắc quan trọng**: User chỉ có quyền trên department mà họ thuộc về

### 4.1. DEPT_DIR Trong Cùng Department (DEPT01)

- [ ] **TC-DEPT-001**: user1 (DEPT_DIR in DEPT01) `GET /api/departments/DEPT01` → `200 OK`
- [ ] **TC-DEPT-002**: user1 (DEPT_DIR in DEPT01) `PUT /api/departments/DEPT01` → `200 OK`
- [ ] **TC-DEPT-003**: user1 (DEPT_DIR in DEPT01) `GET /api/departments/DEPT01/employees` → `200 OK`
- [ ] **TC-DEPT-004**: user1 (DEPT_DIR in DEPT01) `POST /api/departments/DEPT01/employees` → `200 OK`
- [ ] **TC-DEPT-005**: user1 (DEPT_DIR in DEPT01) `DELETE /api/departments/DEPT01/employees/{id}` → `200 OK`
- [ ] **TC-DEPT-006**: user1 (DEPT_DIR in DEPT01) `PUT /api/departments/DEPT01/employees/{id}` → `200 OK`

### 4.2. DEPT_DIR Truy Cập Department Khác (PHẢI BỊ TỪ CHỐI)

- [ ] **TC-DEPT-007**: user1 (DEPT_DIR in DEPT01) `GET /api/departments/DEPT02` → `403 Forbidden` ⚠️
- [ ] **TC-DEPT-008**: user1 (DEPT_DIR in DEPT01) `PUT /api/departments/DEPT02` → `403 Forbidden` ⚠️
- [ ] **TC-DEPT-009**: user1 (DEPT_DIR in DEPT01) `GET /api/departments/DEPT02/employees` → `403 Forbidden` ⚠️
- [ ] **TC-DEPT-010**: user1 (DEPT_DIR in DEPT01) `POST /api/departments/DEPT02/employees` → `403 Forbidden` ⚠️
- [ ] **TC-DEPT-011**: user1 (DEPT_DIR in DEPT01) `DELETE /api/departments/DEPT02/employees/{id}` → `403 Forbidden` ⚠️

### 4.3. DEPT_MGR Trong Cùng Department

- [ ] **TC-DEPT-012**: user3 (DEPT_MGR in DEPT02) `GET /api/departments/DEPT02/employees` → `200 OK`
- [ ] **TC-DEPT-013**: user3 (DEPT_MGR in DEPT02) `GET /api/departments/DEPT01/employees` → `403 Forbidden` ⚠️

### 4.4. DEPT_MEM Chỉ Có Quyền Xem

- [ ] **TC-DEPT-014**: user2 (DEPT_MEM in DEPT01) `GET /api/departments/DEPT01` → `200 OK`
- [ ] **TC-DEPT-015**: user2 (DEPT_MEM in DEPT01) `GET /api/departments/DEPT01/employees` → `200 OK`
- [ ] **TC-DEPT-016**: user2 (DEPT_MEM in DEPT01) `PUT /api/departments/DEPT01` → `403 Forbidden`
- [ ] **TC-DEPT-017**: user2 (DEPT_MEM in DEPT01) `POST /api/departments/DEPT01/employees` → `403 Forbidden`

### 4.5. User Không Thuộc Department Nào

- [ ] **TC-DEPT-018**: user4 (no dept) `GET /api/departments/DEPT01` → `403 Forbidden` ⚠️
- [ ] **TC-DEPT-019**: user4 (no dept) `GET /api/departments/DEPT01/employees` → `403 Forbidden` ⚠️

---

## 5. Kiểm Tra Scope Team

> **Quy tắc quan trọng**: User chỉ có quyền trên team mà họ thuộc về

### 5.1. TEAM_LEAD Trong Cùng Team (T01)

- [ ] **TC-TEAM-001**: user1 (TEAM_LEAD in T01) `GET /api/teams/T01` → `200 OK`
- [ ] **TC-TEAM-002**: user1 (TEAM_LEAD in T01) `GET /api/teams/T01/employees` → `200 OK`
- [ ] **TC-TEAM-003**: user1 (TEAM_LEAD in T01) `GET /api/teams/T01/tasks` → `200 OK`
- [ ] **TC-TEAM-004**: user1 (TEAM_LEAD in T01) `POST /api/teams/T01/tasks` → `200 OK`
- [ ] **TC-TEAM-005**: user1 (TEAM_LEAD in T01) `PUT /api/teams/T01/tasks/1` → `200 OK`
- [ ] **TC-TEAM-006**: user1 (TEAM_LEAD in T01) `DELETE /api/teams/T01/tasks/1` → `200 OK`
- [ ] **TC-TEAM-007**: user1 (TEAM_LEAD in T01) `POST /api/teams/T01/tasks/1/approve` → `200 OK`
- [ ] **TC-TEAM-008**: user1 (TEAM_LEAD in T01) `POST /api/teams/T01/tasks/1/assign` → `200 OK`

### 5.2. TEAM_LEAD Truy Cập Team Khác (PHẢI BỊ TỪ CHỐI)

- [ ] **TC-TEAM-009**: user1 (TEAM_LEAD in T01) `GET /api/teams/T02` → `403 Forbidden` ⚠️
- [ ] **TC-TEAM-010**: user1 (TEAM_LEAD in T01) `GET /api/teams/T02/employees` → `403 Forbidden` ⚠️
- [ ] **TC-TEAM-011**: user1 (TEAM_LEAD in T01) `GET /api/teams/T02/tasks` → `403 Forbidden` ⚠️
- [ ] **TC-TEAM-012**: user1 (TEAM_LEAD in T01) `POST /api/teams/T02/tasks` → `403 Forbidden` ⚠️
- [ ] **TC-TEAM-013**: user1 (TEAM_LEAD in T01) `PUT /api/teams/T02/tasks/1` → `403 Forbidden` ⚠️
- [ ] **TC-TEAM-014**: user1 (TEAM_LEAD in T01) `DELETE /api/teams/T02/tasks/1` → `403 Forbidden` ⚠️
- [ ] **TC-TEAM-015**: user1 (TEAM_LEAD in T01) `POST /api/teams/T02/tasks/1/approve` → `403 Forbidden` ⚠️

### 5.3. TEAM_MEM Chỉ Có Quyền Xem

- [ ] **TC-TEAM-016**: user2 (TEAM_MEM in T01) `GET /api/teams/T01` → `200 OK`
- [ ] **TC-TEAM-017**: user2 (TEAM_MEM in T01) `GET /api/teams/T01/employees` → `200 OK`
- [ ] **TC-TEAM-018**: user2 (TEAM_MEM in T01) `POST /api/teams/T01/tasks` → `403 Forbidden`
- [ ] **TC-TEAM-019**: user2 (TEAM_MEM in T01) `PUT /api/teams/T01/tasks/1` → `403 Forbidden`

### 5.4. TEAM_LEAD Không Có Quyền Quản Lý Member

- [ ] **TC-TEAM-020**: user1 (TEAM_LEAD in T01) `POST /api/teams/T01/employees` (add member) → `403 Forbidden`
- [ ] **TC-TEAM-021**: user1 (TEAM_LEAD in T01) `DELETE /api/teams/T01/employees/{id}` → `403 Forbidden`

### 5.5. DEPT_DIR Quản Lý Team Trong Department

- [ ] **TC-TEAM-022**: user1 (DEPT_DIR in DEPT01) `POST /api/teams/T01/employees` → `200 OK`
- [ ] **TC-TEAM-023**: user1 (DEPT_DIR in DEPT01) `DELETE /api/teams/T01/employees/{id}` → `200 OK`
- [ ] **TC-TEAM-024**: user1 (DEPT_DIR in DEPT01) `PUT /api/teams/T01` → `200 OK`
- [ ] **TC-TEAM-025**: user1 (DEPT_DIR in DEPT01) `DELETE /api/teams/T01` → `200 OK`
- [ ] **TC-TEAM-026**: user1 (DEPT_DIR in DEPT01) `POST /api/teams` (create) → `200 OK`

### 5.6. User Không Thuộc Team Nào

- [ ] **TC-TEAM-027**: user4 (no team) `GET /api/teams/T01` → `403 Forbidden` ⚠️
- [ ] **TC-TEAM-028**: user4 (no team) `GET /api/teams/T01/tasks` → `403 Forbidden` ⚠️

---

## 6. Kiểm Tra Task (Team Scoped)

### 6.1. Task Operations Trong Team

- [ ] **TC-TASK-001**: TEAM_LEAD tạo task trong team của mình → `200 OK`
- [ ] **TC-TASK-002**: TEAM_LEAD tạo task trong team khác → `403 Forbidden`
- [ ] **TC-TASK-003**: TEAM_MEM xem tasks trong team → `200 OK`
- [ ] **TC-TASK-004**: TEAM_MEM tạo task → `403 Forbidden`
- [ ] **TC-TASK-005**: TEAM_MEM update task → `403 Forbidden`
- [ ] **TC-TASK-006**: TEAM_MEM approve task → `403 Forbidden`

### 6.2. Task Self Operations

- [ ] **TC-TASK-007**: User xem task của chính mình → `200 OK`
- [ ] **TC-TASK-008**: User update task của chính mình → `200 OK`

---

## 7. Kiểm Tra Cross-Scope Access (Rất Quan Trọng)

> Đây là phần quan trọng nhất - đảm bảo user không thể truy cập scope họ không thuộc về

### 7.1. Department Cross-Access

- [ ] **TC-CROSS-001**: DEPT_DIR của DEPT01 truy cập `/departments/DEPT02` → `403 Forbidden` ⚠️
- [ ] **TC-CROSS-002**: DEPT_DIR của DEPT01 thêm member vào DEPT02 → `403 Forbidden` ⚠️
- [ ] **TC-CROSS-003**: DEPT_MGR của DEPT02 xem employees của DEPT01 → `403 Forbidden` ⚠️
- [ ] **TC-CROSS-004**: User có global `department.view` nhưng không thuộc DEPT01 → `403 Forbidden` ⚠️

### 7.2. Team Cross-Access

- [ ] **TC-CROSS-005**: TEAM_LEAD của T01 truy cập `/teams/T02/tasks` → `403 Forbidden` ⚠️
- [ ] **TC-CROSS-006**: TEAM_LEAD của T01 tạo task trong T02 → `403 Forbidden` ⚠️
- [ ] **TC-CROSS-007**: TEAM_MEM của T01 xem tasks của T02 → `403 Forbidden` ⚠️
- [ ] **TC-CROSS-008**: User có global `team.view` nhưng không thuộc T01 → `403 Forbidden` ⚠️

### 7.3. Department-Team Relationship

- [ ] **TC-CROSS-009**: DEPT_DIR của DEPT01 quản lý team T01 (thuộc DEPT01) → `200 OK`
- [ ] **TC-CROSS-010**: DEPT_DIR của DEPT01 quản lý team T02 (thuộc DEPT02) → `403 Forbidden` ⚠️
- [ ] **TC-CROSS-011**: DEPT_MGR của DEPT01 thêm member vào team T01 → `200 OK`
- [ ] **TC-CROSS-012**: DEPT_MGR của DEPT01 thêm member vào team T02 → `403 Forbidden` ⚠️

---

## 8. Kiểm Tra Role Hierarchy

### 8.1. DEPT_DIR > DEPT_MGR > DEPT_MEM

- [ ] **TC-ROLE-001**: DEPT_DIR có tất cả quyền của DEPT_MGR → Verify
- [ ] **TC-ROLE-002**: DEPT_DIR có tất cả quyền của DEPT_MEM → Verify
- [ ] **TC-ROLE-003**: DEPT_MGR không có quyền delete department → `403 Forbidden`
- [ ] **TC-ROLE-004**: DEPT_MEM không có quyền thêm member → `403 Forbidden`

### 8.2. TEAM_LEAD > TEAM_MEM

- [ ] **TC-ROLE-005**: TEAM_LEAD có tất cả quyền của TEAM_MEM → Verify
- [ ] **TC-ROLE-006**: TEAM_MEM không có quyền create/update/delete task → `403 Forbidden`

### 8.3. Multiple Roles

- [ ] **TC-ROLE-007**: User vừa là DEPT_DIR của DEPT01 vừa là DEPT_MEM của DEPT02 → Có quyền đầy đủ ở DEPT01, quyền hạn chế ở DEPT02
- [ ] **TC-ROLE-008**: User thuộc nhiều teams → Có quyền tương ứng role ở mỗi team

---

## 9. Kiểm Tra Public Endpoints

> Các endpoint này không cần authentication

- [ ] **TC-PUBLIC-001**: `GET /api/actuator/health` không có token → `200 OK`
- [ ] **TC-PUBLIC-002**: `GET /api/actuator/info` không có token → `200 OK`
- [ ] **TC-PUBLIC-003**: `OPTIONS /api/employees` (CORS preflight) → `200 OK`
- [ ] **TC-PUBLIC-004**: `GET /api/health` không có token → `200 OK`

---

## 10. Kiểm Tra Edge Cases

### 10.1. Endpoint Không Có Permission Mapping

- [ ] **TC-EDGE-001**: Truy cập endpoint không có trong `api_permissions` table → `403 Forbidden` (DENY by default)

### 10.2. Invalid Path Variables

- [ ] **TC-EDGE-002**: `GET /api/departments/INVALID_CODE` → `403 Forbidden` hoặc `404 Not Found`
- [ ] **TC-EDGE-003**: `GET /api/teams/INVALID_CODE/tasks` → `403 Forbidden` hoặc `404 Not Found`

### 10.3. SQL Injection Attempts

- [ ] **TC-EDGE-004**: `GET /api/departments/DEPT01' OR '1'='1` → `403 Forbidden` hoặc `400 Bad Request`
- [ ] **TC-EDGE-005**: `GET /api/teams/T01; DROP TABLE tasks;--` → `403 Forbidden` hoặc `400 Bad Request`

### 10.4. Cache Behavior

- [ ] **TC-EDGE-006**: Update permission mapping trong DB → Cache tự động refresh sau TTL
- [ ] **TC-EDGE-007**: Thêm permission mới → Cache được update

---

## Tổng Kết Test Results

| Category | Total | Pass | Fail | Pending |
|----------|-------|------|------|---------|
| Authentication | 9 | - | - | - |
| SYS_ADMIN | 25 | - | - | - |
| USER Basic | 15 | - | - | - |
| Department Scope | 19 | - | - | - |
| Team Scope | 28 | - | - | - |
| Task | 8 | - | - | - |
| Cross-Scope | 12 | - | - | - |
| Role Hierarchy | 8 | - | - | - |
| Public Endpoints | 4 | - | - | - |
| Edge Cases | 7 | - | - | - |
| **TOTAL** | **135** | - | - | - |

---

## Ghi Chú Lỗi

Ghi lại các test case FAIL tại đây:

| Test Case ID | Mô Tả Lỗi | Actual Result | Expected Result | Ghi Chú |
|--------------|-----------|---------------|-----------------|---------|
| | | | | |
| | | | | |
| | | | | |

---

## Testing với cURL

### Lấy Token

```bash
# Login để lấy token (tùy vào cách authentication của hệ thống)
TOKEN="your_jwt_token_here"
```

### Test Authenticated Endpoint

```bash
curl -X GET "http://localhost:8080/api/employees/profile" \
  -H "Authorization: Bearer $TOKEN"
```

### Test Create Resource

```bash
curl -X POST "http://localhost:8080/api/teams" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"teamCode":"T99","teamName":"Test Team","departmentCode":"DEPT01"}'
```

### Test Scope-Restricted Endpoint

```bash
# User thuộc DEPT01 truy cập DEPT02 - phải bị từ chối
curl -X GET "http://localhost:8080/api/departments/DEPT02/employees" \
  -H "Authorization: Bearer $DEPT01_USER_TOKEN"
# Expected: 403 Forbidden
```

---

## Checklist Hoàn Thành

- [ ] Tất cả Authentication tests passed
- [ ] Tất cả SYS_ADMIN tests passed
- [ ] Tất cả USER Basic tests passed
- [ ] Tất cả Department Scope tests passed
- [ ] Tất cả Team Scope tests passed
- [ ] Tất cả Task tests passed
- [ ] Tất cả Cross-Scope tests passed (QUAN TRỌNG)
- [ ] Tất cả Role Hierarchy tests passed
- [ ] Tất cả Public Endpoints tests passed
- [ ] Tất cả Edge Cases tests passed
- [ ] Ghi chú đầy đủ các lỗi phát hiện được
- [ ] Tất cả lỗi đã được fix và re-test

**Người Test**: _________________  
**Ngày Test**: _________________  
**Kết Quả**: ⬜ PASS / ⬜ FAIL
