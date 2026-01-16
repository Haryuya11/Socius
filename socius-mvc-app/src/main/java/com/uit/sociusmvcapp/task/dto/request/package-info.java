/**
 * Request objects for Task module API endpoints.
 *
 * <p>This package contains request objects with Jakarta validation annotations for:
 *
 * <ul>
 *   <li>{@link com.uit.sociusmvcapp.task.dto.request.CreateTaskRequest} - Create new task
 *   <li>{@link com.uit.sociusmvcapp.task.dto.request.UpdateTaskRequest} - Update existing task
 *   <li>{@link com.uit.sociusmvcapp.task.dto.request.SearchTaskRequest} - Search tasks with filters
 *   <li>{@link com.uit.sociusmvcapp.task.dto.request.SubmitReviewRequest} - Submit task for review
 *   <li>{@link com.uit.sociusmvcapp.task.dto.request.ApproveTaskRequest} - Approve task
 *   <li>{@link com.uit.sociusmvcapp.task.dto.request.RejectTaskRequest} - Reject task
 *   <li>{@link com.uit.sociusmvcapp.task.dto.request.CancelTaskRequest} - Cancel task
 * </ul>
 *
 * <p>All request objects use Jakarta Bean Validation annotations (@NotNull, @NotBlank, etc.) to
 * ensure data integrity at the API boundary.
 *
 * @since 1.0
 */
package com.uit.sociusmvcapp.task.dto.request;
