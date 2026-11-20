# Repository Guidelines

## Project Structure & Module Organization

This is a multi-module Spring Boot project for corporate governance and meeting management:

```
corporate_approval/
├── server/                  # Main backend module (Spring Boot 4.0, Java 21)
│   ├── src/main/java/ru/avg/server/
│   │   ├── config/          # Spring configuration (e.g., WebConfig)
│   │   ├── controller/      # REST API controllers
│   │   ├── service/         # Business logic (interfaces + impl/)
│   │   ├── mservice/        # Microservice-layer orchestration
│   │   ├── repository/      # Spring Data JPA repositories
│   │   ├── model/           # JPA entities, DTOs, enums, mappers
│   │   ├── exception/       # Custom exceptions & error handling
│   │   └── utils/           # Shared utilities (Verifier, Validators)
│   └── pom.xml
├── server_users/            # Secondary module (users-related functionality)
└── GIGACODE.md
```

All source code lives under `ru.avg.server` package. DTOs and entities are kept in separate layers.

## Build, Test, and Development Commands

Navigate to the `server/` directory before running commands:

```bash
# Build the project
mvn clean package

# Run the application (Spring Boot dev server)
mvn spring-boot:run

# Run tests only
mvn test

# Generate Javadoc
mvn javadoc:javadoc

# Generate OpenAPI spec (requires running server first)
mvn openapi:generate
```

Swagger UI is available at `http://localhost:8080/swagger-ui.html` when the server is running.

## Coding Style & Naming Conventions

- **Language**: Java 21
- **Indentation**: 4 spaces
- **Naming**:
    - Classes: `PascalCase` (e.g., `MeetingParticipantServiceImpl`)
    - Interfaces: `PascalCase` (e.g., `MeetingParticipantService`)
    - Methods/fields: `camelCase` (e.g., `meetingParticipantMapper`)
    - Constants: `UPPER_SNAKE_CASE`
- **Lombok**: Used extensively for boilerplate (`@RequiredArgsConstructor`, `@Builder`, `@Getter`)
- **Javadoc**: All public classes, interfaces, and methods must have comprehensive Javadoc in **English**, following the
  style of `Verifier.java`:
    - Include `<p>` paragraphs for descriptions
    - Use `@param`, `@return`, `@throws`, `@see`, `@author AVG`, `@since 1.0`
    - Reference related classes with `{@link ...}`

## Testing Guidelines

- **Framework**: JUnit 5 + Spring Boot Test (`spring-boot-starter-test`)
- **Test location**: `server/src/test/java/` (mirrors main package structure)
- **Naming**: `<ClassName>Test.java` (e.g., `MeetingParticipantServiceImplTest.java`)
- **Run tests**:
  ```bash
  mvn test
  ```

## Commit & Pull Request Guidelines

The project follows **[Conventional Commits](https://www.conventionalcommits.org/)** format:

```
<type>(<scope>): <description>
```

**Types**: `feat`, `fix`, `refactor`, `docs`, `chore`

**Examples from this repository**:

```
feat(Meeting): добавлено создание секретаря и председателя
fix(Meeting): исправление наименования полей в MeetingDto
refactor(MeetingParticipant): вынести логику пагинации в отдельный метод
```

- Descriptions are in **Russian** (matching project language)
- Scope corresponds to the affected module/component (e.g., `Meeting`, `MeetingParticipant`, `Repositories`)
- Keep descriptions imperative and concise (under 72 characters)

## Security & Configuration Tips

- Database credentials and secrets should be managed via environment variables or a `.env` file
- Never commit `.env` files or credentials
- The project uses PostgreSQL — ensure connection properties are set in `application.yml` or `application.properties`
