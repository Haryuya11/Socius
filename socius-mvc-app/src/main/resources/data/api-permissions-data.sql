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
-- Task operations (scoped to team)
('task.view.team', 'team', 'read', 'GET', '/teams/{teamCode}/tasks'),
('task.view.team', 'team', 'read', 'GET', '/teams/{teamCode}/tasks/{taskId}'),
('task.create', 'team', 'create', 'POST', '/teams/{teamCode}/tasks'),
('task.update', 'team', 'update', 'PUT', '/teams/{teamCode}/tasks/{taskId}'),
('task.delete', 'team', 'delete', 'DELETE', '/teams/{teamCode}/tasks/{taskId}'),
('task.approve', 'team', 'approve', 'POST', '/teams/{teamCode}/tasks/{taskId}/approve'),
('task.assign', 'team', 'assign', 'POST', '/teams/{teamCode}/tasks/{taskId}/assign');

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
