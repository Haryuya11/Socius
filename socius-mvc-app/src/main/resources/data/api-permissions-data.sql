-- =====================================================
-- API PERMISSIONS - Database-driven RBAC
-- =====================================================
-- Maps API endpoints to required permissions
-- These permissions are resolved at runtime from the database
-- =====================================================

-- Clean up existing data
TRUNCATE TABLE api_permissions CASCADE;

-- =====================================================
-- 1. EMPLOYEE ENDPOINTS
-- =====================================================
INSERT INTO api_permissions (permission_code, resource, action, http_method, url_pattern)
VALUES
-- Own profile management
('self.profile.view', 'self', 'read', 'GET', '/employees/profile'),
('self.password.update', 'self', 'update', 'PUT', '/employees/change-password'),
('self.avatar.upload', 'self', 'update', 'POST', '/employees/upload-avatar'),
('self.profile.view', 'self', 'read', 'GET', '/employees/avatar-url'),

-- Employee CRUD (admin operations)
('employee.view.basic', 'employee', 'read', 'GET', '/employees/{clientId}'),
('employee.profile.update', 'employee', 'update', 'PUT', '/employees/{clientId}'),
('system.full', 'employee', 'update', 'PUT', '/employees/{clientId}/salary'),
('system.full', 'employee', 'update', 'PUT', '/employees/{clientId}/system-role'),
('employee.profile.update', 'employee', 'delete', 'DELETE', '/employees/{clientId}'),
('employee.profile.update', 'employee', 'create', 'POST', '/employees'),
('employee.view.basic', 'employee', 'read', 'POST', '/employees/search');

-- =====================================================
-- 2. TEAM ENDPOINTS
-- =====================================================
INSERT INTO api_permissions (permission_code, resource, action, http_method, url_pattern)
VALUES
-- Team CRUD
('team.view', 'team', 'read', 'GET', '/teams/{teamCode}'),
('team.create', 'team', 'create', 'POST', '/teams'),
('team.update', 'team', 'update', 'PUT', '/teams/{teamCode}'),
('team.delete', 'team', 'delete', 'DELETE', '/teams/{teamCode}'),
('team.view', 'team', 'read', 'POST', '/teams/search');

-- =====================================================
-- 3. DEPARTMENT ENDPOINTS
-- =====================================================
INSERT INTO api_permissions (permission_code, resource, action, http_method, url_pattern)
VALUES
-- Department CRUD
('department.view', 'department', 'read', 'GET', '/departments/{departmentCode}'),
('department.create', 'department', 'create', 'POST', '/departments'),
('department.update', 'department', 'update', 'PUT', '/departments/{departmentCode}'),
('department.delete', 'department', 'delete', 'DELETE', '/departments/{departmentCode}'),
('department.view', 'department', 'read', 'POST', '/departments/search');

-- =====================================================
-- 4. TEAM EMPLOYEE (WORKFORCE) ENDPOINTS
-- =====================================================
INSERT INTO api_permissions (permission_code, resource, action, http_method, url_pattern)
VALUES
-- Team member management (scoped to team)
('team.member.add', 'team', 'create', 'POST', '/teams/{teamCode}/employees'),
('team.member.remove', 'team', 'delete', 'DELETE', '/teams/{teamCode}/employees/{employeeId}'),
('team.member.role.update', 'team', 'update', 'PUT', '/teams/{teamCode}/employees/{employeeId}'),
('team.view', 'team', 'read', 'GET', '/teams/{teamCode}/employees');

-- =====================================================
-- 5. DEPARTMENT EMPLOYEE (WORKFORCE) ENDPOINTS
-- =====================================================
INSERT INTO api_permissions (permission_code, resource, action, http_method, url_pattern)
VALUES
-- Department member management (scoped to department)
('department.member.add', 'department', 'create', 'POST', '/departments/{departmentCode}/employees'),
('department.member.remove', 'department', 'delete', 'DELETE', '/departments/{departmentCode}/employees/{employeeId}'),
('department.member.role.update', 'department', 'update', 'PUT', '/departments/{departmentCode}/employees/{employeeId}'),
('department.view', 'department', 'read', 'GET', '/departments/{departmentCode}/employees');

-- =====================================================
-- 6. TASK ENDPOINTS
-- =====================================================
INSERT INTO api_permissions (permission_code, resource, action, http_method, url_pattern)
VALUES
-- Task operations (scoped to team via /teams/{teamCode}/tasks - legacy)
('task.view.team', 'team', 'read', 'GET', '/teams/{teamCode}/tasks'),
('task.view.team', 'team', 'read', 'GET', '/teams/{teamCode}/tasks/{taskId}'),
('task.create', 'team', 'create', 'POST', '/teams/{teamCode}/tasks'),
('task.update', 'team', 'update', 'PUT', '/teams/{teamCode}/tasks/{taskId}'),
('task.delete', 'team', 'delete', 'DELETE', '/teams/{teamCode}/tasks/{taskId}'),
('task.approve', 'team', 'approve', 'POST', '/teams/{teamCode}/tasks/{taskId}/approve'),
('task.assign', 'team', 'assign', 'POST', '/teams/{teamCode}/tasks/{taskId}/assign'),

-- Task CRUD via /tasks endpoints
('task.create', 'task', 'create', 'POST', '/tasks'),
('task.view.self', 'task', 'read', 'GET', '/tasks/{id}'),
('task.update', 'task', 'update', 'PUT', '/tasks/{id}'),
('task.delete', 'task', 'delete', 'DELETE', '/tasks/{id}'),
('task.view.self', 'task', 'read', 'POST', '/tasks/search'),

-- Sub-task operations
('task.create', 'task', 'create', 'POST', '/tasks/{parentId}/sub-tasks'),
('task.view.self', 'task', 'read', 'GET', '/tasks/{parentId}/sub-tasks'),

-- Task activities
('task.view.self', 'task', 'read', 'GET', '/tasks/{id}/activities'),

-- My tasks (own tasks - self-scoped)
('task.view.self', 'task', 'read', 'GET', '/tasks/my-tasks'),
('task.view.self', 'task', 'read', 'GET', '/tasks/assigned-by-me'),

-- Team/Department scoped task views via /tasks endpoints
('task.view.team', 'task', 'read', 'GET', '/tasks/team/{teamCode}'),
('task.view.department', 'task', 'read', 'GET', '/tasks/department/{departmentCode}'),

-- Task state transitions (receiver can submit, sender can approve/reject/cancel/reopen)
('task.update.self', 'task', 'update', 'PUT', '/tasks/{id}/submit-review'),
('task.approve', 'task', 'approve', 'PUT', '/tasks/{id}/approve'),
('task.approve', 'task', 'reject', 'PUT', '/tasks/{id}/reject'),
('task.update', 'task', 'cancel', 'PUT', '/tasks/{id}/cancel'),
('task.update', 'task', 'reopen', 'PUT', '/tasks/{id}/reopen');

-- =====================================================
-- 7. ROLE ENDPOINTS (Read-only, for reference)
-- =====================================================
INSERT INTO api_permissions (permission_code, resource, action, http_method, url_pattern)
VALUES
-- Role viewing (available to authenticated users with basic permissions)
('role.view', 'role', 'read', 'GET', '/roles'),
('role.view', 'role', 'read', 'GET', '/roles/{roleCode}'),
('role.view', 'role', 'read', 'GET', '/roles/type/{roleType}');

-- =====================================================
-- 8. NOTIFICATION ENDPOINTS
-- =====================================================
INSERT INTO api_permissions (permission_code, resource, action, http_method, url_pattern)
VALUES
-- Notification operations (self-scoped)
('self.profile.view', 'notification', 'read', 'GET', '/notifications'),
('self.profile.update', 'notification', 'update', 'PUT', '/notifications/{notificationId}/read'),
('self.profile.update', 'notification', 'update', 'PUT', '/notifications/read-all');

-- =====================================================
-- 9. MESSAGE ENDPOINTS
-- =====================================================
INSERT INTO api_permissions (permission_code, resource, action, http_method, url_pattern)
VALUES
-- Message operations
('message.send', 'message', 'create', 'POST', '/messages'),
('message.view', 'message', 'read', 'GET', '/messages/{messageId}'),
('message.update', 'message', 'update', 'PUT', '/messages/{messageId}'),
('message.delete', 'message', 'delete', 'DELETE', '/messages/{messageId}'),
-- Message reactions
('message.reaction', 'message', 'create', 'POST', '/messages/reactions'),
('message.reaction', 'message', 'delete', 'DELETE', '/messages/reactions'),
('message.view', 'message', 'read', 'GET', '/messages/{messageId}/reactions');

-- =====================================================
-- 10. CONVERSATION ENDPOINTS
-- =====================================================
INSERT INTO api_permissions (permission_code, resource, action, http_method, url_pattern)
VALUES
-- Conversation CRUD
('conversation.create', 'conversation', 'create', 'POST', '/conversations/direct/{targetEmployeeId}'),
('conversation.create', 'conversation', 'create', 'POST', '/conversations/group'),
('conversation.view', 'conversation', 'read', 'GET', '/conversations/{conversationId}'),
('conversation.update', 'conversation', 'update', 'PUT', '/conversations/{conversationId}'),
('conversation.delete', 'conversation', 'delete', 'DELETE', '/conversations/{conversationId}'),
('conversation.view', 'conversation', 'read', 'GET', '/conversations'),
-- Participant management
('conversation.view', 'conversation', 'read', 'GET', '/conversations/{conversationId}/participants'),
('conversation.participant.manage', 'conversation', 'create', 'POST', '/conversations/{conversationId}/participants'),
('conversation.participant.manage', 'conversation', 'delete', 'DELETE', '/conversations/{conversationId}/participants'),
('conversation.update', 'conversation', 'update', 'POST', '/conversations/{conversationId}/leave'),
('conversation.update', 'conversation', 'update', 'PUT', '/conversations/{conversationId}/settings'),
('conversation.update', 'conversation', 'update', 'PUT', '/conversations/{conversationId}/read/{messageId}'),
-- Messages in conversation
('message.view', 'conversation', 'read', 'GET', '/conversations/{conversationId}/messages'),
-- File operations
('conversation.file.manage', 'conversation', 'update', 'POST', '/conversations/{conversationId}/avatar'),
('conversation.file.manage', 'conversation', 'create', 'POST', '/conversations/{conversationId}/messages/attachments'),
('conversation.file.manage', 'conversation', 'read', 'POST', '/conversations/{conversationId}/files/download'),
('conversation.file.manage', 'conversation', 'read', 'POST', '/conversations/{conversationId}/files/download-zip');
