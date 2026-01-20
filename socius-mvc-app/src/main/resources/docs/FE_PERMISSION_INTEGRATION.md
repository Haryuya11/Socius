# Frontend Permission Integration Guide

Tài liệu này mô tả cách Frontend tích hợp với hệ thống phân quyền RBAC.

---

## Mục Lục

1. [Tổng Quan Hệ Thống Phân Quyền](#1-tổng-quan-hệ-thống-phân-quyền)
2. [Danh Sách Roles](#2-danh-sách-roles)
3. [Danh Sách Permissions](#3-danh-sách-permissions)
4. [User Context & Authorities](#4-user-context--authorities)
5. [API Endpoints & Required Permissions](#5-api-endpoints--required-permissions)
6. [FE Permission Check Logic](#6-fe-permission-check-logic)
7. [UI Component Mapping](#7-ui-component-mapping)
8. [Error Handling](#8-error-handling)

---

## 1. Tổng Quan Hệ Thống Phân Quyền

### 1.1. Kiến Trúc

```
User → có 1 System Role (SYS_ADMIN | USER)
     → thuộc nhiều Department (mỗi dept có 1 Dept Role: DEPT_DIR | DEPT_MGR | DEPT_MEM)
     → thuộc nhiều Team (mỗi team có 1 Team Role: TEAM_LEAD | TEAM_MEM)

Mỗi Role → có nhiều Permissions
```

### 1.2. Scope-Based Authorization

**Quan trọng**: User chỉ có quyền trong phạm vi (scope) họ thuộc về.

| Scope Type | Ví dụ | Mô tả |
|------------|-------|-------|
| Global | `employee.view.basic` | Có quyền với tất cả resources |
| Department | `DEPARTMENT:DEPT01:department.view` | Chỉ có quyền trong DEPT01 |
| Team | `TEAM:T01:task.create` | Chỉ có quyền trong Team T01 |

### 1.3. Authorization Format

```
# Global permission
{permission_code}

# Scoped permission  
{SCOPE_TYPE}:{SCOPE_CODE}:{permission_code}

# Ví dụ
employee.view.basic              # Global - xem tất cả employees
DEPARTMENT:DEPT01:department.view   # Scoped - chỉ xem DEPT01
TEAM:T01:task.create               # Scoped - chỉ tạo task trong T01
```

---

## 2. Danh Sách Roles

### 2.1. System Roles

| Role Code | Role Name | Mô tả |
|-----------|-----------|-------|
| `SYS_ADMIN` | System Administrator | Full access - bypass tất cả permission checks |
| `USER` | User | Basic user - cần thêm dept/team roles |

### 2.2. Department Roles

| Role Code | Role Name | Mô tả | Quyền Chính |
|-----------|-----------|-------|-------------|
| `DEPT_DIR` | Department Director | Quản lý toàn bộ department | CRUD dept, team, employee, task |
| `DEPT_MGR` | Department Manager | Hỗ trợ director | Quản lý team members, tasks |
| `DEPT_MEM` | Department Member | Thành viên thường | Giống USER trong scope dept |

### 2.3. Team Roles

| Role Code | Role Name | Mô tả | Quyền Chính |
|-----------|-----------|-------|-------------|
| `TEAM_LEAD` | Team Leader | Quản lý team | CRUD tasks, assign, approve |
| `TEAM_MEM` | Team Member | Thành viên thường | Xem tasks trong team |

---

## 3. Danh Sách Permissions

### 3.1. Self Permissions (Quản lý bản thân)

| Permission Code | Mô tả | Roles có quyền |
|-----------------|-------|----------------|
| `self.profile.view` | Xem profile cá nhân | ALL |
| `self.profile.update` | Cập nhật profile cá nhân | ALL |
| `self.password.update` | Đổi mật khẩu | ALL |
| `self.avatar.upload` | Upload avatar | ALL |

### 3.2. Employee Permissions

| Permission Code | Mô tả | Roles có quyền |
|-----------------|-------|----------------|
| `employee.view.basic` | Xem thông tin employee (không salary) | ALL |
| `employee.view.salary` | Xem salary | DEPT_DIR, DEPT_MGR |
| `employee.profile.update` | Update employee info | DEPT_DIR |
| `employee.salary.update` | Update salary | DEPT_DIR |

### 3.3. Department Permissions

| Permission Code | Mô tả | Roles có quyền |
|-----------------|-------|----------------|
| `department.view` | Xem department info | ALL |
| `department.create` | Tạo department | SYS_ADMIN |
| `department.update` | Update department | DEPT_DIR (scoped) |
| `department.delete` | Xóa department | SYS_ADMIN |
| `department.member.add` | Thêm member vào dept | DEPT_DIR (scoped) |
| `department.member.remove` | Xóa member khỏi dept | DEPT_DIR (scoped) |
| `department.member.role.update` | Đổi role của member | DEPT_DIR (scoped) |

### 3.4. Team Permissions

| Permission Code | Mô tả | Roles có quyền |
|-----------------|-------|----------------|
| `team.view` | Xem team info | ALL |
| `team.create` | Tạo team | DEPT_DIR (scoped) |
| `team.update` | Update team | DEPT_DIR (scoped) |
| `team.delete` | Xóa team | DEPT_DIR (scoped) |
| `team.member.add` | Thêm member vào team | DEPT_DIR, DEPT_MGR (scoped) |
| `team.member.remove` | Xóa member khỏi team | DEPT_DIR, DEPT_MGR (scoped) |
| `team.member.role.update` | Đổi role của member | DEPT_DIR, DEPT_MGR (scoped) |

### 3.5. Task Permissions

| Permission Code | Mô tả | Roles có quyền |
|-----------------|-------|----------------|
| `task.view.self` | Xem task của mình | ALL |
| `task.update.self` | Update task của mình | ALL |
| `task.view.team` | Xem tasks trong team | TEAM_LEAD, DEPT_DIR, DEPT_MGR (scoped) |
| `task.view.department` | Xem tasks trong dept | DEPT_DIR, DEPT_MGR (scoped) |
| `task.create` | Tạo task | TEAM_LEAD, DEPT_DIR, DEPT_MGR (scoped) |
| `task.update` | Update task | TEAM_LEAD, DEPT_DIR, DEPT_MGR (scoped) |
| `task.delete` | Xóa task | TEAM_LEAD, DEPT_DIR, DEPT_MGR (scoped) |
| `task.assign` | Giao task cho người khác | TEAM_LEAD, DEPT_DIR, DEPT_MGR (scoped) |
| `task.approve` | Approve/reject task | TEAM_LEAD, DEPT_DIR, DEPT_MGR (scoped) |

### 3.6. Message Permissions (Nhắn tin)

| Permission Code | Mô tả | Roles có quyền |
|-----------------|-------|----------------|
| `message.view` | Xem tin nhắn trong cuộc hội thoại | ALL |
| `message.send` | Gửi tin nhắn mới | ALL |
| `message.update` | Sửa tin nhắn của mình | ALL |
| `message.delete` | Xóa tin nhắn của mình | ALL |
| `message.reaction` | Thêm/xóa reaction | ALL |

### 3.7. Conversation Permissions (Cuộc hội thoại)

| Permission Code | Mô tả | Roles có quyền |
|-----------------|-------|----------------|
| `conversation.view` | Xem cuộc hội thoại | ALL |
| `conversation.create` | Tạo cuộc hội thoại (trực tiếp hoặc nhóm) | ALL |
| `conversation.update` | Cập nhật settings, rời nhóm | ALL |
| `conversation.delete` | Xóa cuộc hội thoại | ALL |
| `conversation.participant.manage` | Thêm/xóa thành viên | ALL |
| `conversation.file.manage` | Upload avatar, đính kèm file | ALL |

### 3.8. System Permissions

| Permission Code | Mô tả | Roles có quyền |
|-----------------|-------|----------------|
| `system.full` | Full access - bypass all checks | SYS_ADMIN |
| `role.view` | Xem danh sách roles | ALL |

---

## 4. User Context & Authorities

### 4.1. User Context Response

Khi login hoặc gọi API `/employees/profile`, BE trả về:

```json
{
  "clientId": "abc123",
  "userId": "user@company.com",
  "firstName": "Nguyen",
  "lastName": "Van A",
  "systemRole": "USER",
  "authorities": [
    "self.profile.view",
    "self.profile.update",
    "self.password.update",
    "self.avatar.upload",
    "employee.view.basic",
    "department.view",
    "team.view",
    "role.view",
    "task.view.self",
    "task.update.self",
    "DEPARTMENT:DEPT01:department.view",
    "DEPARTMENT:DEPT01:department.member.add",
    "DEPARTMENT:DEPT01:department.member.remove",
    "DEPARTMENT:DEPT01:employee.view.salary",
    "TEAM:T01:task.view.team",
    "TEAM:T01:task.create",
    "TEAM:T01:task.update",
    "TEAM:T01:task.delete",
    "TEAM:T01:task.assign",
    "TEAM:T01:task.approve"
  ],
  "departments": [
    {
      "departmentCode": "DEPT01",
      "departmentName": "IT Department",
      "roleCode": "DEPT_DIR",
      "isPrimary": true
    }
  ],
  "teams": [
    {
      "teamCode": "T01",
      "teamName": "Development Team",
      "roleCode": "TEAM_LEAD",
      "isLeader": true
    }
  ]
}
```

### 4.2. Authority Format

```
# FE cần parse authorities để biết user có quyền gì

# Global permissions - không có scope
"employee.view.basic"
"department.view"
"team.view"

# Scoped permissions - có format SCOPE:CODE:permission
"DEPARTMENT:DEPT01:department.view"
"TEAM:T01:task.create"
```

---

## 5. API Endpoints & Required Permissions

### 5.1. Employee APIs

| Method | Endpoint | Permission | Scope |
|--------|----------|------------|-------|
| `GET` | `/employees/profile` | `self.profile.view` | - |
| `PUT` | `/employees/change-password` | `self.password.update` | - |
| `POST` | `/employees/upload-avatar` | `self.avatar.upload` | - |
| `GET` | `/employees/avatar-url` | `self.profile.view` | - |
| `GET` | `/employees/{clientId}` | `employee.view.basic` | - |
| `PUT` | `/employees/{clientId}` | `employee.profile.update` | - |
| `DELETE` | `/employees/{clientId}` | `employee.profile.update` | - |
| `POST` | `/employees` | `employee.profile.update` | - |
| `POST` | `/employees/search` | `employee.view.basic` | - |

### 5.2. Department APIs

| Method | Endpoint | Permission | Scope |
|--------|----------|------------|-------|
| `GET` | `/departments/{departmentCode}` | `department.view` | Department |
| `POST` | `/departments` | `department.create` | SYS_ADMIN only |
| `PUT` | `/departments/{departmentCode}` | `department.update` | Department |
| `DELETE` | `/departments/{departmentCode}` | `department.delete` | SYS_ADMIN only |
| `POST` | `/departments/search` | `department.view` | Department |
| `GET` | `/departments/{deptCode}/employees` | `department.view` | Department |
| `POST` | `/departments/{deptCode}/employees` | `department.member.add` | Department |
| `DELETE` | `/departments/{deptCode}/employees/{empId}` | `department.member.remove` | Department |
| `PUT` | `/departments/{deptCode}/employees/{empId}` | `department.member.role.update` | Department |

### 5.3. Team APIs

| Method | Endpoint | Permission | Scope |
|--------|----------|------------|-------|
| `GET` | `/teams/{teamCode}` | `team.view` | Team |
| `POST` | `/teams` | `team.create` | Department |
| `PUT` | `/teams/{teamCode}` | `team.update` | Department |
| `DELETE` | `/teams/{teamCode}` | `team.delete` | Department |
| `POST` | `/teams/search` | `team.view` | Team |
| `GET` | `/teams/{teamCode}/employees` | `team.view` | Team |
| `POST` | `/teams/{teamCode}/employees` | `team.member.add` | Team |
| `DELETE` | `/teams/{teamCode}/employees/{empId}` | `team.member.remove` | Team |
| `PUT` | `/teams/{teamCode}/employees/{empId}` | `team.member.role.update` | Team |

### 5.4. Task APIs

| Method | Endpoint | Permission | Scope |
|--------|----------|------------|-------|
| `GET` | `/teams/{teamCode}/tasks` | `task.view.team` | Team |
| `GET` | `/teams/{teamCode}/tasks/{taskId}` | `task.view.team` | Team |
| `POST` | `/teams/{teamCode}/tasks` | `task.create` | Team |
| `PUT` | `/teams/{teamCode}/tasks/{taskId}` | `task.update` | Team |
| `DELETE` | `/teams/{teamCode}/tasks/{taskId}` | `task.delete` | Team |
| `POST` | `/teams/{teamCode}/tasks/{taskId}/approve` | `task.approve` | Team |
| `POST` | `/teams/{teamCode}/tasks/{taskId}/assign` | `task.assign` | Team |

### 5.5. Role APIs

| Method | Endpoint | Permission | Scope |
|--------|----------|------------|-------|
| `GET` | `/roles` | `role.view` | - |
| `GET` | `/roles/{roleCode}` | `role.view` | - |
| `GET` | `/roles/type/{roleType}` | `role.view` | - |

### 5.6. Notification APIs

| Method | Endpoint | Permission | Scope |
|--------|----------|------------|-------|
| `GET` | `/notifications` | `self.profile.view` | - |
| `PUT` | `/notifications/{id}/read` | `self.profile.update` | - |
| `PUT` | `/notifications/read-all` | `self.profile.update` | - |

### 5.7. Message APIs (Nhắn tin)

| Method | Endpoint | Permission | Scope |
|--------|----------|------------|-------|
| `POST` | `/messages` | `message.send` | - |
| `GET` | `/messages/{messageId}` | `message.view` | - |
| `PUT` | `/messages/{messageId}` | `message.update` | - |
| `DELETE` | `/messages/{messageId}` | `message.delete` | - |
| `POST` | `/messages/reactions` | `message.reaction` | - |
| `DELETE` | `/messages/reactions` | `message.reaction` | - |
| `GET` | `/messages/{messageId}/reactions` | `message.view` | - |

### 5.8. Conversation APIs (Cuộc hội thoại)

| Method | Endpoint | Permission | Scope |
|--------|----------|------------|-------|
| `POST` | `/conversations/direct/{targetEmployeeId}` | `conversation.create` | - |
| `POST` | `/conversations/group` | `conversation.create` | - |
| `GET` | `/conversations/{conversationId}` | `conversation.view` | - |
| `PUT` | `/conversations/{conversationId}` | `conversation.update` | - |
| `DELETE` | `/conversations/{conversationId}` | `conversation.delete` | - |
| `GET` | `/conversations` | `conversation.view` | - |
| `GET` | `/conversations/{conversationId}/participants` | `conversation.view` | - |
| `POST` | `/conversations/{conversationId}/participants` | `conversation.participant.manage` | - |
| `DELETE` | `/conversations/{conversationId}/participants` | `conversation.participant.manage` | - |
| `POST` | `/conversations/{conversationId}/leave` | `conversation.update` | - |
| `PUT` | `/conversations/{conversationId}/settings` | `conversation.update` | - |
| `PUT` | `/conversations/{conversationId}/read/{messageId}` | `conversation.update` | - |
| `GET` | `/conversations/{conversationId}/messages` | `message.view` | - |
| `POST` | `/conversations/{conversationId}/avatar` | `conversation.file.manage` | - |
| `POST` | `/conversations/{conversationId}/messages/attachments` | `conversation.file.manage` | - |
| `POST` | `/conversations/{conversationId}/files/download` | `conversation.file.manage` | - |
| `POST` | `/conversations/{conversationId}/files/download-zip` | `conversation.file.manage` | - |

---

## 6. FE Permission Check Logic

### 6.1. TypeScript Helper Functions

```typescript
// types.ts
interface UserContext {
  clientId: string;
  systemRole: 'SYS_ADMIN' | 'USER';
  authorities: string[];
  departments: DepartmentMembership[];
  teams: TeamMembership[];
}

interface DepartmentMembership {
  departmentCode: string;
  roleCode: 'DEPT_DIR' | 'DEPT_MGR' | 'DEPT_MEM';
  isPrimary: boolean;
}

interface TeamMembership {
  teamCode: string;
  roleCode: 'TEAM_LEAD' | 'TEAM_MEM';
  isLeader: boolean;
}

// permission-utils.ts
const SYSTEM_FULL_PERMISSION = 'system.full';

/**
 * Check if user has system admin permission (bypass all checks)
 */
export const isSystemAdmin = (user: UserContext): boolean => {
  return user.systemRole === 'SYS_ADMIN' || 
         user.authorities.includes(SYSTEM_FULL_PERMISSION);
};

/**
 * Check if user has a global permission
 */
export const hasGlobalPermission = (
  user: UserContext, 
  permission: string
): boolean => {
  if (isSystemAdmin(user)) return true;
  return user.authorities.includes(permission);
};

/**
 * Check if user has a scoped permission for a specific department
 */
export const hasDepartmentPermission = (
  user: UserContext,
  departmentCode: string,
  permission: string
): boolean => {
  if (isSystemAdmin(user)) return true;
  
  // Check scoped authority: DEPARTMENT:DEPT01:permission
  const scopedAuthority = `DEPARTMENT:${departmentCode}:${permission}`;
  return user.authorities.includes(scopedAuthority);
};

/**
 * Check if user has a scoped permission for a specific team
 */
export const hasTeamPermission = (
  user: UserContext,
  teamCode: string,
  permission: string
): boolean => {
  if (isSystemAdmin(user)) return true;
  
  // Check scoped authority: TEAM:T01:permission
  const scopedAuthority = `TEAM:${teamCode}:${permission}`;
  return user.authorities.includes(scopedAuthority);
};

/**
 * Check if user belongs to a department
 */
export const belongsToDepartment = (
  user: UserContext,
  departmentCode: string
): boolean => {
  if (isSystemAdmin(user)) return true;
  return user.departments.some(d => d.departmentCode === departmentCode);
};

/**
 * Check if user belongs to a team
 */
export const belongsToTeam = (
  user: UserContext,
  teamCode: string
): boolean => {
  if (isSystemAdmin(user)) return true;
  return user.teams.some(t => t.teamCode === teamCode);
};

/**
 * Get user's role in a specific department
 */
export const getDepartmentRole = (
  user: UserContext,
  departmentCode: string
): string | null => {
  const membership = user.departments.find(
    d => d.departmentCode === departmentCode
  );
  return membership?.roleCode ?? null;
};

/**
 * Get user's role in a specific team
 */
export const getTeamRole = (
  user: UserContext,
  teamCode: string
): string | null => {
  const membership = user.teams.find(t => t.teamCode === teamCode);
  return membership?.roleCode ?? null;
};

/**
 * Check if user can access a resource
 * Use for conditional rendering
 */
export const canAccess = (
  user: UserContext,
  permission: string,
  scope?: { type: 'DEPARTMENT' | 'TEAM'; code: string }
): boolean => {
  if (isSystemAdmin(user)) return true;
  
  if (scope) {
    if (scope.type === 'DEPARTMENT') {
      return hasDepartmentPermission(user, scope.code, permission);
    }
    if (scope.type === 'TEAM') {
      return hasTeamPermission(user, scope.code, permission);
    }
  }
  
  return hasGlobalPermission(user, permission);
};
```

### 6.2. React Hook Example

```typescript
// usePermission.ts
import { useContext } from 'react';
import { UserContext } from './auth-context';
import * as permissionUtils from './permission-utils';

export const usePermission = () => {
  const user = useContext(UserContext);
  
  return {
    isAdmin: () => permissionUtils.isSystemAdmin(user),
    hasPermission: (permission: string) => 
      permissionUtils.hasGlobalPermission(user, permission),
    hasDeptPermission: (deptCode: string, permission: string) =>
      permissionUtils.hasDepartmentPermission(user, deptCode, permission),
    hasTeamPermission: (teamCode: string, permission: string) =>
      permissionUtils.hasTeamPermission(user, teamCode, permission),
    canAccess: (permission: string, scope?: { type: 'DEPARTMENT' | 'TEAM'; code: string }) =>
      permissionUtils.canAccess(user, permission, scope),
    belongsToDept: (deptCode: string) =>
      permissionUtils.belongsToDepartment(user, deptCode),
    belongsToTeam: (teamCode: string) =>
      permissionUtils.belongsToTeam(user, teamCode),
    getDeptRole: (deptCode: string) =>
      permissionUtils.getDepartmentRole(user, deptCode),
    getTeamRole: (teamCode: string) =>
      permissionUtils.getTeamRole(user, teamCode),
  };
};
```

---

## 7. UI Component Mapping

### 7.1. Sidebar Menu

```typescript
// sidebar-config.ts
const menuItems = [
  {
    id: 'dashboard',
    label: 'Dashboard',
    path: '/dashboard',
    permission: null, // Không cần permission
  },
  {
    id: 'my-profile',
    label: 'My Profile',
    path: '/profile',
    permission: 'self.profile.view',
  },
  {
    id: 'employees',
    label: 'Employees',
    path: '/employees',
    permission: 'employee.view.basic',
  },
  {
    id: 'departments',
    label: 'Departments',
    path: '/departments',
    permission: 'department.view',
    children: [
      {
        id: 'dept-list',
        label: 'All Departments',
        path: '/departments',
        permission: 'department.view',
      },
      {
        id: 'dept-create',
        label: 'Create Department',
        path: '/departments/create',
        permission: 'department.create', // SYS_ADMIN only
      },
    ],
  },
  {
    id: 'teams',
    label: 'Teams',
    path: '/teams',
    permission: 'team.view',
  },
  {
    id: 'tasks',
    label: 'My Tasks',
    path: '/tasks',
    permission: 'task.view.self',
  },
  {
    id: 'roles',
    label: 'Roles',
    path: '/roles',
    permission: 'role.view',
  },
];

// Render sidebar
const Sidebar = () => {
  const { hasPermission, isAdmin } = usePermission();
  
  return (
    <nav>
      {menuItems
        .filter(item => !item.permission || hasPermission(item.permission) || isAdmin())
        .map(item => (
          <MenuItem key={item.id} {...item} />
        ))}
    </nav>
  );
};
```

### 7.2. Action Buttons

```typescript
// DepartmentDetail.tsx
const DepartmentDetail = ({ departmentCode }) => {
  const { hasDeptPermission, isAdmin } = usePermission();
  
  const canEdit = isAdmin() || 
    hasDeptPermission(departmentCode, 'department.update');
  const canAddMember = isAdmin() || 
    hasDeptPermission(departmentCode, 'department.member.add');
  const canRemoveMember = isAdmin() || 
    hasDeptPermission(departmentCode, 'department.member.remove');
  
  return (
    <div>
      <h1>Department: {departmentCode}</h1>
      
      {canEdit && (
        <Button onClick={handleEdit}>Edit Department</Button>
      )}
      
      {canAddMember && (
        <Button onClick={handleAddMember}>Add Member</Button>
      )}
      
      <MemberList 
        departmentCode={departmentCode}
        showRemoveButton={canRemoveMember}
      />
    </div>
  );
};
```

### 7.3. Task Management

```typescript
// TeamTasks.tsx
const TeamTasks = ({ teamCode }) => {
  const { hasTeamPermission, isAdmin, belongsToTeam } = usePermission();
  
  // Kiểm tra user có thuộc team không
  if (!isAdmin() && !belongsToTeam(teamCode)) {
    return <AccessDenied message="You do not belong to this team" />;
  }
  
  const canCreate = isAdmin() || 
    hasTeamPermission(teamCode, 'task.create');
  const canUpdate = isAdmin() || 
    hasTeamPermission(teamCode, 'task.update');
  const canDelete = isAdmin() || 
    hasTeamPermission(teamCode, 'task.delete');
  const canAssign = isAdmin() || 
    hasTeamPermission(teamCode, 'task.assign');
  const canApprove = isAdmin() || 
    hasTeamPermission(teamCode, 'task.approve');
  
  return (
    <div>
      <h1>Team {teamCode} Tasks</h1>
      
      {canCreate && (
        <Button onClick={handleCreate}>Create Task</Button>
      )}
      
      <TaskList 
        teamCode={teamCode}
        showEditButton={canUpdate}
        showDeleteButton={canDelete}
        showAssignButton={canAssign}
        showApproveButton={canApprove}
      />
    </div>
  );
};
```

### 7.4. Salary Column (Conditional Display)

```typescript
// EmployeeTable.tsx
const EmployeeTable = ({ departmentCode }) => {
  const { isAdmin, hasDeptPermission } = usePermission();
  
  // Kiểm tra có quyền xem salary không
  const canViewSalary = isAdmin() || 
    hasDeptPermission(departmentCode, 'employee.view.salary');
  
  const columns = [
    { key: 'firstName', label: 'First Name' },
    { key: 'lastName', label: 'Last Name' },
    { key: 'email', label: 'Email' },
    // Chỉ hiển thị cột salary nếu có quyền
    ...(canViewSalary ? [{ key: 'salary', label: 'Salary' }] : []),
  ];
  
  return <Table columns={columns} data={employees} />;
};
```

---

## 8. Error Handling

### 8.1. HTTP 403 Response

Khi BE trả về 403 Forbidden:

```json
{
  "timestamp": "2024-01-18T10:30:00Z",
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied: You do not have permission to access this resource",
  "path": "/api/departments/DEPT02/employees"
}
```

### 8.2. FE Error Handling

```typescript
// api-client.ts
import axios from 'axios';

const apiClient = axios.create({
  baseURL: '/api',
});

apiClient.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      // Token expired or invalid
      redirectToLogin();
    }
    
    if (error.response?.status === 403) {
      // User doesn't have permission
      showNotification({
        type: 'error',
        message: 'Bạn không có quyền truy cập chức năng này',
      });
      // Optionally redirect to dashboard
      // router.push('/dashboard');
    }
    
    return Promise.reject(error);
  }
);
```

### 8.3. Access Denied Component

```typescript
// AccessDenied.tsx
const AccessDenied = ({ message }) => (
  <div className="access-denied">
    <Icon name="lock" size={64} />
    <h2>Truy Cập Bị Từ Chối</h2>
    <p>{message || 'Bạn không có quyền truy cập trang này'}</p>
    <Button onClick={() => router.push('/dashboard')}>
      Quay về Dashboard
    </Button>
  </div>
);
```

---

## Quick Reference Card

### Permission Check Cheat Sheet

| Muốn làm gì | Check như thế nào |
|-------------|-------------------|
| Xem profile cá nhân | `hasPermission('self.profile.view')` |
| Xem employee khác | `hasPermission('employee.view.basic')` |
| Xem salary của employee trong DEPT01 | `hasDeptPermission('DEPT01', 'employee.view.salary')` |
| Thêm member vào DEPT01 | `hasDeptPermission('DEPT01', 'department.member.add')` |
| Tạo task trong team T01 | `hasTeamPermission('T01', 'task.create')` |
| Approve task trong team T01 | `hasTeamPermission('T01', 'task.approve')` |
| Xóa department (SYS_ADMIN only) | `isAdmin()` |
| Gửi tin nhắn | `hasPermission('message.send')` |
| Tạo cuộc hội thoại | `hasPermission('conversation.create')` |
| Upload file trong chat | `hasPermission('conversation.file.manage')` |
| Thêm thành viên vào nhóm chat | `hasPermission('conversation.participant.manage')` |

### Role Capabilities Summary

| Role | Scope | Có thể làm gì |
|------|-------|---------------|
| SYS_ADMIN | Global | Tất cả |
| USER | Global | Xem profile, employees, depts, teams, roles + **Nhắn tin đầy đủ** |
| DEPT_DIR | Department | CRUD dept/team/employee/task trong dept của mình + Nhắn tin |
| DEPT_MGR | Department | Manage team members, CRUD tasks + Nhắn tin |
| TEAM_LEAD | Team | CRUD tasks, assign, approve trong team + Nhắn tin |
| TEAM_MEM | Team | Xem tasks trong team + Nhắn tin |
| DEPT_MEM | Department | Xem resources trong dept + Nhắn tin |

---

## Changelog

| Version | Date | Description |
|---------|------|-------------|
| 1.0.0 | 2024-01-18 | Initial version |
| 1.1.0 | 2024-01-20 | Added messaging and conversation permissions |
