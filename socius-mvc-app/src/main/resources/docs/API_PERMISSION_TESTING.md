# API Permission Testing Guide

This document lists all API endpoints that require permission checks, with test scenarios for both successful access and access denied cases.

## Overview

The RBAC (Role-Based Access Control) system uses database-driven permission mappings. All protected endpoints must have explicit permission mappings in the `api_permissions` table.

### Authorization Flow

1. Request arrives at RbacAuthorizationManager
2. If user is not authenticated → **DENY**
3. If user has `system.full` permission → **ALLOW** (SYS_ADMIN)
4. Lookup permission from database by HTTP method + URL pattern
5. If no mapping found → **DENY** (secure by default)
6. Check if user has required permission (global or scoped) → **ALLOW/DENY**

---

## Test Scenarios

### Legend
- ✅ = Access Granted
- ❌ = Access Denied

---

## 1. Employee Endpoints

### GET `/employees/profile` (Own Profile)
| User Role | Expected Result | Reason |
|-----------|-----------------|--------|
| SYS_ADMIN | ✅ | Has `system.full` permission |
| USER | ✅ | Has `self.profile.view` permission |
| Unauthenticated | ❌ | Not authenticated |

### PUT `/employees/change-password` (Change Own Password)
| User Role | Expected Result | Reason |
|-----------|-----------------|--------|
| SYS_ADMIN | ✅ | Has `system.full` permission |
| USER | ✅ | Has `self.password.update` permission |
| Unauthenticated | ❌ | Not authenticated |

### GET `/employees/{clientId}` (View Employee)
| User Role | Expected Result | Reason |
|-----------|-----------------|--------|
| SYS_ADMIN | ✅ | Has `system.full` permission |
| USER | ✅ | Has `employee.view.basic` permission |
| Unauthenticated | ❌ | Not authenticated |

### PUT `/employees/{clientId}` (Update Employee)
| User Role | Expected Result | Reason |
|-----------|-----------------|--------|
| SYS_ADMIN | ✅ | Has `system.full` permission |
| DEPT_DIR | ✅ | Has `employee.profile.update` permission |
| USER | ❌ | Does not have `employee.profile.update` permission |
| Unauthenticated | ❌ | Not authenticated |

---

## 2. Team Endpoints

### GET `/teams/{teamCode}` (View Team)
| User Role | Expected Result | Reason |
|-----------|-----------------|--------|
| SYS_ADMIN | ✅ | Has `system.full` permission |
| USER | ✅ | Has `team.view` permission |
| Unauthenticated | ❌ | Not authenticated |

### POST `/teams` (Create Team)
| User Role | Expected Result | Reason |
|-----------|-----------------|--------|
| SYS_ADMIN | ✅ | Has `system.full` permission |
| DEPT_DIR | ✅ | Has `team.create` permission |
| USER | ❌ | Does not have `team.create` permission |
| Unauthenticated | ❌ | Not authenticated |

### PUT `/teams/{teamCode}` (Update Team)
| User Role | Expected Result | Reason |
|-----------|-----------------|--------|
| SYS_ADMIN | ✅ | Has `system.full` permission |
| DEPT_DIR | ✅ | Has `team.update` permission |
| USER | ❌ | Does not have `team.update` permission |
| Unauthenticated | ❌ | Not authenticated |

### DELETE `/teams/{teamCode}` (Delete Team)
| User Role | Expected Result | Reason |
|-----------|-----------------|--------|
| SYS_ADMIN | ✅ | Has `system.full` permission |
| DEPT_DIR | ✅ | Has `team.delete` permission |
| USER | ❌ | Does not have `team.delete` permission |
| Unauthenticated | ❌ | Not authenticated |

---

## 3. Department Endpoints

### GET `/departments/{departmentCode}` (View Department)
| User Role | Expected Result | Reason |
|-----------|-----------------|--------|
| SYS_ADMIN | ✅ | Has `system.full` permission |
| USER | ✅ | Has `department.view` permission |
| Unauthenticated | ❌ | Not authenticated |

### POST `/departments` (Create Department)
| User Role | Expected Result | Reason |
|-----------|-----------------|--------|
| SYS_ADMIN | ✅ | Has `system.full` permission |
| USER | ❌ | Does not have `department.create` permission |
| Unauthenticated | ❌ | Not authenticated |

### PUT `/departments/{departmentCode}` (Update Department)
| User Role | Expected Result | Reason |
|-----------|-----------------|--------|
| SYS_ADMIN | ✅ | Has `system.full` permission |
| DEPT_DIR (same dept) | ✅ | Has `DEPARTMENT:DEPT01:department.update` scoped permission |
| DEPT_DIR (other dept) | ❌ | Does not have permission for this department |
| USER | ❌ | Does not have `department.update` permission |
| Unauthenticated | ❌ | Not authenticated |

---

## 4. Team Member Management (Scoped)

### POST `/teams/{teamCode}/employees` (Add Member to Team)
| User Role | Scope | Expected Result | Reason |
|-----------|-------|-----------------|--------|
| SYS_ADMIN | - | ✅ | Has `system.full` permission |
| TEAM_LEAD | Same Team | ❌ | TEAM_LEAD does not have `team.member.add` |
| DEPT_DIR | Team in Dept | ✅ | Has `team.member.add` permission |
| DEPT_MGR | Team in Dept | ✅ | Has `team.member.add` permission |
| USER | Any | ❌ | Does not have `team.member.add` permission |
| Unauthenticated | - | ❌ | Not authenticated |

### DELETE `/teams/{teamCode}/employees/{employeeId}` (Remove Member from Team)
| User Role | Scope | Expected Result | Reason |
|-----------|-------|-----------------|--------|
| SYS_ADMIN | - | ✅ | Has `system.full` permission |
| DEPT_DIR | Team in Dept | ✅ | Has `team.member.remove` permission |
| DEPT_MGR | Team in Dept | ✅ | Has `team.member.remove` permission |
| USER | Any | ❌ | Does not have `team.member.remove` permission |
| Unauthenticated | - | ❌ | Not authenticated |

### GET `/teams/{teamCode}/employees` (View Team Members)
| User Role | Expected Result | Reason |
|-----------|-----------------|--------|
| SYS_ADMIN | ✅ | Has `system.full` permission |
| USER | ✅ | Has `team.view` permission |
| Unauthenticated | ❌ | Not authenticated |

---

## 5. Department Member Management (Scoped)

### POST `/departments/{departmentCode}/employees` (Add Member to Department)
| User Role | Scope | Expected Result | Reason |
|-----------|-------|-----------------|--------|
| SYS_ADMIN | - | ✅ | Has `system.full` permission |
| DEPT_DIR | Same Dept | ✅ | Has `DEPARTMENT:DEPT01:department.member.add` |
| DEPT_DIR | Other Dept | ❌ | Does not have permission for other department |
| USER | Any | ❌ | Does not have `department.member.add` permission |
| Unauthenticated | - | ❌ | Not authenticated |

### DELETE `/departments/{departmentCode}/employees/{employeeId}` (Remove Member)
| User Role | Scope | Expected Result | Reason |
|-----------|-------|-----------------|--------|
| SYS_ADMIN | - | ✅ | Has `system.full` permission |
| DEPT_DIR | Same Dept | ✅ | Has `DEPARTMENT:DEPT01:department.member.remove` |
| DEPT_DIR | Other Dept | ❌ | Does not have permission for other department |
| USER | Any | ❌ | Does not have `department.member.remove` permission |
| Unauthenticated | - | ❌ | Not authenticated |

---

## 6. Task Endpoints (Team Scoped)

### GET `/teams/{teamCode}/tasks` (View Team Tasks)
| User Role | Scope | Expected Result | Reason |
|-----------|-------|-----------------|--------|
| SYS_ADMIN | - | ✅ | Has `system.full` permission |
| TEAM_LEAD | Same Team | ✅ | Has `TEAM:T01:task.view.team` |
| TEAM_LEAD | Other Team | ❌ | Does not have permission for other team |
| DEPT_DIR | Any Team in Dept | ✅ | Has `task.view.team` permission |
| USER | Any | ❌ | Does not have `task.view.team` permission |
| Unauthenticated | - | ❌ | Not authenticated |

### POST `/teams/{teamCode}/tasks` (Create Task)
| User Role | Scope | Expected Result | Reason |
|-----------|-------|-----------------|--------|
| SYS_ADMIN | - | ✅ | Has `system.full` permission |
| TEAM_LEAD | Same Team | ✅ | Has `TEAM:T01:task.create` |
| TEAM_LEAD | Other Team | ❌ | Does not have permission for other team |
| DEPT_DIR | Any Team in Dept | ✅ | Has `task.create` permission |
| USER | Any | ❌ | Does not have `task.create` permission |
| Unauthenticated | - | ❌ | Not authenticated |

### PUT `/teams/{teamCode}/tasks/{taskId}` (Update Task)
| User Role | Scope | Expected Result | Reason |
|-----------|-------|-----------------|--------|
| SYS_ADMIN | - | ✅ | Has `system.full` permission |
| TEAM_LEAD | Same Team | ✅ | Has `TEAM:T01:task.update` |
| TEAM_LEAD | Other Team | ❌ | Does not have permission for other team |
| DEPT_DIR | Any Team in Dept | ✅ | Has `task.update` permission |
| USER | Any | ❌ | Does not have `task.update` permission |
| Unauthenticated | - | ❌ | Not authenticated |

### DELETE `/teams/{teamCode}/tasks/{taskId}` (Delete Task)
| User Role | Scope | Expected Result | Reason |
|-----------|-------|-----------------|--------|
| SYS_ADMIN | - | ✅ | Has `system.full` permission |
| TEAM_LEAD | Same Team | ✅ | Has `TEAM:T01:task.delete` |
| TEAM_LEAD | Other Team | ❌ | Does not have permission for other team |
| DEPT_DIR | Any Team in Dept | ✅ | Has `task.delete` permission |
| USER | Any | ❌ | Does not have `task.delete` permission |
| Unauthenticated | - | ❌ | Not authenticated |

### POST `/teams/{teamCode}/tasks/{taskId}/approve` (Approve Task)
| User Role | Scope | Expected Result | Reason |
|-----------|-------|-----------------|--------|
| SYS_ADMIN | - | ✅ | Has `system.full` permission |
| TEAM_LEAD | Same Team | ✅ | Has `TEAM:T01:task.approve` |
| TEAM_LEAD | Other Team | ❌ | Does not have permission for other team |
| DEPT_DIR | Any Team in Dept | ✅ | Has `task.approve` permission |
| USER | Any | ❌ | Does not have `task.approve` permission |
| Unauthenticated | - | ❌ | Not authenticated |

### POST `/teams/{teamCode}/tasks/{taskId}/assign` (Assign Task)
| User Role | Scope | Expected Result | Reason |
|-----------|-------|-----------------|--------|
| SYS_ADMIN | - | ✅ | Has `system.full` permission |
| TEAM_LEAD | Same Team | ✅ | Has `TEAM:T01:task.assign` |
| TEAM_LEAD | Other Team | ❌ | Does not have permission for other team |
| DEPT_DIR | Any Team in Dept | ✅ | Has `task.assign` permission |
| USER | Any | ❌ | Does not have `task.assign` permission |
| Unauthenticated | - | ❌ | Not authenticated |

---

## 7. Role Endpoints

### GET `/roles` (List Roles)
| User Role | Expected Result | Reason |
|-----------|-----------------|--------|
| SYS_ADMIN | ✅ | Has `system.full` permission |
| USER | ✅ | Has `role.view` permission |
| Unauthenticated | ❌ | Not authenticated |

### GET `/roles/{roleCode}` (Get Role)
| User Role | Expected Result | Reason |
|-----------|-----------------|--------|
| SYS_ADMIN | ✅ | Has `system.full` permission |
| USER | ✅ | Has `role.view` permission |
| Unauthenticated | ❌ | Not authenticated |

### GET `/roles/type/{roleType}` (Get Roles by Type)
| User Role | Expected Result | Reason |
|-----------|-----------------|--------|
| SYS_ADMIN | ✅ | Has `system.full` permission |
| USER | ✅ | Has `role.view` permission |
| Unauthenticated | ❌ | Not authenticated |

---

## 8. Notification Endpoints

### GET `/notifications` (List Notifications)
| User Role | Expected Result | Reason |
|-----------|-----------------|--------|
| SYS_ADMIN | ✅ | Has `system.full` permission |
| USER | ✅ | Has `self.profile.view` permission |
| Unauthenticated | ❌ | Not authenticated |

### PUT `/notifications/{notificationId}/read` (Mark as Read)
| User Role | Expected Result | Reason |
|-----------|-----------------|--------|
| SYS_ADMIN | ✅ | Has `system.full` permission |
| USER | ✅ | Has `self.profile.update` permission |
| Unauthenticated | ❌ | Not authenticated |

### PUT `/notifications/read-all` (Mark All as Read)
| User Role | Expected Result | Reason |
|-----------|-----------------|--------|
| SYS_ADMIN | ✅ | Has `system.full` permission |
| USER | ✅ | Has `self.profile.update` permission |
| Unauthenticated | ❌ | Not authenticated |

---

## Public Endpoints (No Authentication Required)

These endpoints are configured as `permitAll()` in SecurityConfig:
- `GET /health`
- `GET /ping`
- `GET /favicon.ico`
- `GET /robots.txt`
- `GET /error`
- `GET /actuator/health`
- `GET /actuator/health/**`
- `GET /actuator/info`
- `OPTIONS /**` (CORS preflight)

---

## Role Permission Summary

| Role | Scope | Permissions |
|------|-------|-------------|
| SYS_ADMIN | Global | `system.full` (access all) |
| USER | Global | `self.*`, `employee.view.basic`, `team.view`, `department.view`, `task.view.self`, `task.update.self`, `role.view` |
| DEPT_DIR | Department | All USER permissions + `employee.*`, `team.*`, `department.*`, `task.*` |
| DEPT_MGR | Department | All USER permissions + `employee.view.salary`, `team.member.*`, `task.*` |
| TEAM_LEAD | Team | All USER permissions + `task.*` (scoped to team) |
| DEPT_MEM | Department | Same as USER |
| TEAM_MEM | Team | Same as USER |

---

## Testing with cURL

### Example: Authenticated Request
```bash
curl -X GET "http://localhost:8080/api/employees/profile" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

### Example: Create Team (DEPT_DIR only)
```bash
curl -X POST "http://localhost:8080/api/teams" \
  -H "Authorization: Bearer <DEPT_DIR_JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"teamCode":"T01","teamName":"Team Alpha","departmentCode":"DEPT01"}'
```

### Example: Access Denied (USER trying to create team)
```bash
curl -X POST "http://localhost:8080/api/teams" \
  -H "Authorization: Bearer <USER_JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"teamCode":"T01","teamName":"Team Alpha","departmentCode":"DEPT01"}'
# Expected: 403 Forbidden
```
