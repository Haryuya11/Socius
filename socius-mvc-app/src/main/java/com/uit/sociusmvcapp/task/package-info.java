/**
 * Task management module.
 *
 * <p>This module provides comprehensive task management functionality including:
 *
 * <ul>
 *   <li>Task creation, update, deletion (soft delete with cascade)
 *   <li>Sub-task management with parent-child hierarchies
 *   <li>Task workflow: submit for review, approve, reject, cancel, reopen
 *   <li>Task search with flexible filtering and pagination
 *   <li>Activity tracking with complete audit trail
 *   <li>Task status management (in progress, pending, approved, rejected, overdue, cancelled)
 *   <li>Priority levels (low, medium, high)
 *   <li>Team and department assignment
 * </ul>
 *
 * <p>The module follows a layered architecture:
 *
 * <ul>
 *   <li>{@code task.internal.domain} - Domain entities (Task, TaskActivity)
 *   <li>{@code task.internal.persistence} - MyBatis mappers for database operations
 *   <li>{@code task.internal.repository} - Repository layer abstracting persistence
 *   <li>{@code task.internal.service} - Business logic implementation
 *   <li>{@code task.dto} - Data transfer objects for API responses
 *   <li>{@code task.dto.request} - Request objects with validation
 *   <li>{@code task.enums} - Task-specific enumerations
 * </ul>
 *
 * <p>Key features:
 *
 * <ul>
 *   <li>Parent-child task relationships with date validation
 *   <li>Cascade operations for parent deletion/cancellation
 *   <li>Approval workflow requiring all sub-tasks to be approved before parent
 *   <li>Automatic overdue detection based on due dates
 *   <li>Comprehensive activity logging with metadata (notes, old/new values)
 *   <li>Soft delete with cascade to children
 * </ul>
 *
 * @since 1.0
 */
@ApplicationModule(
    displayName = "Task Management",
    allowedDependencies = {"shared", "iam"})
package com.uit.sociusmvcapp.task;

import org.springframework.modulith.ApplicationModule;
