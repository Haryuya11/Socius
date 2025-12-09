INSERT INTO permissions (permission_code, permission_name, resource, action, description)
VALUES
-- Employee permissions
('employee.view', 'View Employees', 'employee', 'read', 'Can view employee information'),
('employee.create', 'Create Employee', 'employee', 'create', 'Can create new employees'),
('employee.edit', 'Edit Employee', 'employee', 'update', 'Can edit employee information'),
('employee.delete', 'Delete Employee', 'employee', 'delete', 'Can delete employees'),

-- Task permissions
('task.view', 'View Tasks', 'task', 'read', 'Can view tasks'),
('task.create', 'Create Task', 'task', 'create', 'Can create tasks'),
('task.assign', 'Assign Task', 'task', 'update', 'Can assign tasks to others'),
('task.update', 'Update Task', 'task', 'update', 'Can update task status'),
('task.approve', 'Approve Task', 'task', 'approve', 'Can approve completed tasks'),
('task.delete', 'Delete Task', 'task', 'delete', 'Can delete tasks'),

-- Department permissions
('department.view', 'View Departments', 'department', 'read', 'Can view departments'),
('department.create', 'Create Department', 'department', 'create', 'Can create departments'),
('department.edit', 'Edit Department', 'department', 'update', 'Can edit departments'),
('department.delete', 'Delete Department', 'department', 'delete', 'Can delete departments'),

-- Team permissions
('team.view', 'View Teams', 'team', 'read', 'Can view teams'),
('team.create', 'Create Team', 'team', 'create', 'Can create teams'),
('team.edit', 'Edit Team', 'team', 'update', 'Can edit teams'),
('team.delete', 'Delete Team', 'team', 'delete', 'Can delete teams'),

-- Report permissions
('report.view', 'View Reports', 'report', 'read', 'Can view reports'),
('report.export', 'Export Reports', 'report', 'export', 'Can export reports');

-- Insert Roles
INSERT INTO roles (role_code, role_name, role_type, description)
VALUES
-- System roles
('SYS_ADMIN', 'System Administrator', 'SYSTEM', 'Full system access'),
('USER', 'User', 'SYSTEM', 'Basic user access'),

-- Department roles
('DEPT_DIR', 'Department Director', 'DEPARTMENT', 'Department head'),
('DEPT_MGR', 'Department Manager', 'DEPARTMENT', 'Department manager'),
('DEPT_MEM', 'Department Member', 'DEPARTMENT', 'Department member'),

-- Team roles
('TEAM_LEAD', 'Team Leader', 'TEAM', 'Team leader'),
('TEAM_MEM', 'Team Member', 'TEAM', 'Team member');


-- Assign Permissions to Roles

-- SYS_ADMIN: All permissions
INSERT INTO role_permissions (role_code, permission_code)
VALUES ('SYS_ADMIN', 'employee.view'),
       ('SYS_ADMIN', 'employee.create'),
       ('SYS_ADMIN', 'employee.edit'),
       ('SYS_ADMIN', 'employee.delete'),
       ('SYS_ADMIN', 'task.view'),
       ('SYS_ADMIN', 'task.create'),
       ('SYS_ADMIN', 'task.assign'),
       ('SYS_ADMIN', 'task.update'),
       ('SYS_ADMIN', 'task.approve'),
       ('SYS_ADMIN', 'task.delete'),
       ('SYS_ADMIN', 'department.view'),
       ('SYS_ADMIN', 'department.create'),
       ('SYS_ADMIN', 'department.edit'),
       ('SYS_ADMIN', 'department.delete'),
       ('SYS_ADMIN', 'team.view'),
       ('SYS_ADMIN', 'team.create'),
       ('SYS_ADMIN', 'team.edit'),
       ('SYS_ADMIN', 'team.delete'),
       ('SYS_ADMIN', 'report.view'),
       ('SYS_ADMIN', 'report.export');

-- DEPT_DIR: Department director permissions
INSERT INTO role_permissions (role_code, permission_code)
VALUES ('DEPT_DIR', 'employee.view'),
       ('DEPT_DIR', 'employee.edit'),
       ('DEPT_DIR', 'task.view'),
       ('DEPT_DIR', 'task.create'),
       ('DEPT_DIR', 'task.assign'),
       ('DEPT_DIR', 'task.approve'),
       ('DEPT_DIR', 'task.update'),
       ('DEPT_DIR', 'department.view'),
       ('DEPT_DIR', 'team.view'),
       ('DEPT_DIR', 'team.create'),
       ('DEPT_DIR', 'team.edit'),
       ('DEPT_DIR', 'report.view'),
       ('DEPT_DIR', 'report.export');

-- DEPT_MGR: Department manager permissions
INSERT INTO role_permissions (role_code, permission_code)
VALUES ('DEPT_MGR', 'employee.view'),
       ('DEPT_MGR', 'task.view'),
       ('DEPT_MGR', 'task.create'),
       ('DEPT_MGR', 'task.assign'),
       ('DEPT_MGR', 'task.approve'),
       ('DEPT_MGR', 'department.view'),
       ('DEPT_MGR', 'team.view'),
       ('DEPT_MGR', 'report.view');

-- DEPT_MEM: Department member permissions
INSERT INTO role_permissions (role_code, permission_code)
VALUES ('DEPT_MEM', 'employee.view'),
       ('DEPT_MEM', 'task.view'),
       ('DEPT_MEM', 'task.update'),
       ('DEPT_MEM', 'department.view'),
       ('DEPT_MEM', 'team.view');

-- TEAM_LEAD: Team leader permissions
INSERT INTO role_permissions (role_code, permission_code)
VALUES ('TEAM_LEAD', 'employee.view'),
       ('TEAM_LEAD', 'task.view'),
       ('TEAM_LEAD', 'task.create'),
       ('TEAM_LEAD', 'task.assign'),
       ('TEAM_LEAD', 'task.update'),
       ('TEAM_LEAD', 'team.view'),
       ('TEAM_LEAD', 'report.view');

-- TEAM_MEM: Team member permissions
INSERT INTO role_permissions (role_code, permission_code)
VALUES ('TEAM_MEM', 'task.view'),
       ('TEAM_MEM', 'task.update'),
       ('TEAM_MEM', 'team.view');

-- USER: Basic user permissions
INSERT INTO role_permissions (role_code, permission_code)
VALUES ('USER', 'employee.view'),
       ('USER', 'task.view'),
       ('USER', 'task.update');
