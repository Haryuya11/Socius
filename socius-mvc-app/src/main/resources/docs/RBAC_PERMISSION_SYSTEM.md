# Hệ Thống Phân Quyền RBAC - Socius

Tài liệu này mô tả chi tiết hệ thống phân quyền RBAC (Role-Based Access Control) của Socius, giúp các developer (Backend/Frontend) hiểu và tích hợp dễ dàng.

---

## Mục Lục

1. [Tổng Quan](#1-tổng-quan)
2. [Kiến Trúc Hệ Thống](#2-kiến-trúc-hệ-thống)
3. [Roles (Vai Trò)](#3-roles-vai-trò)
4. [Permissions (Quyền)](#4-permissions-quyền)
5. [Role-Permission Mapping](#5-role-permission-mapping)
6. [API Endpoint Permissions](#6-api-endpoint-permissions)
7. [Scope-Based Authorization](#7-scope-based-authorization)
8. [Hướng Dẫn Frontend](#8-hướng-dẫn-frontend)
9. [Messaging & Conversation Permissions](#9-messaging--conversation-permissions)

---

## 1. Tổng Quan

### 1.1. RBAC là gì?

RBAC (Role-Based Access Control) là mô hình phân quyền dựa trên vai trò:
- **User** được gán các **Roles** (vai trò)
- **Roles** chứa các **Permissions** (quyền)
- **Permissions** ánh xạ đến các **API Endpoints**

### 1.2. Đặc điểm của hệ thống

| Đặc điểm | Mô tả |
|----------|-------|
| **Database-driven** | Tất cả roles, permissions, mappings được lưu trong database |
| **Scope-based** | Phân quyền theo phạm vi (Department, Team) |
| **Hierarchical** | Roles kế thừa permissions từ USER role |
| **Runtime resolution** | Permissions được resolve tại runtime, không hardcode |

### 1.3. Luồng Authorization

```
Request → JWT Token → Extract User → Load Permissions → Check API Permission → Allow/Deny
```

---

## 2. Kiến Trúc Hệ Thống

### 2.1. Database Schema

```
┌─────────────────┐     ┌─────────────────────┐     ┌──────────────────┐
│     roles       │     │  role_permissions   │     │   permissions    │
├─────────────────┤     ├─────────────────────┤     ├──────────────────┤
│ role_code (PK)  │────▶│ role_code (FK)      │◀────│ permission_code  │
│ role_name       │     │ permission_code (FK)│     │ permission_name  │
│ role_type       │     └─────────────────────┘     │ resource         │
│ description     │                                 │ action           │
└─────────────────┘                                 │ description      │
                                                    └──────────────────┘

┌─────────────────────────────────────────┐
│           api_permissions               │
├─────────────────────────────────────────┤
│ permission_code                         │
│ resource                                │
│ action                                  │
│ http_method (GET/POST/PUT/DELETE)       │
│ url_pattern (/teams/{teamCode})         │
└─────────────────────────────────────────┘
```

### 2.2. File Configuration

| File | Mô tả |
|------|-------|
| `data/role-data.sql` | Định nghĩa roles, permissions, role-permission mappings |
| `data/api-permissions-data.sql` | Ánh xạ API endpoints đến permissions |
| `data/schema.sql` | Database schema cho RBAC tables |

---

## 3. Roles (Vai Trò)

### 3.1. System Roles (Vai trò hệ thống)

| Role Code | Tên | Mô tả |
|-----------|-----|-------|
| `SYS_ADMIN` | System Administrator | Toàn quyền hệ thống - bypass tất cả permission checks |
| `USER` | User | Quyền cơ bản cho tất cả người dùng |

### 3.2. Department Roles (Vai trò phòng ban)

| Role Code | Tên | Mô tả |
|-----------|-----|-------|
| `DEPT_DIR` | Department Director | Giám đốc phòng - quản lý toàn bộ phòng ban |
| `DEPT_MGR` | Department Manager | Quản lý phòng - hỗ trợ giám đốc |
| `DEPT_MEM` | Department Member | Thành viên phòng - quyền cơ bản |

### 3.3. Team Roles (Vai trò nhóm)

| Role Code | Tên | Mô tả |
|-----------|-----|-------|
| `TEAM_LEAD` | Team Leader | Trưởng nhóm - quản lý tasks trong nhóm |
| `TEAM_MEM` | Team Member | Thành viên nhóm - quyền cơ bản |

### 3.4. Sơ đồ kế thừa quyền

```
SYS_ADMIN ──────────────────────────────────────► system.full (bypass all)
     │
     ▼
   USER (base permissions)
     │
     ├───► DEPT_DIR ──► + department/team/task management
     │         │
     │         ▼
     ├───► DEPT_MGR ──► + team member/task management
     │
     ├───► DEPT_MEM ──► (same as USER in dept scope)
     │
     ├───► TEAM_LEAD ──► + task management in team
     │
     └───► TEAM_MEM ──► (same as USER in team scope)
```

---

## 4. Permissions (Quyền)

### 4.1. Self Permissions (Quản lý bản thân)

| Permission Code | Mô tả | Ai có quyền |
|-----------------|-------|-------------|
| `self.profile.view` | Xem profile cá nhân | Tất cả |
| `self.profile.update` | Cập nhật profile | Tất cả |
| `self.password.update` | Đổi mật khẩu | Tất cả |
| `self.avatar.upload` | Upload avatar | Tất cả |

### 4.2. Employee Permissions

| Permission Code | Mô tả | Ai có quyền |
|-----------------|-------|-------------|
| `employee.view.basic` | Xem thông tin nhân viên (không lương) | Tất cả |
| `employee.view.salary` | Xem lương nhân viên | DEPT_DIR, DEPT_MGR |
| `employee.profile.update` | Cập nhật thông tin nhân viên | DEPT_DIR |

### 4.3. Department Permissions

| Permission Code | Mô tả | Ai có quyền |
|-----------------|-------|-------------|
| `department.view` | Xem thông tin phòng ban | Tất cả |
| `department.create` | Tạo phòng ban | SYS_ADMIN |
| `department.update` | Cập nhật phòng ban | DEPT_DIR (scoped) |
| `department.delete` | Xóa phòng ban | SYS_ADMIN |
| `department.member.add` | Thêm thành viên vào phòng | DEPT_DIR (scoped) |
| `department.member.remove` | Xóa thành viên khỏi phòng | DEPT_DIR (scoped) |
| `department.member.role.update` | Cập nhật vai trò thành viên | DEPT_DIR (scoped) |

### 4.4. Team Permissions

| Permission Code | Mô tả | Ai có quyền |
|-----------------|-------|-------------|
| `team.view` | Xem thông tin nhóm | Tất cả |
| `team.create` | Tạo nhóm | DEPT_DIR (scoped) |
| `team.update` | Cập nhật nhóm | DEPT_DIR (scoped) |
| `team.delete` | Xóa nhóm | DEPT_DIR (scoped) |
| `team.member.add` | Thêm thành viên vào nhóm | DEPT_DIR, DEPT_MGR (scoped) |
| `team.member.remove` | Xóa thành viên khỏi nhóm | DEPT_DIR, DEPT_MGR (scoped) |
| `team.member.role.update` | Cập nhật vai trò trong nhóm | DEPT_DIR, DEPT_MGR (scoped) |

### 4.5. Task Permissions

| Permission Code | Mô tả | Ai có quyền |
|-----------------|-------|-------------|
| `task.view.self` | Xem task của mình | Tất cả |
| `task.update.self` | Cập nhật task của mình | Tất cả |
| `task.view.team` | Xem tasks trong nhóm | TEAM_LEAD, DEPT_DIR, DEPT_MGR |
| `task.view.department` | Xem tasks trong phòng | DEPT_DIR, DEPT_MGR |
| `task.create` | Tạo task | TEAM_LEAD, DEPT_DIR, DEPT_MGR |
| `task.update` | Cập nhật task | TEAM_LEAD, DEPT_DIR, DEPT_MGR |
| `task.delete` | Xóa task | TEAM_LEAD, DEPT_DIR, DEPT_MGR |
| `task.assign` | Giao task | TEAM_LEAD, DEPT_DIR, DEPT_MGR |
| `task.approve` | Duyệt task | TEAM_LEAD, DEPT_DIR, DEPT_MGR |

### 4.6. Message Permissions (Nhắn tin)

| Permission Code | Mô tả | Ai có quyền |
|-----------------|-------|-------------|
| `message.view` | Xem tin nhắn | Tất cả |
| `message.send` | Gửi tin nhắn | Tất cả |
| `message.update` | Sửa tin nhắn của mình | Tất cả |
| `message.delete` | Xóa tin nhắn của mình | Tất cả |
| `message.reaction` | Thêm/xóa reaction | Tất cả |

### 4.7. Conversation Permissions (Cuộc hội thoại)

| Permission Code | Mô tả | Ai có quyền |
|-----------------|-------|-------------|
| `conversation.view` | Xem cuộc hội thoại | Tất cả |
| `conversation.create` | Tạo cuộc hội thoại | Tất cả |
| `conversation.update` | Cập nhật cuộc hội thoại | Tất cả |
| `conversation.delete` | Xóa cuộc hội thoại | Tất cả |
| `conversation.participant.manage` | Quản lý thành viên | Tất cả |
| `conversation.file.manage` | Upload/download file | Tất cả |

### 4.8. Other Permissions

| Permission Code | Mô tả | Ai có quyền |
|-----------------|-------|-------------|
| `role.view` | Xem danh sách roles | Tất cả |
| `report.view` | Xem báo cáo | SYS_ADMIN |
| `report.export` | Xuất báo cáo | SYS_ADMIN |
| `system.full` | Toàn quyền hệ thống | SYS_ADMIN |

---

## 5. Role-Permission Mapping

### 5.1. USER Role (Base permissions)

Tất cả người dùng đều có các quyền sau:

```sql
-- Self management
self.profile.view, self.profile.update, self.password.update, self.avatar.upload

-- View others
employee.view.basic, department.view, team.view, role.view

-- Tasks
task.view.self, task.update.self

-- Messaging (ALL users have access)
message.view, message.send, message.update, message.delete, message.reaction

-- Conversations (ALL users have access)
conversation.view, conversation.create, conversation.update, conversation.delete,
conversation.participant.manage, conversation.file.manage
```

### 5.2. Kế thừa quyền

- `DEPT_DIR`, `DEPT_MGR`, `TEAM_LEAD`, `DEPT_MEM`, `TEAM_MEM` **kế thừa tất cả quyền của USER**
- Mỗi role có thêm các quyền riêng (xem Section 4)

---

## 6. API Endpoint Permissions

### 6.1. Message Endpoints

| Method | Endpoint | Permission |
|--------|----------|------------|
| `POST` | `/messages` | `message.send` |
| `GET` | `/messages/{messageId}` | `message.view` |
| `PUT` | `/messages/{messageId}` | `message.update` |
| `DELETE` | `/messages/{messageId}` | `message.delete` |
| `POST` | `/messages/reactions` | `message.reaction` |
| `DELETE` | `/messages/reactions` | `message.reaction` |
| `GET` | `/messages/{messageId}/reactions` | `message.view` |

### 6.2. Conversation Endpoints

| Method | Endpoint | Permission |
|--------|----------|------------|
| `POST` | `/conversations/direct/{targetEmployeeId}` | `conversation.create` |
| `POST` | `/conversations/group` | `conversation.create` |
| `GET` | `/conversations/{conversationId}` | `conversation.view` |
| `PUT` | `/conversations/{conversationId}` | `conversation.update` |
| `DELETE` | `/conversations/{conversationId}` | `conversation.delete` |
| `GET` | `/conversations` | `conversation.view` |
| `GET` | `/conversations/{conversationId}/participants` | `conversation.view` |
| `POST` | `/conversations/{conversationId}/participants` | `conversation.participant.manage` |
| `DELETE` | `/conversations/{conversationId}/participants` | `conversation.participant.manage` |
| `POST` | `/conversations/{conversationId}/leave` | `conversation.update` |
| `PUT` | `/conversations/{conversationId}/settings` | `conversation.update` |
| `PUT` | `/conversations/{conversationId}/read/{messageId}` | `conversation.update` |
| `GET` | `/conversations/{conversationId}/messages` | `message.view` |
| `POST` | `/conversations/{conversationId}/avatar` | `conversation.file.manage` |
| `POST` | `/conversations/{conversationId}/messages/attachments` | `conversation.file.manage` |
| `POST` | `/conversations/{conversationId}/files/download` | `conversation.file.manage` |
| `POST` | `/conversations/{conversationId}/files/download-zip` | `conversation.file.manage` |

### 6.3. Employee Endpoints

| Method | Endpoint | Permission |
|--------|----------|------------|
| `GET` | `/employees/profile` | `self.profile.view` |
| `PUT` | `/employees/change-password` | `self.password.update` |
| `POST` | `/employees/upload-avatar` | `self.avatar.upload` |
| `GET` | `/employees/avatar-url` | `self.profile.view` |
| `GET` | `/employees/{clientId}` | `employee.view.basic` |
| `PUT` | `/employees/{clientId}` | `employee.profile.update` |
| `DELETE` | `/employees/{clientId}` | `employee.profile.update` |
| `POST` | `/employees` | `employee.profile.update` |
| `POST` | `/employees/search` | `employee.view.basic` |

### 6.4. Team Endpoints

| Method | Endpoint | Permission |
|--------|----------|------------|
| `GET` | `/teams/{teamCode}` | `team.view` |
| `POST` | `/teams` | `team.create` |
| `PUT` | `/teams/{teamCode}` | `team.update` |
| `DELETE` | `/teams/{teamCode}` | `team.delete` |
| `POST` | `/teams/search` | `team.view` |
| `GET` | `/teams/{teamCode}/employees` | `team.view` |
| `POST` | `/teams/{teamCode}/employees` | `team.member.add` |
| `DELETE` | `/teams/{teamCode}/employees/{employeeId}` | `team.member.remove` |
| `PUT` | `/teams/{teamCode}/employees/{employeeId}` | `team.member.role.update` |

### 6.5. Department Endpoints

| Method | Endpoint | Permission |
|--------|----------|------------|
| `GET` | `/departments/{departmentCode}` | `department.view` |
| `POST` | `/departments` | `department.create` |
| `PUT` | `/departments/{departmentCode}` | `department.update` |
| `DELETE` | `/departments/{departmentCode}` | `department.delete` |
| `POST` | `/departments/search` | `department.view` |
| `GET` | `/departments/{departmentCode}/employees` | `department.view` |
| `POST` | `/departments/{departmentCode}/employees` | `department.member.add` |
| `DELETE` | `/departments/{departmentCode}/employees/{employeeId}` | `department.member.remove` |
| `PUT` | `/departments/{departmentCode}/employees/{employeeId}` | `department.member.role.update` |

### 6.6. Task Endpoints

| Method | Endpoint | Permission |
|--------|----------|------------|
| `GET` | `/tasks/my-tasks` | `task.view.self` |
| `GET` | `/tasks/assigned-by-me` | `task.view.self` |
| `GET` | `/tasks/{id}` | `task.view.self` |
| `POST` | `/tasks` | `task.create` |
| `PUT` | `/tasks/{id}` | `task.update` |
| `DELETE` | `/tasks/{id}` | `task.delete` |
| `POST` | `/tasks/search` | `task.view.self` |
| `GET` | `/tasks/team/{teamCode}` | `task.view.team` |
| `GET` | `/tasks/department/{departmentCode}` | `task.view.department` |
| `PUT` | `/tasks/{id}/submit-review` | `task.update.self` |
| `PUT` | `/tasks/{id}/approve` | `task.approve` |
| `PUT` | `/tasks/{id}/reject` | `task.approve` |
| `PUT` | `/tasks/{id}/cancel` | `task.update` |
| `PUT` | `/tasks/{id}/reopen` | `task.update` |

### 6.7. Notification Endpoints

| Method | Endpoint | Permission |
|--------|----------|------------|
| `GET` | `/notifications` | `self.profile.view` |
| `PUT` | `/notifications/{notificationId}/read` | `self.profile.update` |
| `PUT` | `/notifications/read-all` | `self.profile.update` |

### 6.8. Role Endpoints

| Method | Endpoint | Permission |
|--------|----------|------------|
| `GET` | `/roles` | `role.view` |
| `GET` | `/roles/{roleCode}` | `role.view` |
| `GET` | `/roles/type/{roleType}` | `role.view` |

---

## 7. Scope-Based Authorization

### 7.1. Khái niệm Scope

User chỉ có quyền trong phạm vi (scope) họ thuộc về:

| Scope Type | Format | Ví dụ |
|------------|--------|-------|
| **Global** | `{permission}` | `employee.view.basic` |
| **Department** | `DEPARTMENT:{code}:{permission}` | `DEPARTMENT:DEPT01:department.view` |
| **Team** | `TEAM:{code}:{permission}` | `TEAM:T01:task.create` |

### 7.2. Authority Format trong JWT

```json
{
  "authorities": [
    "self.profile.view",
    "employee.view.basic",
    "message.send",
    "conversation.create",
    "DEPARTMENT:DEPT01:department.member.add",
    "TEAM:T01:task.create",
    "TEAM:T01:task.approve"
  ]
}
```

### 7.3. Hierarchical Access

- **DEPT_DIR** có quyền với tất cả teams trong department của mình
- **DEPT_MGR** có quyền quản lý team members
- **TEAM_LEAD** chỉ có quyền trong team của mình

---

## 8. Hướng Dẫn Frontend

### 8.1. Kiểm tra quyền cơ bản

```typescript
// Kiểm tra global permission
const hasMessagePermission = authorities.includes('message.send');

// Kiểm tra scoped permission
const hasDeptPermission = authorities.includes(`DEPARTMENT:${deptCode}:department.view`);
const hasTeamPermission = authorities.includes(`TEAM:${teamCode}:task.create`);
```

### 8.2. Helper Functions

```typescript
// Check if user has permission
const hasPermission = (authorities: string[], permission: string): boolean => {
  return authorities.includes(permission) || authorities.includes('system.full');
};

// Check if user has scoped permission
const hasScopedPermission = (
  authorities: string[], 
  scopeType: 'DEPARTMENT' | 'TEAM',
  scopeCode: string,
  permission: string
): boolean => {
  const scopedAuth = `${scopeType}:${scopeCode}:${permission}`;
  return authorities.includes(scopedAuth) || authorities.includes('system.full');
};
```

### 8.3. Hiển thị UI theo quyền

```tsx
// Messaging - tất cả user đều có quyền
{hasPermission(authorities, 'message.send') && (
  <SendMessageButton />
)}

// Conversation management
{hasPermission(authorities, 'conversation.create') && (
  <CreateConversationButton />
)}

// Department management - chỉ DEPT_DIR trong scope
{hasScopedPermission(authorities, 'DEPARTMENT', deptCode, 'department.member.add') && (
  <AddMemberButton />
)}
```

---

## 9. Messaging & Conversation Permissions

### 9.1. Tổng quan

**Tất cả người dùng đã đăng nhập** đều có quyền sử dụng tính năng nhắn tin và cuộc hội thoại. Không có phân quyền đặc biệt theo role hoặc scope.

### 9.2. Quyền Messaging

| Tính năng | Permission | Mô tả |
|-----------|------------|-------|
| Gửi tin nhắn | `message.send` | Gửi tin nhắn mới trong cuộc hội thoại |
| Xem tin nhắn | `message.view` | Xem tin nhắn và reactions |
| Sửa tin nhắn | `message.update` | Sửa tin nhắn của chính mình |
| Xóa tin nhắn | `message.delete` | Xóa tin nhắn của chính mình |
| Thêm reaction | `message.reaction` | Thêm/xóa emoji reaction |

### 9.3. Quyền Conversation

| Tính năng | Permission | Mô tả |
|-----------|------------|-------|
| Tạo hội thoại trực tiếp | `conversation.create` | Tạo chat 1-1 với người khác |
| Tạo nhóm chat | `conversation.create` | Tạo group conversation |
| Xem hội thoại | `conversation.view` | Xem danh sách và chi tiết hội thoại |
| Cập nhật hội thoại | `conversation.update` | Đổi tên, settings, rời nhóm |
| Xóa hội thoại | `conversation.delete` | Xóa cuộc hội thoại |
| Quản lý thành viên | `conversation.participant.manage` | Thêm/xóa thành viên nhóm |
| Quản lý file | `conversation.file.manage` | Upload avatar, đính kèm file |

### 9.4. Ví dụ Frontend

```tsx
// MessageInput component - hiển thị cho tất cả users
const MessageInput = () => {
  const { hasPermission } = useAuth();
  
  // Kiểm tra quyền phòng trường hợp permission bị thu hồi
  if (!hasPermission('message.send')) {
    return null;
  }
  
  return (
    <form onSubmit={sendMessage}>
      <input type="text" placeholder="Type a message..." />
      <button type="submit">Send</button>
    </form>
  );
};

// ConversationList - hiển thị cho tất cả users
const ConversationList = () => {
  const { hasPermission } = useAuth();
  
  return (
    <div>
      {hasPermission('conversation.create') && (
        <button onClick={createNewConversation}>New Chat</button>
      )}
      
      {conversations.map(conv => (
        <ConversationItem key={conv.id} conversation={conv} />
      ))}
    </div>
  );
};
```

---

## Quick Reference

### Permission Check Cheatsheet

| Muốn làm gì | Cách kiểm tra |
|-------------|---------------|
| Gửi tin nhắn | `hasPermission('message.send')` |
| Tạo cuộc hội thoại | `hasPermission('conversation.create')` |
| Quản lý file trong chat | `hasPermission('conversation.file.manage')` |
| Xem thông tin nhân viên | `hasPermission('employee.view.basic')` |
| Thêm member vào DEPT01 | `hasScopedPermission('DEPARTMENT', 'DEPT01', 'department.member.add')` |
| Tạo task trong team T01 | `hasScopedPermission('TEAM', 'T01', 'task.create')` |

### Role Summary

| Role | Scope | Quyền chính |
|------|-------|-------------|
| SYS_ADMIN | Global | Toàn quyền |
| USER | Global | Xem cơ bản + Nhắn tin |
| DEPT_DIR | Department | Quản lý phòng/nhóm/task |
| DEPT_MGR | Department | Quản lý nhóm/task |
| DEPT_MEM | Department | Quyền cơ bản trong phòng |
| TEAM_LEAD | Team | Quản lý task trong nhóm |
| TEAM_MEM | Team | Quyền cơ bản trong nhóm |

---

## Changelog

| Version | Date | Description |
|---------|------|-------------|
| 1.0.0 | 2024-01-18 | Initial version |
| 1.1.0 | 2024-01-20 | Added messaging and conversation permissions |
