# Todo API Automation Framework

Java 21 / Maven automation reference project using REST Assured, JUnit 5, Allure, and targeted JDBC validation against a local H2 database.

The project is intentionally structured as both a working automation framework and a reference for common SDET patterns: environment configuration, API clients, test-data isolation, cleanup, tagging, reporting, and API-to-database integration testing.

## Project structure

- `api/todo/client` - REST Assured client methods and endpoint wiring
- `api/todo/model` - API request/response model
- `api/todo/testData` - reusable test-data creation and generated test IDs
- `api/todo/tests` - API contract/regression tests and focused local checks
- `api/todo/util` - pure Todo collection/analysis helpers
- `config` - environment-specific configuration loader
- `core` - shared REST Assured request configuration and base test behavior
- `integration/todo/database` - JDBC helpers and database-facing record model
- `integration/todo/tests` - targeted API-to-database persistence tests

## Run against the local Todo API

Start the Todo API on port `8081`, then run:

```powershell
mvn test "-Denv=local"
```

Run only smoke tests:

```powershell
mvn test "-Denv=local" "-Dtest.groups=smoke"
```

Run the regression suite:

```powershell
mvn test "-Denv=local" "-Dtest.groups=regression"
```

Run the database integration suite:

```powershell
mvn test "-Denv=local" "-Dtest.groups=integration"
```

Run the complete local suite:

```powershell
mvn test "-Denv=local"
```

The Jenkins `TEST_GROUP` choices are `smoke`, `regression`, `integration`, and `all`. The `local` tag is an environment constraint used by local-only tests; it is not a Jenkins suite selection.

## Environment configuration

`ConfigManager` loads `config-<env>.properties` based on the JVM `env` property. If `-Denv` is omitted, the framework defaults to `qa`.

That default makes environment selection important: local tests should always be run with `-Denv=local` so they target the Spring Boot API on port 8081 rather than the QA/mock URL.

## Test-data isolation and cleanup

Test-case identifiers remain in Todo titles (for example, `TC09 - Todo 1`) so records are easy to trace when inspecting an environment. User IDs are generated per JVM test run so tests do not depend on fixed shared IDs.

Created Todo IDs are registered for `@AfterEach` cleanup. Cleanup accepts both `204` (the cleanup deleted the record) and `404` (the test already deleted it).

The current generated user-ID counter protects parallel threads within one JVM. A real shared CI environment with multiple independent workers should add a build/worker-specific namespace or provision real test users.

## API vs. persistence coverage

`TodoApiTest` validates the public HTTP contract and does not query the database directly. `TodoPersistenceTest` contains the smaller set of tests that intentionally cross boundaries to prove API writes are reflected in H2.

Keeping those concerns separate avoids coupling every API test to the persistence implementation while still demonstrating end-to-end data validation.

## Reporting

REST Assured requests are attached to Allure results through `AllureRestAssured`. Maven Surefire produces JUnit XML reports, and the Jenkins pipeline publishes both JUnit and Allure results.
