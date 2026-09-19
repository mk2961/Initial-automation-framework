package com.company.automation.api.todo.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.company.automation.api.todo.client.TodoClient;
import com.company.automation.api.todo.model.Todo;
import com.company.automation.api.todo.testData.TodoTestData;

/**
 * Focused local checks used while developing the Spring Boot Todo API.
 *
 * The broader TodoApiTest suite represents the reusable API contract coverage;
 * this class keeps a small local-development workflow visible as a reference.
 */
@Tag("local")
public class LocalTodoApiTest {

    private final TodoClient todoClient = new TodoClient();

    /*
     * Register created resources immediately so cleanup still runs when a test
     * fails after setup. A 404 during cleanup is acceptable when the test
     * intentionally deleted the resource itself.
     */
    private final List<Integer> createdTodoIds = new ArrayList<>();

    @AfterEach
    void cleanUpTodos() {
        List<String> failures = new ArrayList<>();

        for (int id : createdTodoIds) {
            try {
                var response = todoClient.deleteTodo(id);
                int status = response.statusCode();

                if (status != 204 && status != 404) {
                    failures.add("Todo " + id + ": HTTP " + status);
                }
            } catch (Exception exception) {
                failures.add("Todo " + id + ": " + exception.getMessage());
            }
        }

        createdTodoIds.clear();

        assertTrue(
                failures.isEmpty(),
                "Cleanup failures: " + String.join("; ", failures));
    }

    @Test
    void shouldCreateAndRetrieveTodo() {
        int userId = TodoTestData.generateUniqueUserId();

        Todo request = new Todo();
        request.setUserId(userId);
        request.setTitle("LOCAL - Create and retrieve Todo");
        request.setCompleted(false);

        var createResponse = todoClient.createTodo(request);
        assertEquals(201, createResponse.statusCode());

        Todo created = createResponse.as(Todo.class);
        createdTodoIds.add(created.getId());

        assertTrue(created.getId() > 0);

        var getResponse = todoClient.getTodoResponseById(created.getId());
        assertEquals(200, getResponse.statusCode());

        Todo retrieved = getResponse.as(Todo.class);

        assertEquals(created.getId(), retrieved.getId());
        assertEquals(userId, retrieved.getUserId());
        assertEquals(request.getTitle(), retrieved.getTitle());
        assertEquals(request.isCompleted(), retrieved.isCompleted());
    }

    @Test
    void shouldRejectBlankTitle() {
        Todo request = new Todo();
        request.setUserId(TodoTestData.generateUniqueUserId());
        request.setTitle("   ");
        request.setCompleted(false);

        var response = todoClient.createTodo(request);

        assertEquals(400, response.statusCode());
        assertEquals(
                "Todo title is required",
                response.jsonPath().getString("error"));
    }

    @Test
    void shouldReturn404ForMissingTodo() {
        var response = todoClient.getTodoResponseById(-1);
        assertEquals(404, response.statusCode());
    }

    @Test
    void shouldDeleteTodo() {
        Todo request = new Todo();
        request.setUserId(TodoTestData.generateUniqueUserId());
        request.setTitle("LOCAL - Todo to delete");
        request.setCompleted(false);

        var createResponse = todoClient.createTodo(request);
        assertEquals(201, createResponse.statusCode());

        int id = createResponse.as(Todo.class).getId();
        createdTodoIds.add(id);

        var deleteResponse = todoClient.deleteTodo(id);

        assertEquals(204, deleteResponse.statusCode());
        assertTrue(deleteResponse.asString().isEmpty());

        var getResponse = todoClient.getTodoResponseById(id);
        assertEquals(404, getResponse.statusCode());
    }

    @Test
    void shouldReturn404WhenDeletingMissingTodo() {
        var response = todoClient.deleteTodo(-1);
        assertEquals(404, response.statusCode());
    }

    @Test
    void shouldUpdateTodo() {
        int userId = TodoTestData.generateUniqueUserId();

        Todo request = new Todo();
        request.setUserId(userId);
        request.setTitle("LOCAL - Original title");
        request.setCompleted(false);

        var createResponse = todoClient.createTodo(request);
        assertEquals(201, createResponse.statusCode());

        int id = createResponse.as(Todo.class).getId();
        createdTodoIds.add(id);

        Todo replacement = new Todo();
        replacement.setUserId(userId);
        replacement.setTitle("LOCAL - Updated title");
        replacement.setCompleted(true);

        var updateResponse = todoClient.updateTodo(id, replacement);
        assertEquals(200, updateResponse.statusCode());

        Todo updated = updateResponse.as(Todo.class);

        assertEquals(id, updated.getId());
        assertEquals(userId, updated.getUserId());
        assertEquals("LOCAL - Updated title", updated.getTitle());
        assertTrue(updated.isCompleted());

        var getResponse = todoClient.getTodoResponseById(id);
        assertEquals(200, getResponse.statusCode());

        Todo stored = getResponse.as(Todo.class);

        assertEquals(id, stored.getId());
        assertEquals(userId, stored.getUserId());
        assertEquals("LOCAL - Updated title", stored.getTitle());
        assertTrue(stored.isCompleted());
    }

    @Test
    void shouldFilterTodosByUserIdAndCompletionStatus() {
        int targetUserId = TodoTestData.generateUniqueUserId();
        int otherUserId = TodoTestData.generateUniqueUserId();

        Todo matching = TodoTestData.createTodo(
                todoClient,
                targetUserId,
                "LOCAL - Matching Todo",
                false);
        createdTodoIds.add(matching.getId());

        Todo wrongStatus = TodoTestData.createTodo(
                todoClient,
                targetUserId,
                "LOCAL - Completed Todo",
                true);
        createdTodoIds.add(wrongStatus.getId());

        Todo wrongUser = TodoTestData.createTodo(
                todoClient,
                otherUserId,
                "LOCAL - Different user's Todo",
                false);
        createdTodoIds.add(wrongUser.getId());

        var results = todoClient.getTodosByUserIdAndCompletionStatus(
                targetUserId,
                false);

        assertTrue(results.stream()
                .anyMatch(todo -> todo.getId() == matching.getId()));

        assertFalse(results.stream()
                .anyMatch(todo -> todo.getId() == wrongStatus.getId()));

        assertFalse(results.stream()
                .anyMatch(todo -> todo.getId() == wrongUser.getId()));

        assertTrue(results.stream()
                .allMatch(todo ->
                        todo.getUserId() == targetUserId && !todo.isCompleted()));
    }
}
