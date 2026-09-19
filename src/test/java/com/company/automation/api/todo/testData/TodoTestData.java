package com.company.automation.api.todo.testData;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import com.company.automation.api.todo.client.TodoClient;
import com.company.automation.api.todo.model.Todo;

import io.restassured.response.Response;

/**
 * Factory/helper methods for creating Todo records used by API tests.
 * Created IDs are registered with the calling test so @AfterEach cleanup
 * can remove test-owned data even when a test fails partway through.
 */
public final class TodoTestData {

    private TodoTestData() {
        // Utility class; do not instantiate.
    }

    public static Todo createTodoByUserId(
            int userId,
            String title,
            boolean completed,
            List<Integer> createdTodoIds) {

        Todo todo = new Todo();
        todo.setUserId(userId);
        todo.setTitle(title);
        todo.setCompleted(completed);

        Response createResponse = new TodoClient().createTodo(todo);
        assertEquals(
                201,
                createResponse.statusCode(),
                "Test-data creation should return HTTP 201");

        Todo createdTodo = createResponse.as(Todo.class);
        createdTodoIds.add(createdTodo.getId());
        return createdTodo;
    }
}
