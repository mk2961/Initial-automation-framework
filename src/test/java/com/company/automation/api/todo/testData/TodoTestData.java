package com.company.automation.api.todo.testData;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.company.automation.api.todo.client.TodoClient;
import com.company.automation.api.todo.model.Todo;

import io.restassured.response.Response;

/**
 * Reusable factory methods for API test data.
 *
 * Test-case identifiers stay in Todo titles for human traceability, while
 * generated user IDs keep records created by different tests isolated.
 */
public final class TodoTestData {

    private static int nextUserId = 100_000;

    private TodoTestData() {
        // Static test-data utility.
    }

    /**
     * Returns a unique user ID within this JVM/test run.
     *
     * The synchronized counter is safe for parallel test threads in one JVM.
     * Separate CI processes would need a build/worker-specific prefix or a
     * real test-user provisioning strategy to guarantee global uniqueness.
     */
    public static synchronized int generateUniqueUserId() {
        return nextUserId++;
    }

    /**
     * Creates setup data through the public API rather than inserting directly
     * into the database, keeping API tests independent of persistence details.
     */
    public static Todo createTodo(
            TodoClient todoClient,
            int userId,
            String title,
            boolean completed) {

        Todo todo = new Todo();
        todo.setUserId(userId);
        todo.setTitle(title);
        todo.setCompleted(completed);

        Response response = todoClient.createTodo(todo);

        assertEquals(
                201,
                response.statusCode(),
                "Test-data creation should return HTTP 201");

        return response.as(Todo.class);
    }
}
