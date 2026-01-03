# Socius - Employee Management System

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Modulith](https://img.shields.io/badge/Spring%20Modulith-1.4.3-blue.svg)](https://spring.io/projects/spring-modulith)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Socius is a modern, enterprise-grade employee management system built with Spring Boot and Spring Modulith architecture. It provides comprehensive workforce management capabilities with seamless Azure Active Directory integration, team and department management, and real-time notifications.

## 🚀 Key Features

### Core Capabilities

- **Employee Management**: Complete employee lifecycle management with Azure AD synchronization
- **Team Management**: Create and manage teams with dynamic member assignments
- **Department Management**: Hierarchical department structures and employee assignments
- **Workforce Management**: Advanced employee-team and employee-department relationship management
- **Identity & Access Management (IAM)**: Role-based access control (RBAC) with fine-grained permissions
- **Notification System**: Asynchronous notification delivery via RabbitMQ

### Integration & Security

- **Azure Active Directory**: Full Azure AD/Entra ID integration for authentication and user management
- **Microsoft Graph API**: Seamless user profile synchronization and management
- **Azure Blob Storage**: Secure file storage and management for user avatars and documents
- **Azure Key Vault**: Secure secret management for production environments
- **OAuth 2.0 & JWT**: Industry-standard security with JWT token validation

### Additional Features

- **RESTful API**: Comprehensive REST API with standardized response formats
- **Internationalization (i18n)**: Multi-language support with English as default
- **Health Monitoring**: Spring Boot Actuator endpoints for application health and metrics
- **Asynchronous Processing**: Event-driven architecture with async operations
- **Retry Mechanisms**: Built-in retry logic for resilient operations

## 📋 Table of Contents

- [Technology Stack](#-technology-stack)
- [Architecture](#-architecture)
- [Prerequisites](#-prerequisites)
- [Getting Started](#-getting-started)
- [Configuration](#-configuration)
- [Building the Project](#-building-the-project)
- [Running the Application](#-running-the-application)
- [API Documentation](#-api-documentation)
- [Project Structure](#-project-structure)
- [Development](#-development)
- [Deployment](#-deployment)
- [Contributing](#-contributing)
- [License](#-license)

## 🛠 Technology Stack

### Core Framework & Language

- **Java 17** - Programming language (OpenJDK 17.0.17)
- **Spring Boot 3.5.6** - Application framework
- **Spring Modulith 1.4.3** - Modular monolith architecture pattern
- **Spring MVC** - RESTful API framework

### Database & Persistence

- **PostgreSQL 42.7.8** - Relational database
- **MyBatis 3.0.5** - SQL mapping framework
- **HikariCP** - High-performance JDBC connection pool

### Cloud & Integration

- **Azure Active Directory (Entra ID)** - Authentication and authorization
- **Microsoft Graph API 6.54.0** - User management and Azure AD integration
- **Azure Blob Storage 12.32.0** - Cloud file storage
- **Azure Key Vault** - Secret management
- **RabbitMQ** - Message broker for asynchronous messaging

### Security

- **Spring Security** - Security framework
- **OAuth 2.0 Resource Server** - JWT token validation
- **Nimbus JOSE JWT 10.5** - JWT processing

### Utilities & Tools

- **MapStruct 1.5.5.Final** - Java bean mapping
- **Lombok 1.18.30** - Boilerplate code reduction
- **Apache Commons Lang3 3.19.0** - Common utilities
- **Gradle 8.14** - Build automation tool
- **Spotless** - Code formatting
- **Checkstyle** - Code quality checks (Google Java Style Guide)

### Monitoring & Logging

- **Spring Boot Actuator** - Application monitoring and management
- **Log4j2** - Logging framework

## 🏗 Architecture

Socius follows the **Spring Modulith** architectural pattern, organizing the application into well-defined, loosely coupled modules. This approach provides the benefits of a modular monolith while maintaining simplicity.

### Modular Design

The application is organized into the following domain modules:

```
socius-mvc-app/
├── employee/          # Employee domain - User management and profiles
├── team/              # Team domain - Team creation and management
├── department/        # Department domain - Department operations
├── workforce/         # Workforce domain - Employee-team/department assignments
├── iam/               # Identity & Access Management - Roles and permissions
├── notification/      # Notification domain - Asynchronous notifications
├── azure/             # Azure integration - Blob storage and Graph API
└── shared/            # Shared utilities and cross-cutting concerns
```

### Module Structure

Each domain module follows a consistent internal structure:

```
[module]/
├── dto/                    # Public data transfer objects
│   └── request/            # Request DTOs for API endpoints
├── *Service.java           # Public service interface(s)
├── *Controller.java        # REST API endpoints (when applicable)
└── internal/               # Implementation details (encapsulated)
    ├── converter/          # MapStruct converters
    ├── repository/         # Repository interfaces
    ├── persistence/        # MyBatis mapper interfaces
    ├── service/            # Service implementations
    ├── domain/             # Domain models/entities
    ├── component/          # Spring components
    └── listener/           # Event listeners
```

### Key Architectural Principles

- **Modularity**: Clear boundaries between domain modules
- **Encapsulation**: Internal implementation details hidden from other modules
- **Event-Driven**: Asynchronous communication via domain events
- **Separation of Concerns**: Each module focuses on a specific business domain
- **Security by Default**: OAuth 2.0 authentication on all protected endpoints

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

### Required

- **Java Development Kit (JDK) 17** or higher
  - Verify: `java -version`
  - Download: [OpenJDK 17](https://adoptium.net/)

- **PostgreSQL 12+** database server
  - Verify: `psql --version`
  - Download: [PostgreSQL](https://www.postgresql.org/download/)

- **Gradle 8.14** (included via Gradle Wrapper)
  - No separate installation needed

### Cloud Services (Required for Full Functionality)

- **Azure Active Directory (Entra ID)** tenant
  - Application registration with appropriate permissions
  - Client ID, Tenant ID, and Client Secret

- **Azure Blob Storage** account
  - Connection string and container name

- **Azure Key Vault** (for production)
  - Endpoint URL and managed identity access

- **RabbitMQ** server
  - Host, port, virtual host, and credentials

### Optional Development Tools

- **IntelliJ IDEA** or **Eclipse** (recommended IDEs)
- **Postman** or **curl** for API testing
- **Docker** (for running PostgreSQL and RabbitMQ locally)

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/Haryuya11/Socius.git
cd Socius
```

### 2. Set Up the Database

Create a PostgreSQL database and schema:

```sql
CREATE DATABASE socius_db;
CREATE SCHEMA socius;
```

### 3. Configure Environment Variables

Create environment variables or a `.env` file (not committed to Git) with the following:

```properties
# Database Configuration
DB_URL=jdbc:postgresql://localhost:5432/socius_db
DB_SCHEMA=socius
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password

# Azure Active Directory Configuration
AZURE_TENANT_ID=your-tenant-id
AZURE_CLIENT_ID=your-client-id
AZURE_CLIENT_SECRET=your-client-secret
AZURE_GRAPH_SCOPE=https://graph.microsoft.com/.default
AZURE_PRIMARY_DOMAIN=your-domain.onmicrosoft.com

# Azure Blob Storage Configuration
AZURE_BLOB_CONNECTION_STRING=your-connection-string
AZURE_BLOB_USER_CONTAINER_NAME=user-avatars

# RabbitMQ Configuration
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest
RABBITMQ_VHOST=/
RABBITMQ_SSL=false
RABBITMQ_EXCHANGE_NAME=socius-exchange
NOTIFICATION_ROUTING_KEY=notification.routing.key

# Azure Key Vault Configuration (Production)
KEY_VAULT_ENDPOINT=https://your-keyvault.vault.azure.net/
```

### 4. Initialize Database Schema

Run the provided SQL migration scripts (if available) or let the application create tables via MyBatis migrations.

## 🔧 Configuration

The application is configured through `application.properties` located at:
```
socius-mvc-app/src/main/resources/application.properties
```

### Key Configuration Sections

#### Server Configuration
```properties
server.port=8080
server.servlet.context-path=/api
```

#### Database Configuration
```properties
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=${DB_URL}
mybatis.mapper-locations=classpath*:mappers/**/*.xml
```

#### Security Configuration
```properties
azure.graph.jwk-set-uri=https://${AZURE_TENANT_ID}.ciamlogin.com/${AZURE_TENANT_ID}/discovery/v2.0/keys
azure.graph.issuer-uri=https://${AZURE_TENANT_ID}.ciamlogin.com/${AZURE_TENANT_ID}/v2.0
```

#### Actuator Endpoints
```properties
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when-authorized
```

Available actuator endpoints:
- `/api/actuator/health` - Application health status
- `/api/actuator/info` - Application information
- `/api/actuator/metrics` - Application metrics

## 🔨 Building the Project

### Format Code (Required Before Building)

Always run code formatting before building to avoid checkstyle violations:

```bash
./gradlew spotlessApply
```

This automatically fixes:
- Import order
- Trailing whitespace
- Unused imports
- Line endings

### Build Without Tests

Recommended for quick builds (tests require full environment setup):

```bash
./gradlew clean build -x test
```

### Build With Tests

Requires all external services to be configured:

```bash
./gradlew clean build
```

### Fast Build with Caching

```bash
./gradlew build -x test --parallel --build-cache
```

### Build Executable JAR Only

```bash
./gradlew :socius-mvc-app:bootJar
```

The executable JAR will be created at:
```
socius-mvc-app/build/libs/socius-mvc-app-1.0-SNAPSHOT.jar
```

### Code Quality Checks

#### Check Code Formatting
```bash
./gradlew spotlessCheck
```

#### Run Checkstyle
```bash
./gradlew checkstyleMain checkstyleTest
```

#### View Checkstyle Report
```bash
cat socius-mvc-app/build/reports/checkstyle/main.html
```

### Build Performance

- Clean build without tests: **~25-30 seconds**
- With parallel/cache: **~20-25 seconds**
- Full build with tests: **45-60 seconds**

## ▶️ Running the Application

### Using Gradle

```bash
./gradlew :socius-mvc-app:bootRun
```

### Using JAR File

After building the JAR:

```bash
java -jar socius-mvc-app/build/libs/socius-mvc-app-1.0-SNAPSHOT.jar
```

### Using Docker (If Configured)

```bash
docker compose up -d
```

### Verify Application is Running

Check the health endpoint:

```bash
curl http://localhost:8080/api/actuator/health
```

Expected response:
```json
{
  "status": "UP"
}
```

### Application Access

- **Base URL**: `http://localhost:8080/api`
- **Health Check**: `http://localhost:8080/api/actuator/health`
- **Metrics**: `http://localhost:8080/api/actuator/metrics`

## 📚 API Documentation

### Base URL

All API endpoints are prefixed with `/api`:
```
http://localhost:8080/api
```

### Authentication

Most endpoints require JWT authentication via Azure AD. Include the JWT token in the Authorization header:

```bash
Authorization: Bearer <your-jwt-token>
```

### Main API Endpoints

#### Employee Management
- `GET /api/employees/profile` - Get authenticated user profile
- `POST /api/employees` - Create new employee
- `GET /api/employees/{id}` - Get employee by ID
- `PUT /api/employees/{id}` - Update employee
- `DELETE /api/employees/{id}` - Delete employee
- `POST /api/employees/search` - Search employees with pagination
- `PUT /api/employees/{id}/password` - Change employee password
- `POST /api/employees/{id}/avatar` - Upload employee avatar

#### Team Management
- `POST /api/teams` - Create new team
- `GET /api/teams/{id}` - Get team by ID
- `PUT /api/teams/{id}` - Update team
- `DELETE /api/teams/{id}` - Delete team
- `POST /api/teams/search` - Search teams with pagination

#### Department Management
- `POST /api/departments` - Create new department
- `GET /api/departments/{id}` - Get department by ID
- `PUT /api/departments/{id}` - Update department
- `DELETE /api/departments/{id}` - Delete department
- `POST /api/departments/search` - Search departments with pagination

#### Workforce Management
- `POST /api/workforce/teams` - Assign employee to team
- `DELETE /api/workforce/teams/{id}` - Remove employee from team
- `POST /api/workforce/departments` - Assign employee to department
- `DELETE /api/workforce/departments/{id}` - Remove employee from department

#### Role & Permission Management
- `POST /api/roles` - Create new role
- `GET /api/roles` - List all roles
- `PUT /api/roles/{id}` - Update role
- `DELETE /api/roles/{id}` - Delete role
- `POST /api/roles/{roleId}/permissions` - Assign permissions to role

#### Notifications
- `GET /api/notifications` - Get user notifications
- `PUT /api/notifications/{id}/read` - Mark notification as read

### Response Format

All API responses follow a standardized format:

```json
{
  "success": true,
  "status": 200,
  "code": "S_EMP_001",
  "message": "Operation completed successfully",
  "data": {
    // Response data here
  }
}
```

### Pagination

Paginated endpoints use the following request structure:

```json
{
  "page": 1,
  "size": 10,
  "sort": "createdAt",
  "direction": "DESC",
  "filters": {}
}
```

Response includes pagination metadata:

```json
{
  "content": [],
  "page": 1,
  "size": 10,
  "totalElements": 100,
  "totalPages": 10
}
```

## 📁 Project Structure

```
Socius/
├── .github/
│   └── workflows/
│       └── main.yml                    # CI/CD deployment workflow
├── config/
│   └── checkstyle/
│       ├── google_checks.xml           # Checkstyle configuration
│       └── checkstyle-suppressions.xml # Checkstyle suppressions
├── gradle/
│   ├── libs.versions.toml              # Dependency version catalog
│   └── wrapper/                        # Gradle wrapper files
├── socius-mvc-app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/uit/sociusmvcapp/
│   │   │   │   ├── SociusMvcAppApplication.java
│   │   │   │   ├── employee/           # Employee domain module
│   │   │   │   ├── team/               # Team domain module
│   │   │   │   ├── department/         # Department domain module
│   │   │   │   ├── workforce/          # Workforce domain module
│   │   │   │   ├── iam/                # IAM domain module
│   │   │   │   ├── notification/       # Notification domain module
│   │   │   │   ├── azure/              # Azure integration
│   │   │   │   └── shared/             # Shared utilities
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       ├── log4j2.xml
│   │   │       ├── mappers/            # MyBatis SQL mappers
│   │   │       └── i18n/               # Internationalization files
│   │   └── test/                       # Test classes
│   └── build.gradle                    # Module build configuration
├── build.gradle                        # Root build configuration
├── settings.gradle                     # Gradle settings
├── gradlew                             # Gradle wrapper script (Unix)
├── gradlew.bat                         # Gradle wrapper script (Windows)
├── .gitignore                          # Git ignore rules
└── README.md                           # This file
```

### Module Organization

Each domain module contains:

- **Public API Layer**: DTOs, service interfaces, controllers
- **Internal Layer**: Implementations, converters, repositories, domain models

### MyBatis Mappers

SQL mappers are located in `src/main/resources/mappers/`:
- `EmployeeMapper.xml` - Employee queries
- `TeamMapper.xml` - Team queries
- `DepartmentMapper.xml` - Department queries
- `TeamEmployeeMapper.xml` - Team-employee relationship queries
- `DepartmentEmployeeMapper.xml` - Department-employee relationship queries
- `RoleMapper.xml` - Role queries
- `RolePermissionMapper.xml` - Role-permission queries
- `NotificationMapper.xml` - Notification queries

## 💻 Development

### Code Style Guidelines

The project follows the **Google Java Style Guide** with Checkstyle enforcement.

#### Key Rules

1. **Always run Spotless before committing**:
   ```bash
   ./gradlew spotlessApply
   ```

2. **MyBatis SQL Injection Prevention**:
   - ✅ Use `#{}` for parameters (parameterized queries)
   - ❌ Never use `${}` for user input (SQL injection risk)

   ```xml
   <!-- CORRECT: Safe parameterized query -->
   <select id="findById" resultType="Employee">
     SELECT * FROM employees WHERE id = #{id}
   </select>
   
   <!-- WRONG: SQL injection vulnerability -->
   <select id="findById" resultType="Employee">
     SELECT * FROM employees WHERE id = ${id}
   </select>
   ```

3. **MapStruct for Object Mapping**:
   Always use MapStruct for DTO-Entity conversions:
   ```java
   @Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
   public interface EmployeeConverter {
       EmployeeDto toDto(Employee entity);
       Employee toEntity(EmployeeDto dto);
   }
   ```

4. **Naming Conventions**:
   - Classes: `PascalCase`
   - Methods: `camelCase` (verb-first)
   - Variables: `camelCase`
   - Constants: `UPPER_SNAKE_CASE`
   - Packages: lowercase

5. **Code Complexity**:
   - Keep methods under 50 lines
   - Maximum 3 levels of nesting
   - Maximum 5 parameters per method

### Development Workflow

1. **Create a feature branch**:
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make changes and format code**:
   ```bash
   ./gradlew spotlessApply
   ```

3. **Build and verify**:
   ```bash
   ./gradlew build -x test
   ```

4. **Commit changes**:
   ```bash
   git add .
   git commit -m "feat: your feature description"
   ```

5. **Push and create pull request**:
   ```bash
   git push origin feature/your-feature-name
   ```

### Running Tests

```bash
# Run all tests
./gradlew test

# Run tests for specific module
./gradlew :socius-mvc-app:test

# Run tests with coverage
./gradlew test jacocoTestReport
```

**Note**: Tests require full environment configuration (database, Azure services, RabbitMQ).

### Debugging

To run the application in debug mode:

```bash
./gradlew :socius-mvc-app:bootRun --debug-jvm
```

Then attach your IDE debugger to port 5005.

### Common Development Tasks

#### Add a New Endpoint

1. Create/update DTO in `[module]/dto/`
2. Add method to service interface in `[module]/`
3. Implement in `[module]/internal/service/`
4. Add controller method if needed

#### Add a Database Query

1. Add method to MyBatis mapper interface in `[module]/internal/persistence/`
2. Add SQL query to corresponding XML in `resources/mappers/`
3. Use `#{}` for all parameters (security best practice)

#### Add Domain Object Conversion

1. Create/update MapStruct converter in `[module]/internal/converter/`
2. Use `@Mapper(componentModel = "spring")`
3. Let MapStruct auto-generate implementation

## 🚢 Deployment

### Deployment Workflow

The project includes a GitHub Actions workflow for automated deployment to Azure VM:

**Workflow File**: `.github/workflows/main.yml`

**Trigger**: Manual (`workflow_dispatch`) with environment selection

**Supported Environments**:
- Staging
- Production

### Manual Deployment

#### 1. Build Production JAR

```bash
./gradlew clean bootJar -x test --parallel --build-cache
```

#### 2. Transfer JAR to Server

```bash
scp socius-mvc-app/build/libs/socius-mvc-app-1.0-SNAPSHOT.jar user@server:/opt/socius/
```

#### 3. Run on Server

```bash
ssh user@server
cd /opt/socius
java -jar socius-mvc-app-1.0-SNAPSHOT.jar
```

### Using Systemd Service (Recommended)

Create a systemd service file `/etc/systemd/system/socius.service`:

```ini
[Unit]
Description=Socius Employee Management System
After=network.target

[Service]
Type=simple
User=socius
WorkingDirectory=/opt/socius
ExecStart=/usr/bin/java -jar /opt/socius/socius-mvc-app-1.0-SNAPSHOT.jar
Restart=on-failure
RestartSec=10
StandardOutput=append:/var/log/socius/application.log
StandardError=append:/var/log/socius/error.log

# Environment variables
Environment="JAVA_OPTS=-Xms512m -Xmx2g"

[Install]
WantedBy=multi-user.target
```

Manage the service:

```bash
# Start service
sudo systemctl start socius

# Enable auto-start on boot
sudo systemctl enable socius

# Check status
sudo systemctl status socius

# View logs
sudo journalctl -u socius -f
```

### Environment-Specific Configuration

Use Spring profiles for different environments:

```bash
java -jar -Dspring.profiles.active=production socius-mvc-app-1.0-SNAPSHOT.jar
```

Create `application-production.properties` for production-specific settings.

### Health Checks

Monitor application health:

```bash
curl http://localhost:8080/api/actuator/health
```

Set up monitoring with your preferred tool (Prometheus, Datadog, etc.) using the `/actuator/metrics` endpoint.

## 🤝 Contributing

We welcome contributions to Socius! Please follow these guidelines:

### How to Contribute

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/amazing-feature`
3. **Make your changes**
4. **Format code**: `./gradlew spotlessApply`
5. **Build and test**: `./gradlew build`
6. **Commit changes**: `git commit -m 'feat: add amazing feature'`
7. **Push to branch**: `git push origin feature/amazing-feature`
8. **Open a Pull Request**

### Commit Message Convention

Follow [Conventional Commits](https://www.conventionalcommits.org/):

- `feat:` - New feature
- `fix:` - Bug fix
- `docs:` - Documentation changes
- `style:` - Code style changes (formatting, etc.)
- `refactor:` - Code refactoring
- `test:` - Adding or updating tests
- `chore:` - Maintenance tasks

### Code Review Process

1. All PRs require code review before merging
2. Ensure all CI checks pass
3. Address review comments promptly
4. Keep PRs focused and reasonably sized

### Reporting Issues

When reporting issues, please include:

- Clear description of the problem
- Steps to reproduce
- Expected vs actual behavior
- Environment details (Java version, OS, etc.)
- Relevant logs or error messages

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support & Contact

- **Issues**: [GitHub Issues](https://github.com/Haryuya11/Socius/issues)
- **Discussions**: [GitHub Discussions](https://github.com/Haryuya11/Socius/discussions)
- **Email**: Contact the maintainers through GitHub

## 🙏 Acknowledgments

- Spring Boot and Spring Modulith teams for excellent frameworks
- Azure team for comprehensive cloud services
- All contributors who help improve this project

## 📊 Project Statistics

- **Language**: Java 17
- **Lines of Code**: ~9,159 (source only)
- **Files**: 188 Java source files
- **Modules**: 7 domain modules + shared utilities
- **Database Mappers**: 8 MyBatis XML mappers
- **Minimum Build Time**: ~20-30 seconds

---

**Built with ❤️ using Spring Boot and Spring Modulith**
