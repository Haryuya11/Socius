/**
 * This package contains team-employee relationship components following the layered architecture
 * pattern.
 *
 * <p>Package structure:
 *
 * <ul>
 *   <li>constants - TeamEmployee-specific constants
 *   <li>converter - Converters between domain objects and DTOs
 *   <li>domain - TeamEmployee domain models
 *   <li>dto - Data Transfer Objects for team-employee data
 *   <li>enums - TeamEmployee-related enumerations
 *   <li>persistence - MyBatis mapper interfaces
 *   <li>repository - Data access layer
 *   <li>request - Request objects for team-employee operations
 *   <li>service - Business logic layer
 * </ul>
 */
@ApplicationModule(
    displayName = "Workforce Module",
    allowedDependencies = {
      "team",
      "employee",
      "department",
      "department :: enums",
      "iam :: enum",
      "iam :: dto",
      "shared",
      "team :: enums",
      "iam",
      "department :: dto",
      "team :: dto",
      "employee :: dto"
    })
package com.uit.sociusmvcapp.workforce;

import org.springframework.modulith.ApplicationModule;
