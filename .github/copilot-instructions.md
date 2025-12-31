# Socius Project - Copilot Agent Instructions

## Repository Overview

**Socius** is a Spring Boot-based employee management system designed for workforce, team, department, and notification management with Azure Active Directory integration. The application follows Spring Modulith architectural patterns for a well-organized modular monolith.

### Key Technologies

- **Java 17** - Programming language (OpenJDK 17.0.17)
- **Spring Boot 3.5.6** - Application framework
- **Spring Modulith 1.4.3** - Modular monolith architecture  
- **MyBatis 3.0.5** - SQL mapping and persistence
- **MapStruct 1.5.5.Final** - Object mapping
- **Spring MVC** - Web framework (RESTful APIs)
- **PostgreSQL 42.7.8** - Database
- **RabbitMQ** - Message broker for async messaging
- **Azure Entra ID** - Authentication/authorization provider
- **Microsoft Graph API 6.54.0** - User management integration
- **Azure Blob Storage 12.32.0** - File storage
- **Azure Key Vault** - Secret management
- **Lombok 1.18.30** - Boilerplate code reduction
- **Gradle 8.14** - Build tool

### Repository Statistics

- **Size**: ~130 MB (with dependencies)
- **Java Files**: 188 source files (~9,824 lines of code)
- **Single Module**: `socius-mvc-app` (monolithic application)
- **Package Organization**: Modular packages by domain (workforce, team, department, employee, notification, iam, shared)

---

## Project Structure

### Module: `socius-mvc-app`

**Location**: `/socius-mvc-app`  
**Main Class**: `com.uit.sociusmvcapp.SociusMvcAppApplication`  
**Application Type**: Spring Boot web application (blocking I/O, traditional MVC)

**Server Configuration**:
- Port: 8080
- Context Path: `/api`
- Actuator endpoints: `/api/actuator/health`, `/api/actuator/info`, `/api/actuator/metrics`

### Domain Modules

Located in `socius-mvc-app/src/main/java/com/uit/sociusmvcapp/`:

1. **`workforce/`** - Workforce management (employee-team/department assignments)
2. **`team/`** - Team domain (creation, management, operations)
3. **`department/`** - Department domain (operations, hierarchies)
4. **`employee/`** - Employee management (Azure AD sync, user profiles)
5. **`notification/`** - Notification system (RabbitMQ-based async notifications)
6. **`iam/`** - Identity and Access Management (roles, permissions, RBAC)
7. **`shared/`** - Shared utilities, exceptions, configurations, filters

### Package Structure Pattern

Each domain module follows this internal structure:
```
[module]/
├── dto/                    # Public data transfer objects
│   └── request/            # Request DTOs for API endpoints
├── *Service.java           # Public service interface(s)
└── internal/               # Implementation details (internal visibility)
    ├── converter/          # MapStruct converters
    ├── repository/         # Repository interfaces (business layer)
    ├── persistence/        # MyBatis mapper interfaces
    ├── service/            # Service implementations
    ├── domain/             # Domain models/entities
    ├── constants/          # Module constants
    ├── component/          # Spring components
    └── listener/           # Event listeners
```

### Key Configuration Files

- **Build**: `build.gradle` (root), `socius-mvc-app/build.gradle`
- **Settings**: `settings.gradle`
- **Dependencies**: `gradle/libs.versions.toml` (version catalog)
- **Checkstyle**: `config/checkstyle/google_checks.xml`, `config/checkstyle/checkstyle-suppressions.xml`
- **Application Config**: `socius-mvc-app/src/main/resources/application.properties`
- **MyBatis Mappers**: `socius-mvc-app/src/main/resources/mappers/*.xml` (8 mapper files)
- **Logging**: `socius-mvc-app/src/main/resources/log4j2.xml`
- **i18n**: `socius-mvc-app/src/main/resources/i18n/messages*.properties`

---

## Build & Development Commands

### Critical Pre-Build Step

**ALWAYS run `./gradlew spotlessApply` before building** if you've made any code changes. The build will fail on checkstyle violations otherwise.

### Validated Build Sequence

#### 1. Format Code (Required Before Building)
```bash
# Fix code formatting automatically
./gradlew spotlessApply

# Fixes: import order, trailing whitespace, unused imports, newlines
# Takes: ~4-5 seconds
```

#### 2. Build Application
```bash
# Recommended: Build without tests (tests require full environment)
./gradlew clean build -x test

# Full build with tests (requires database, Azure, RabbitMQ)
./gradlew clean build

# Fast build with caching
./gradlew clean build -x test --parallel --build-cache

# Build only (no clean)
./gradlew build -x test
```

**Build Performance**:
- Clean build without tests: ~25-30 seconds
- With parallel/cache: ~20-25 seconds
- Full build with tests: 45-60 seconds (requires environment setup)

**Build Order** (as executed by Gradle):
1. `compileJava` - Compile source code (~15-20 seconds with Lombok/MapStruct processors)
2. `processResources` - Copy resources
3. `classes` - Assemble main classes
4. `bootJar` - Create executable JAR
5. `jar` - Create standard JAR
6. `checkstyleMain` - Run checkstyle on main code
7. `compileTestJava` - Compile test code
8. `checkstyleTest` - Run checkstyle on test code
9. `assemble`, `check`, `build` - Aggregate tasks

#### 3. Code Quality Checks
```bash
# Check formatting (runs automatically with build)
./gradlew spotlessCheck

# Run checkstyle manually
./gradlew checkstyleMain checkstyleTest

# View checkstyle report
cat socius-mvc-app/build/reports/checkstyle/main.html
```

**Spotless Configuration**:
- Uses Google Java Format
- Auto-removes unused imports
- Trims trailing whitespace
- Ensures newline at end of file

**Checkstyle Configuration**:
- Based on Google Java Style Guide (`config/checkstyle/google_checks.xml`)
- Custom suppressions in `config/checkstyle/checkstyle-suppressions.xml`
- **Max warnings: 0** (build fails on any warning)
- Common violations: import order, extra import separations

#### 4. Testing
```bash
# Run all tests
./gradlew test

# Skip tests (recommended for code validation)
./gradlew build -x test
```

**⚠️ Important: Test Environment Requirements**

Tests require full environment configuration and will fail without:
- PostgreSQL database connection
- Azure Active Directory credentials
- RabbitMQ server
- Azure Blob Storage
- Azure Key Vault

**Expected test failure without environment**:
```
ConfigurationPropertiesBindException
→ caused by ConversionFailedException
→ caused by IllegalArgumentException
```

**Solution**: Always use `-x test` flag when validating code changes unless you have a fully configured environment.

#### 5. Running the Application
```bash
# Run application with Gradle
./gradlew :socius-mvc-app:bootRun

# Build executable JAR
./gradlew :socius-mvc-app:bootJar

# Run JAR directly
java -jar socius-mvc-app/build/libs/socius-mvc-app-1.0-SNAPSHOT.jar
```

**Required Environment Variables** (see `application.properties` for full list):
- **Database**: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DB_SCHEMA`
- **Azure AD**: `AZURE_TENANT_ID`, `AZURE_CLIENT_ID`, `AZURE_CLIENT_SECRET`, `AZURE_PRIMARY_DOMAIN`
- **Azure Storage**: `AZURE_BLOB_CONNECTION_STRING`, `AZURE_BLOB_USER_CONTAINER_NAME`
- **RabbitMQ**: `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD`, `RABBITMQ_VHOST`, `RABBITMQ_SSL`, `RABBITMQ_EXCHANGE_NAME`, `NOTIFICATION_ROUTING_KEY`
- **Key Vault**: `KEY_VAULT_ENDPOINT`

---

## Common Build Issues & Solutions

### Issue 1: Checkstyle Violations After Code Changes

**Symptoms**:
```
[WARN] Wrong lexicographical order for imports
[WARN] Extra separation in import group
Task :socius-mvc-app:checkstyleMain FAILED
```

**Solution**:
```bash
./gradlew spotlessApply
./gradlew build -x test
```

**Prevention**: Always run `spotlessApply` before committing code.

### Issue 2: Lombok/MapStruct Compilation Errors

**Symptoms**: "Cannot find symbol" for getters/setters, or mapper implementations not found

**Root Cause**: Annotation processors not executed or IDE not configured

**Solution**:
```bash
# Clean and rebuild
./gradlew clean build -x test

# Verify annotation processors in build.gradle:
# - compileOnly libs.lombok
# - annotationProcessor libs.lombok
# - implementation libs.mapstruct
# - annotationProcessor libs.mapstruct.processor
```

### Issue 3: Tests Fail with Configuration Errors

**Symptoms**: `ConfigurationPropertiesBindException`, `UnsatisfiedDependencyException`

**Root Cause**: Missing environment variables for external services

**Solution**: Use `-x test` flag or set up full test environment

### Issue 4: Gradle Daemon Issues

**Symptoms**: Build hangs or shows stale results

**Solution**:
```bash
./gradlew --stop
./gradlew clean build -x test
```

---

## Code Quality Guidelines

### 1. MyBatis SQL Injection Prevention (CRITICAL)

**Always use `#{}` for parameters, never `${}`**

```xml
<!-- ✅ CORRECT: Safe parameterized query -->
<select id="findById" resultType="Employee">
  SELECT * FROM employees WHERE id = #{id}
</select>

<!-- ❌ WRONG: SQL injection vulnerability -->
<select id="findById" resultType="Employee">
  SELECT * FROM employees WHERE id = ${id}
</select>
```

**When to use each**:
- `#{}` - All user inputs, parameters (99% of cases)
- `${}` - Only for column/table names from static enums (avoid if possible)

**Mapper XML Locations**: `socius-mvc-app/src/main/resources/mappers/*.xml`

### 2. MapStruct Best Practices

**Always use MapStruct for conversions** (never manual mapping):

```java
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmployeeConverter {
    EmployeeDto toDto(Employee entity);
    Employee toEntity(EmployeeDto dto);
    
    @Mapping(source = "userId", target = "user.id")
    EmployeeDto toDtoWithMapping(Employee entity);
}
```

**Key requirements**:
- `componentModel = "spring"` - Required for Spring injection
- `unmappedTargetPolicy` - Explicitly set to `IGNORE` or `ERROR`
- Use `@Mapping` for non-standard field mappings

### 3. Package Organization Rules

**Public API** (top-level package):
- DTOs, service interfaces, enums
- Exposed to other modules

**Internal** (`internal/` subpackage):
- Implementations, converters, repositories
- Not exposed to other modules

**Shared** (`shared/` package):
- Cross-cutting concerns only
- Utilities, exceptions, filters, configs

### 4. Naming Conventions

- **Classes**: `PascalCase` (e.g., `EmployeeService`, `TeamDto`)
- **Methods**: `camelCase`, verb-first (e.g., `findById`, `createEmployee`, `validateInput`)
- **Variables**: `camelCase`, descriptive (e.g., `employeeList`, `currentUser`)
- **Constants**: `UPPER_SNAKE_CASE` (e.g., `DEFAULT_PAGE_SIZE`, `MAX_RETRY_ATTEMPTS`)
- **Packages**: lowercase, single word preferred

### 5. Code Complexity Limits

- **Method length**: Target < 20 lines, refactor if > 50 lines
- **Class length**: Consider splitting if > 300 lines
- **Nesting depth**: Maximum 3 levels of conditionals
- **Cyclomatic complexity**: Keep below 10 per method
- **Parameters**: Maximum 5 parameters per method

---

## GitHub Workflow

### Deployment Workflow (`main.yml`)

**Location**: `.github/workflows/main.yml`

**Trigger**: Manual (`workflow_dispatch`) with environment selection

**Build Command Used in CI**:
```bash
./gradlew clean bootJar -x test --parallel --build-cache
```

**Steps**:
1. Validate deployment confirmation
2. Pre-deployment checks (disk space, Java version)
3. Build application with Gradle
4. Prepare JAR artifacts (excludes `*-plain.jar`)
5. Upload artifacts
6. Transfer to Azure VM
7. Deploy with automatic rollback on failure
8. Record deployment history

**Important**: CI uses `bootJar` task (not `build`) to create executable JAR only.

---

## Security Considerations

### Critical Security Checks

1. **SQL Injection** - Verify MyBatis uses `#{}` not `${}`
2. **Sensitive Data** - No passwords, tokens, API keys in logs or source code
3. **Input Validation** - All user inputs validated before processing
4. **Authentication** - Verify security filters applied to protected endpoints
5. **Azure Tokens** - Proper validation and expiration handling

### Sensitive Files (Never Commit)

- `.env` files
- `application-local.properties`
- Any file containing credentials or API keys

### Secret Management

- Use environment variables for all secrets
- Azure Key Vault for production secrets
- Never hardcode credentials

---

## Working with This Codebase

### Before Making Changes

1. Understand the domain module structure
2. Locate the relevant package (workforce, team, department, etc.)
3. Check existing patterns in similar files
4. Review MyBatis mappers if persistence changes needed

### Making Code Changes

1. Make minimal changes to accomplish goal
2. Run `./gradlew spotlessApply` after editing
3. Build to verify: `./gradlew build -x test`
4. Check for checkstyle/spotless violations
5. Review changes with `git diff`

### Common Tasks

**Add new endpoint**:
1. Create/update DTO in `[module]/dto/`
2. Add/update service interface in `[module]/`
3. Implement in `[module]/internal/service/`
4. Add controller method (if needed)

**Add database query**:
1. Add method to MyBatis mapper interface in `[module]/internal/persistence/`
2. Add SQL query to corresponding XML in `resources/mappers/`
3. Use `#{}` for all parameters
4. Add integration test if possible

**Add domain object conversion**:
1. Create/update MapStruct converter in `[module]/internal/converter/`
2. Use `@Mapper(componentModel = "spring")`
3. Let MapStruct auto-generate implementation

### Trust These Instructions

These instructions were created through comprehensive repository exploration and validated by:
- Running actual build commands
- Testing formatting tools
- Examining all configuration files
- Reviewing package structure
- Analyzing GitHub workflows

**Only perform additional searches if**:
- Information is incomplete for your specific task
- You find contradictions in these instructions
- The codebase has changed significantly since these were written

Otherwise, trust these instructions and proceed with your task efficiently.
