-- =====================================================
-- OPTIMIZED PERMISSION SYSTEM
-- =====================================================

-- Clean up existing data
TRUNCATE TABLE role_permissions CASCADE;
TRUNCATE TABLE permissions CASCADE;
TRUNCATE TABLE roles CASCADE;

-- =====================================================
-- 1. ROLES DEFINITION
-- =====================================================
INSERT INTO roles (role_code, role_name, role_type, description)
VALUES
-- System roles
('SYS_ADMIN', 'System Administrator', 'SYSTEM', 'Full system access - not belong to any department/team'),
('USER', 'User', 'SYSTEM', 'Basic user access'),

-- Department roles (Scope roles)
('DEPT_DIR', 'Department Director', 'DEPARTMENT', 'Department director - manages department resources'),
('DEPT_MGR', 'Department Manager', 'DEPARTMENT', 'Department manager - assists director'),
('DEPT_MEM', 'Department Member', 'DEPARTMENT', 'Department member - same as basic user'),

-- Team roles (Scope roles)
('TEAM_LEAD', 'Team Leader', 'TEAM', 'Team leader - manages team tasks'),
('TEAM_MEM', 'Team Member', 'TEAM', 'Team member - same as basic user');

-- =====================================================
-- 2. PERMISSIONS DEFINITION (Resource-based)
-- =====================================================
INSERT INTO permissions (permission_code, permission_name, resource, action, description)
VALUES
-- ===== SELF permissions (own data) =====
('self.profile.view', 'View Own Profile', 'self', 'read', 'View own profile information'),
('self.profile.update', 'Update Own Profile', 'self', 'update', 'Update own profile (not including salary)'),
('self.password.update', 'Change Own Password', 'self', 'update', 'Change own password'),
('self.avatar.upload', 'Upload Own Avatar', 'self', 'update', 'Upload own avatar image'),
-- ===== EMPLOYEE permissions =====
('employee.view.basic', 'View Employee Basic Info', 'employee', 'read', 'View employee info (excluding salary)'),
('employee.view.salary', 'View Employee Salary', 'employee', 'read', 'View employee salary information'),
('employee.salary.update', 'Update Employee Salary', 'employee', 'update', 'Update employee salary'),
('employee.profile.update', 'Update Employee Profile', 'employee', 'update', 'Update employee profile information'),

-- ===== DEPARTMENT permissions =====
('department.view', 'View Departments', 'department', 'read', 'View department information'),
('department.create', 'Create Department', 'department', 'create', 'Create new departments'),
('department.update', 'Update Department', 'department', 'update', 'Update department information'),
('department.delete', 'Delete Department', 'department', 'delete', 'Delete departments'),
('department.member.add', 'Add Department Member', 'department', 'create', 'Add employee to department'),
('department.member.remove', 'Remove Department Member', 'department', 'delete', 'Remove employee from department'),
('department.member.role.update', 'Update Member Role', 'department', 'update', 'Update member scope role in department'),

-- ===== TEAM permissions =====
('team.view', 'View Teams', 'team', 'read', 'View team information'),
('team.create', 'Create Team', 'team', 'create', 'Create new teams'),
('team.update', 'Update Team', 'team', 'update', 'Update team information'),
('team.delete', 'Delete Team', 'team', 'delete', 'Delete teams'),
('team.member.add', 'Add Team Member', 'team', 'create', 'Add employee to team'),
('team.member.remove', 'Remove Team Member', 'team', 'delete', 'Remove employee from team'),
('team.member.role.update', 'Update Team Member Role', 'team', 'update', 'Update member role in team'),

-- ===== TASK permissions =====
('task.view.self', 'View Own Tasks', 'task', 'read', 'View own tasks'),
('task.update.self', 'Update Own Tasks', 'task', 'update', 'Update own tasks'),
('task.view.department', 'View Department Tasks', 'task', 'read', 'View tasks within department'),
('task.view.team', 'View Team Tasks', 'task', 'read', 'View tasks within team'),
('task.create', 'Create Task', 'task', 'create', 'Create new tasks'),
('task.update', 'Update Task', 'task', 'update', 'Update task information'),
('task.delete', 'Delete Task', 'task', 'delete', 'Delete tasks'),
('task.assign', 'Assign Task', 'task', 'assign', 'Assign tasks to others'),
('task.approve', 'Approve Task', 'task', 'approve', 'Approve/review completed tasks'),

-- ===== REPORT permissions =====
('report.view', 'View Reports', 'report', 'read', 'View reports'),
('report.export', 'Export Reports', 'report', 'export', 'Export reports'),

-- ===== SYSTEM permissions =====
('system.full', 'Full System Access', 'system', 'all', 'Complete system access');

-- =====================================================
-- 3. ROLE-PERMISSION MAPPINGS
-- =====================================================

-- ----------------------------------------------------
-- SYS_ADMIN: Full system access
-- ----------------------------------------------------
INSERT INTO role_permissions (role_code, permission_code)
VALUES ('SYS_ADMIN', 'system.full');

-- ----------------------------------------------------
-- USER: Basic user permissions
-- ----------------------------------------------------
INSERT INTO role_permissions (role_code, permission_code)
VALUES
-- Self management
('USER', 'self.profile.view'),
('USER', 'self.profile.update'),
('USER', 'self.password.update'),
('USER', 'self.avatar.upload'),
-- View others (no salary)
('USER', 'employee.view.basic'),
-- View basic info
('USER', 'department.view'),
('USER', 'team.view'),
('USER', 'task.view.self'),
('USER', 'task.update.self');

-- ----------------------------------------------------
-- DEPT_DIR, DEPT_MGR, TEAM_LEAD, DEPT_MEM, TEAM_MEM: Inherit USER permissions
-- ----------------------------------------------------
INSERT INTO role_permissions (role_code, permission_code)
SELECT target_role, permission_code
FROM role_permissions
         CROSS JOIN (VALUES ('DEPT_DIR'), ('DEPT_MGR'), ('TEAM_LEAD'), ('DEPT_MEM'), ('TEAM_MEM')) AS t(target_role)
WHERE role_code = 'USER';

-- ----------------------------------------------------
-- DEPT_DIR: Department Director permissions
-- ----------------------------------------------------
INSERT INTO role_permissions (role_code, permission_code)
VALUES
-- Department management
('DEPT_DIR', 'department.member.add'),
('DEPT_DIR', 'department.member.remove'),
('DEPT_DIR', 'department.member.role.update'),

-- Employee management in department
('DEPT_DIR', 'employee.view.salary'),        -- View salary of dept members
('DEPT_DIR', 'employee.salary.update'),      -- Update salary
('DEPT_DIR', 'employee.profile.update'),

-- Team management
('DEPT_DIR', 'team.create'),
('DEPT_DIR', 'team.update'),
('DEPT_DIR', 'team.delete'),
('DEPT_DIR', 'team.member.add'),
('DEPT_DIR', 'team.member.remove'),
('DEPT_DIR', 'team.member.role.update'),

-- Task management
('DEPT_DIR', 'task.view.department'),
('DEPT_DIR', 'task.view.team'),
('DEPT_DIR', 'task.create'),
('DEPT_DIR', 'task.update'),
('DEPT_DIR', 'task.delete'),
('DEPT_DIR', 'task.assign'),
('DEPT_DIR', 'task.approve');
-- ----------------------------------------------------
-- DEPT_MGR: Department Manager permissions
-- ----------------------------------------------------
INSERT INTO role_permissions (role_code, permission_code)
VALUES
-- Limited employee viewing (can see salary except DEPT_DIR and other DEPT_MGR)
('DEPT_MGR', 'employee.view.salary'),        -- View with restrictions in business logic

-- Team management
('DEPT_MGR', 'team.member.add'),
('DEPT_MGR', 'team.member.remove'),
('DEPT_MGR', 'team.member.role.update'),

-- Task management
('DEPT_MGR', 'task.view.department'),
('DEPT_MGR', 'task.view.team'),
('DEPT_MGR', 'task.create'),
('DEPT_MGR', 'task.update'),
('DEPT_MGR', 'task.delete'),
('DEPT_MGR', 'task.assign'),
('DEPT_MGR', 'task.approve');

-- ----------------------------------------------------
-- TEAM_LEAD: Team Leader permissions
-- ----------------------------------------------------
INSERT INTO role_permissions (role_code, permission_code)
VALUES
-- Task management (for team scope)
('TEAM_LEAD', 'task.view.team'),
('TEAM_LEAD', 'task.create'),
('TEAM_LEAD', 'task.update'),
('TEAM_LEAD', 'task.delete'),
('TEAM_LEAD', 'task.assign'),
('TEAM_LEAD', 'task.approve');