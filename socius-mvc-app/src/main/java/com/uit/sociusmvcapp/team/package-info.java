/**
 * This package contains team-related components following the layered architecture pattern.
 *
 * <p>Package structure:
 *
 * <ul>
 *   <li>constants - Team-specific constants
 *   <li>converter - Converters between domain objects and DTOs
 *   <li>domain - Team domain models
 *   <li>dto - Data Transfer Objects for team data
 *   <li>enums - Team-related enumerations
 *   <li>persistence - MyBatis mapper interfaces
 *   <li>repository - Data access layer
 *   <li>request - Request objects for team operations
 *   <li>service - Business logic layer
 * </ul>
 */
@ApplicationModule(
    displayName = "Employee Team Management",
    allowedDependencies = {"shared", "iam"})
package com.uit.sociusmvcapp.team;

import org.springframework.modulith.ApplicationModule;
