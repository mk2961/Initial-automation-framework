package com.company.automation.integration.todo.tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.company.automation.api.todo.client.TodoClient;
import com.company.automation.api.todo.model.Todo;
import com.company.automation.api.todo.testData.TodoTestData;
import com.company.automation.config.ConfigManager;
import com.company.automation.core.BaseTest;
import com.company.automation.integration.todo.database.TodoDbClient;
import com.company.automation.integration.todo.database.TodoDbRecord;

import io.restassured.response.Response;

/**
 * API-to-database integration coverage for the local Todo application.
 *
 * These tests intentionally cross the HTTP and persistence boundaries.
 * API contract behavior remains in TodoApiTest, while this suite verifies
 * that critical API operations produce the expected database state.
 *
 * The integration tag allows CI/CD to execute persistence tests separately
 * from smoke and regression suites. The local tag documents that these tests
 * require direct access to the local H2 database.
 */
@Tag("integration")
@Tag("local")
public class TodoPersistenceTest extends BaseTest {

    private TodoClient todoClient;
    private TodoDbClient todoDbClient;

    /*
     * Tracks records created by each integration test so cleanup still occurs
     * when a test fails before reaching its normal delete operation.
     */
    private final List<Integer> createdTodoIds = new ArrayList<>();

    @BeforeEach
    void setUp() {

        /*
         * Direct database validation is currently supported only for the local
         * environment because this framework has direct access to the local
         * application's H2 database.
         */
        assumeTrue(
                "local".equalsIgnoreCase(ConfigManager.getEnvironment()),
                "Todo database integration tests run only against the local environment");

        todoClient = new TodoClient();
        todoDbClient = new TodoDbClient();
    }

    @AfterEach
    void cleanUpTodos() {

        /*
         * If the environment assumption prevented setup from completing,
         * there is no API client or test data to clean up.
         */
        if (todoClient == null) {
            return;
        }

        List<String> failures = new ArrayList<>();

        for (int id : createdTodoIds) {

            try {
                Response response = todoClient.deleteTodo(id);
                int status = response.statusCode();

                /*
                 * 204 means cleanup removed the record.
                 * 404 is also acceptable when the test already deleted it.
                 */
                if (status != 204 && status != 404) {
                    failures.add("Todo " + id + ": HTTP " + status);
                }

            } catch (Exception exception) {
                failures.add(
                        "Todo " + id + ": " + exception.getMessage());
            }
        }

        createdTodoIds.clear();

        assertTrue(
                failures.isEmpty(),
                "Cleanup failures: " + String.join("; ", failures));
    }

    @Test
    @DisplayName("INT01 - POST persists Todo in database")
    void shouldPersistCreatedTodoInDatabase() throws SQLException {

        int userId = TodoTestData.generateUniqueUserId();

        Todo request = new Todo();
        request.setUserId(userId);
        request.setTitle("INT01 - Persisted Todo");
        request.setCompleted(false);

        Response createResponse = todoClient.createTodo(request);

        assertEquals(
                201,
                createResponse.statusCode(),
                "POST should return HTTP 201");

        Todo createdTodo = createResponse.as(Todo.class);
        createdTodoIds.add(createdTodo.getId());

        /*
         * Read directly from the database instead of calling GET through the
         * API. This independently verifies that POST actually persisted the
         * expected values rather than only returning them in the response.
         */
        TodoDbRecord dbTodo =
                todoDbClient.getTodoById(createdTodo.getId());

        assertNotNull(
                dbTodo,
                "Created Todo should exist in the database");

        assertEquals(
                createdTodo.getId(),
                dbTodo.id(),
                "Database ID should match the API-created Todo");

        assertEquals(
                userId,
                dbTodo.userId(),
                "Database user ID should match the request");

        assertEquals(
                request.getTitle(),
                dbTodo.title(),
                "Database title should match the request");

        assertEquals(
                request.isCompleted(),
                dbTodo.completed(),
                "Database completed status should match the request");
    }

    @Test
    @DisplayName("INT02 - PUT persists updated Todo values")
    void shouldPersistUpdatedTodoInDatabase() throws SQLException {

        int userId = TodoTestData.generateUniqueUserId();

        Todo original = new Todo();
        original.setUserId(userId);
        original.setTitle("INT02 - Original Todo");
        original.setCompleted(false);

        Response createResponse = todoClient.createTodo(original);

        assertEquals(
                201,
                createResponse.statusCode(),
                "Setup POST should return HTTP 201");

        Todo createdTodo = createResponse.as(Todo.class);
        int id = createdTodo.getId();

        createdTodoIds.add(id);

        Todo replacement = new Todo();
        replacement.setUserId(userId);
        replacement.setTitle("INT02 - Updated Todo");
        replacement.setCompleted(true);

        Response updateResponse =
                todoClient.updateTodo(id, replacement);

        assertEquals(
                200,
                updateResponse.statusCode(),
                "PUT should return HTTP 200");

        /*
         * The database read verifies that the update crossed the persistence
         * boundary rather than validating only the PUT response body.
         */
        TodoDbRecord dbTodo =
                todoDbClient.getTodoById(id);

        assertNotNull(
                dbTodo,
                "Updated Todo should still exist in the database");

        assertEquals(
                id,
                dbTodo.id(),
                "PUT should update the existing database record");

        assertEquals(
                userId,
                dbTodo.userId(),
                "Database user ID should remain unchanged");

        assertEquals(
                replacement.getTitle(),
                dbTodo.title(),
                "Updated title should be persisted");

        assertEquals(
                replacement.isCompleted(),
                dbTodo.completed(),
                "Updated completed status should be persisted");
    }

    @Test
    @DisplayName("INT03 - DELETE removes Todo from database")
    void shouldRemoveDeletedTodoFromDatabase() throws SQLException {

        Todo request = new Todo();
        request.setUserId(TodoTestData.generateUniqueUserId());
        request.setTitle("INT03 - Todo to Delete");
        request.setCompleted(false);

        Response createResponse = todoClient.createTodo(request);

        assertEquals(
                201,
                createResponse.statusCode(),
                "Setup POST should return HTTP 201");

        int id = createResponse.as(Todo.class).getId();
        createdTodoIds.add(id);

        Response deleteResponse =
                todoClient.deleteTodo(id);

        assertEquals(
                204,
                deleteResponse.statusCode(),
                "DELETE should return HTTP 204");

        /*
         * A successful HTTP response alone does not prove that the persisted
         * record was removed. Verify deletion directly against the database.
         */
        assertFalse(
                todoDbClient.todoExistsById(id),
                "Deleted Todo should no longer exist in the database");
    }
}