# Todo API Automation Framework

Java 21 / Maven API automation project using REST Assured, JUnit 5, and Allure.
The framework can target multiple environments through `config-<env>.properties`.

## Project structure

- `api/todo/client` - REST Assured client methods for Todo endpoints
- `api/todo/model` - request/response model
- `api/todo/testData` - reusable test-data creation helpers
- `api/todo/tests` - API integration tests
- `api/todo/util` - pure Todo collection/analysis helpers
- `config` - environment configuration loader
- `core` - shared REST Assured request configuration and base test behavior

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

## Reporting

REST Assured requests are attached to Allure results through `AllureRestAssured`.
Generate/serve an Allure report after a test run with the configured Allure tooling.

## Test-data isolation

Tests create their own records and register generated IDs for `@AfterEach` cleanup.
Cleanup accepts both `204` (deleted during cleanup) and `404` (already deleted by the test).
