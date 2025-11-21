# Socius Project - Copilot Agent Instructions

## Repository Overview

**Socius** is a Spring Boot-based employee management system with dual architecture support: traditional Spring MVC and reactive Spring WebFlux. The project follows a modular design pattern with shared core business logic and separate application layers for different deployment scenarios.

### Key Technologies

- **Java 17** - Programming language
- **Spring Boot 3.5.6** - Application framework
- **Spring Modulith 1.4.3** - Modular monolith architecture
- **MyBatis 3.0.x** - SQL mapping framework
- **MapStruct 1.5.5.Final** - Object mapping
- **Spring WebFlux** - Reactive programming
- **Spring MVC** - Traditional web framework
- **PostgreSQL** - Database
- **RabbitMQ** - Message broker
- **Azure Entra ID** - Authentication provider
- **Microsoft Graph API** - User management
- **Lombok** - Code generation
- **Gradle 8.14** - Build tool

---

## Module Architecture

The project is organized into three main modules:

### 1. `socius-core-modules` (Shared Core)

This is a **Java library module** (not a runnable application) containing shared business logic, domain models, and infrastructure code.

**Key packages:**
- `employee` - Employee management, Azure AD integration
- `notification` - Notification system with reactive support
- `shared` - Common utilities, exceptions, configurations

**Important components:**
- **MyBatis Mappers**: `EmployeeMapper`, `NotificationMapper` (interfaces + XML)
- **MapStruct Converters**: `EmployeeConverter`, `NotificationConverter`
- **Domain Services**: Business logic implementations
- **Repositories**: Data access layer

### 2. `socius-mvc-app` (Spring MVC Application)

Traditional servlet-based web application using blocking I/O.

**Key features:**
- REST controllers in `endpoint` package
- Servlet-based security filter
- Synchronous request handling
- Traditional thread-per-request model

### 3. `socius-webflux-app` (Reactive Application)

Reactive, non-blocking application using Spring WebFlux.

**Key features:**
- WebSocket support for real-time notifications
- Reactive security with `ReactiveAuthorizationFilter`
- Non-blocking handlers using `Flux` and `Mono`
- Concurrent map management with `Sinks` for broadcasting

**Critical reactive components:**
- `NotificationBroadcaster` - Manages WebSocket sinks with `ConcurrentHashMap`
- `NotificationWebSocketHandler` - Handles WebSocket connections reactively

---

## PR Review Focus Areas

When reviewing pull requests, prioritize these areas in order:

### 1. Bugs & Correctness (HIGHEST PRIORITY)

#### MyBatis Security Issues
- **CRITICAL**: Never use `${}` for parameter substitution in MyBatis XML files
  - Use `#{}` for safe parameterized queries
  - `${}` allows SQL injection - only acceptable for static enum values or constants
  - Example: `WHERE id = #{userId}` ✅ vs `WHERE id = ${userId}` ❌
- Check all `.xml` mapper files in `src/main/resources/mappers/`

#### Reactive Programming Errors (WebFlux only)
- **Never call `.block()` in reactive handlers** - this defeats non-blocking architecture
- Check for blocking operations in:
  - WebSocket handlers
  - Reactive controllers
  - Any method returning `Mono<T>` or `Flux<T>`
- **Race conditions** with `Sinks`:
  - Verify proper use of `tryEmitNext()` instead of `emitNext()`
  - Check for thread-safe operations on `ConcurrentHashMap` in `NotificationBroadcaster`
  - Ensure proper cleanup in `doFinally()` blocks
- **Null pointer risks** in reactive chains:
  - Always use `switchIfEmpty()` or provide default values
  - Check `flatMap()` chains for null safety

#### MapStruct Mapping Issues
- Verify mappers are properly injected (Spring component model)
- Check for manual field mapping that should use MapStruct
- Look for forgotten `@Mapper(componentModel = "spring")` annotation
- Verify complex mappings use `@Mapping` annotations correctly

#### Thread Safety
- Check concurrent access to shared data structures
- Verify proper synchronization in `NotificationBroadcaster`
- Look for race conditions in sink management

### 2. Code Complexity

#### Cognitive Complexity
Identify methods with:
- **Deeply nested conditionals** (more than 3 levels)
- **Multiple early returns** without clear pattern
- **Long method chains** that are hard to follow
- **Mixed concerns** in single method

**Refactoring suggestions:**
- Extract nested logic into helper methods
- Use early returns to reduce nesting
- Apply guard clauses for validation
- Split large methods following Single Responsibility Principle

#### Cyclomatic Complexity
Flag methods with:
- **Many conditional branches** (if/else, switch)
- **Multiple loop constructs**
- **Complex boolean expressions**

**Simplification strategies:**
- Replace nested if/else with switch expressions or strategy pattern
- Extract conditional logic into well-named predicates
- Use streams for collection operations instead of loops

### 3. Readability & Clean Code

#### Naming Conventions
- **Classes**: PascalCase, descriptive nouns (e.g., `EmployeeService`, `NotificationDto`)
- **Methods**: camelCase, verb phrases (e.g., `findByClientId`, `createUserProfile`)
- **Variables**: camelCase, meaningful names (avoid single letters except loop counters)
- **Constants**: UPPER_SNAKE_CASE (e.g., `DEFAULT_PASSWORD`, `BUFFER_SIZE`)

#### Code Organization
- **Package structure** must respect module boundaries:
  - Core modules: domain logic only, no web/reactive specifics
  - MVC app: servlet-based components only
  - WebFlux app: reactive components only
- **Method size**: Aim for < 20 lines; refactor if > 50 lines
- **Class size**: Consider splitting classes > 300 lines
- **Remove dead code**: Unused methods, commented code, redundant variables

#### Documentation
- **JavaDoc** required for:
  - All public APIs
  - MapStruct converters with complex mappings
  - MyBatis mapper interfaces
- **Comments** should explain "why", not "what"
- Update docs when changing behavior

### 4. Best Practices

#### Module Boundaries
- **Core modules** must not import MVC or WebFlux specific classes
- **No reactive types** (Mono/Flux) in core if used by MVC app
- **Shared utilities** belong in `socius-core-modules/shared`

#### MapStruct Usage
- Always use MapStruct for DTO ↔ Domain ↔ Entity conversions
- Avoid manual mapping with getters/setters
- Use `@Mapping` for non-standard field mappings
- Set `unmappedTargetPolicy = ReportingPolicy.IGNORE` or `ERROR` explicitly

#### MyBatis Best Practices
- Use `#{}` for parameters (prevents SQL injection)
- Define `resultMap` for complex object mapping
- Avoid N+1 queries - use joins or batch fetching
- Keep SQL in XML files, not annotations

#### Reactive Best Practices (WebFlux only)
- Chain operations instead of blocking
- Use `subscribeOn()` and `publishOn()` for thread control
- Properly handle backpressure with `onBackpressureBuffer()` or `onBackpressureDrop()`
- Always test reactive flows with `StepVerifier`

---

## Commit Guidelines

Before creating a PR:

### Squash Commits
- Combine related commits into logical units
- Each commit should represent one complete change

### Commit Message Format
```
<type>: <short summary> (max 72 chars)

<optional detailed description>

<optional references to issues/tickets>
```

**Types:**
- `feat:` - New feature
- `fix:` - Bug fix
- `refactor:` - Code restructuring without behavior change
- `perf:` - Performance improvement
- `docs:` - Documentation only
- `style:` - Code style/formatting (Spotless)
- `test:` - Adding or updating tests
- `chore:` - Build, dependencies, tooling

**Good examples:**
```
feat: Add WebSocket notification broadcasting

Implements real-time notification delivery using Spring WebFlux
WebSocket with per-user sinks managed in ConcurrentHashMap.
```

```
fix: Prevent SQL injection in EmployeeMapper

Changed ${userId} to #{userId} in findByUserId query.
```

**Bad examples:**
```
Update files  ❌ (too vague)
```
```
Fixed bug in employee service and added notification feature and updated dependencies  ❌ (should be 3 commits)
```

---

## Build & Development Commands

### Build
```bash
# Clean and build all modules
./gradlew clean build

# Build without tests (faster)
./gradlew clean build -x test

# Build specific module
./gradlew :socius-core-modules:build
./gradlew :socius-mvc-app:build
./gradlew :socius-webflux-app:build
```

### Code Quality

#### Spotless (Code Formatting)
```bash
# Check formatting
./gradlew spotlessCheck

# Auto-fix formatting
./gradlew spotlessApply
```

**Configuration:**
- Uses Google Java Format
- Auto-removes unused imports
- Trims trailing whitespace
- Ensures newline at end of file

#### Checkstyle
```bash
# Run checkstyle (runs automatically with build)
./gradlew checkstyleMain
./gradlew checkstyleTest
```

**Configuration:**
- Based on `config/checkstyle/google_checks.xml`
- Custom suppressions in `config/checkstyle/checkstyle-suppressions.xml`
- Max warnings: 0 (build fails on any warning)

### Testing
```bash
# Run all tests
./gradlew test

# Run tests for specific module
./gradlew :socius-core-modules:test

# Run tests with coverage (if configured)
./gradlew test jacocoTestReport
```

### Running Applications

#### MVC Application
```bash
./gradlew :socius-mvc-app:bootRun
```

#### WebFlux Application
```bash
./gradlew :socius-webflux-app:bootRun
```

**Environment setup:**
- Create `.env` file in project root (see `.env.example` if available)
- Required: Database connection, Azure AD credentials, RabbitMQ config

---

## Search & Analysis Strategy

### PR Review Scope
1. **Always start with files changed in the PR**
2. **Check related files only when necessary:**
   - Mapper XML when Java mapper interface changes
   - Service implementations when interfaces change
   - Tests when implementation changes
   - Converters when DTOs or domain models change

3. **Avoid unnecessary project-wide searches unless:**
   - Detecting cross-module impact (e.g., shared utility change)
   - Finding all usages of deprecated method
   - Verifying consistent pattern across codebase

### File Patterns to Check

When reviewing changes in:
- **`*Mapper.java`** → Check `src/main/resources/mappers/*Mapper.xml`
- **`*Converter.java`** → Verify DTO/Domain/Entity classes
- **Reactive handlers** → Check for blocking calls, proper Flux/Mono usage
- **Service classes** → Verify transaction boundaries, error handling
- **Controllers** → Check request validation, response formatting

---

## Common Issues & Solutions

### Build Failures

#### Checkstyle Violations
```bash
# View detailed report
cat build/reports/checkstyle/main.html

# Auto-fix most issues
./gradlew spotlessApply
```

#### Spotless Failures
```bash
# Fix formatting
./gradlew spotlessApply

# Then rebuild
./gradlew build
```

#### Compilation Errors
- Check for missing dependencies in `build.gradle`
- Verify Lombok and MapStruct annotation processors are configured
- Ensure Java 17 toolchain is available

### Test Failures
- Check for environment-specific configuration in test properties
- Verify test database setup (H2/PostgreSQL)
- Check for hardcoded values that need mocking

---

## Security Considerations

### Always Review For:

1. **SQL Injection** - Verify MyBatis uses `#{}` not `${}`
2. **Sensitive Data Logging** - No passwords, tokens, or PII in logs
3. **Input Validation** - All user input must be validated
4. **Authentication Bypass** - Verify security filters are applied
5. **Azure AD Token Handling** - Proper validation and expiration checks

### Sensitive Files
- Never commit `.env` files
- Keep secrets in environment variables or Azure Key Vault
- Review changes to security configurations carefully

---

## Operating Principles Summary

1. **Safety First** - Always prioritize correctness over feature delivery
2. **Minimal Changes** - Only modify what's necessary for the PR goal
3. **Respect Boundaries** - Maintain module separation (core, mvc, webflux)
4. **Test Before Approve** - Verify build, tests, and formatting pass
5. **Clear Communication** - Provide specific, actionable feedback
6. **Trust but Verify** - Follow these instructions; search only when gaps exist

---

## Quick Reference Checklist

Use this for every PR review:

- [ ] Build passes: `./gradlew clean build`
- [ ] Formatting correct: `./gradlew spotlessCheck`
- [ ] Tests pass: `./gradlew test`
- [ ] No SQL injection risks (MyBatis `#{}` vs `${}`)
- [ ] No blocking calls in reactive code (`.block()`)
- [ ] MapStruct used for conversions (not manual mapping)
- [ ] Thread safety verified (especially sinks in WebFlux)
- [ ] Complexity acceptable (methods < 50 lines, clear logic)
- [ ] Naming consistent and clear
- [ ] Module boundaries respected
- [ ] Commit messages follow format
- [ ] No sensitive data exposed
- [ ] Documentation updated if needed

---

## When You Need Help

If you encounter situations not covered in these instructions:

1. **Check existing code** for similar patterns
2. **Review Spring Boot/WebFlux documentation** for framework-specific questions
3. **Consult MyBatis docs** for mapper configuration issues
4. **Ask the user** if business logic context is unclear

**Focus areas where human judgment is needed:**
- Business logic correctness
- Performance implications of architectural changes
- Trade-offs between different implementation approaches
- Product requirements and feature specifications
