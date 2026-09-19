package com.company.automation.api.todo.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.company.automation.api.todo.client.TodoClient;
import com.company.automation.api.todo.model.Todo;
import com.company.automation.api.todo.testData.TodoTestData;
import com.company.automation.api.todo.util.TodoUtils;
import com.company.automation.core.BaseTest;

import io.restassured.response.Response;

import static org.junit.jupiter.api.Assertions.*;

/**
 * API contract/regression coverage for the Todo service.
 *
 * These tests validate behavior through HTTP only. Direct database assertions
 * live in TodoPersistenceTest so API contract tests do not become coupled to
 * the persistence implementation.
 */
public class TodoApiTest extends BaseTest {

    private TodoClient todoClient;

    /*
     * Tracks Todos created during each test.
     * @AfterEach removes registered test data so tests remain isolated.
     */
    private final List<Integer> createdTodoIds = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        todoClient = new TodoClient();
    }

    @AfterEach
    void cleanUpTodos() {

        List<String> failures = new ArrayList<>();

        for (int id : createdTodoIds) {

            try {
                Response response = todoClient.deleteTodo(id);
                int status = response.statusCode();

                /*
                 * 204 = cleanup deleted the Todo.
                 * 404 = the test already deleted the Todo.
                 */
                if (status != 204 && status != 404) {
                    failures.add(
                            "Todo " + id + ": HTTP " + status);
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

    /*
     * Registers test-created data for cleanup.
     */
    private Todo registerForCleanup(Todo todo) {
        createdTodoIds.add(todo.getId());
        return todo;
    }

    // ============================================================
    // CORE API / CRUD TESTS
    // ============================================================

    @Test
    @Tag("smoke")
    @Tag("regression")
    @DisplayName("TC01 - GET all Todos")
    public void shouldGetAllTodos() {

        int userId = TodoTestData.generateUniqueUserId();

        Todo todo = new Todo();
        todo.setTitle("TC01 - GET All Todo");
        todo.setCompleted(false);
        todo.setUserId(userId);

        Response createResponse = todoClient.createTodo(todo);

        assertEquals(
                201,
                createResponse.statusCode(),
                "Create request should return HTTP 201");

        Todo createdTodo = createResponse.as(Todo.class);
        int id = createdTodo.getId();

        createdTodoIds.add(id);

        List<Todo> todos = todoClient.getTodos();

        assertNotNull(todos);

        assertTrue(
                todos.stream()
                        .anyMatch(todoItem -> todoItem.getId() == id),
                "GET all should contain the Todo created by TC01");
    }

    @Test
    @Tag("smoke")
    @Tag("regression")
    @DisplayName("TC02 - GET Todo by ID")
    public void shouldGetTodoById() {

        int userId = TodoTestData.generateUniqueUserId();

        Todo request = new Todo();
        request.setUserId(userId);
        request.setTitle("TC02 - GET Todo by ID");
        request.setCompleted(false);

        Response createResponse = todoClient.createTodo(request);

        assertEquals(
                201,
                createResponse.statusCode(),
                "Create request should return HTTP 201");

        Todo createdTodo = createResponse.as(Todo.class);
        int id = createdTodo.getId();

        createdTodoIds.add(id);

        Todo retrievedTodo = todoClient.getTodoById(id);

        assertNotNull(retrievedTodo);
        assertEquals(id, retrievedTodo.getId());
        assertEquals(userId, retrievedTodo.getUserId());
        assertEquals(
                "TC02 - GET Todo by ID",
                retrievedTodo.getTitle());
        assertFalse(retrievedTodo.isCompleted());
    }

    @Test
    @Tag("smoke")
    @Tag("regression")
    @DisplayName("TC03 - POST creates a Todo")
    public void shouldCreateTodoSuccessfully() {

        int userId = TodoTestData.generateUniqueUserId();

        Todo todo = new Todo();
        todo.setUserId(userId);
        todo.setTitle("TC03 - POST Todo");
        todo.setCompleted(false);

        Response createResponse = todoClient.createTodo(todo);

        assertNotNull(
                createResponse,
                "POST response should not be null");

        assertEquals(
                201,
                createResponse.statusCode(),
                "POST should return HTTP 201");

        Todo createdTodo = createResponse.as(Todo.class);
        createdTodoIds.add(createdTodo.getId());

        assertTrue(
                createdTodo.getId() > 0,
                "Created Todo should have a generated ID");

        assertEquals(
                userId,
                createdTodo.getUserId(),
                "User ID should match the request");

        assertEquals(
                todo.getTitle(),
                createdTodo.getTitle(),
                "Title should match the request");

        assertEquals(
                todo.isCompleted(),
                createdTodo.isCompleted(),
                "Completed status should match the request");
    }

    @Test
    @Tag("regression")
    @DisplayName("TC04 - PUT updates an existing Todo")
    public void shouldPutTodoSuccessfully() {

        int userId = TodoTestData.generateUniqueUserId();

        Todo todo = new Todo();
        todo.setUserId(userId);
        todo.setTitle("TC04 - Original Todo");
        todo.setCompleted(false);

        Response createResponse = todoClient.createTodo(todo);

        assertEquals(
                201,
                createResponse.statusCode(),
                "Create request should return HTTP 201");

        Todo createdTodo = createResponse.as(Todo.class);
        int id = createdTodo.getId();

        createdTodoIds.add(id);

        todo.setTitle("TC04 - PUT Updated Todo");
        todo.setCompleted(true);

        Response putResponse =
                todoClient.updateTodo(id, todo);

        assertEquals(
                200,
                putResponse.statusCode(),
                "PUT request should return HTTP 200");

        Todo updatedTodo =
                putResponse.as(Todo.class);

        assertEquals(id, updatedTodo.getId());

        assertEquals(
                userId,
                updatedTodo.getUserId(),
                "User ID should remain unchanged");

        assertEquals(
                "TC04 - PUT Updated Todo",
                updatedTodo.getTitle(),
                "PUT should return the updated title");

        assertTrue(
                updatedTodo.isCompleted(),
                "Todo should be completed after PUT");
    }

    @Test
    @Tag("smoke")
    @Tag("regression")
    @DisplayName("TC05 - PATCH updates an existing Todo")
    public void shouldPatchTodoSuccessfully() {

        int userId = TodoTestData.generateUniqueUserId();

        Todo todo = new Todo();
        todo.setUserId(userId);
        todo.setTitle("TC05 - Original Todo");
        todo.setCompleted(false);

        Response createResponse =
                todoClient.createTodo(todo);

        assertEquals(
                201,
                createResponse.statusCode(),
                "Create request should return HTTP 201");

        Todo createdTodo =
                createResponse.as(Todo.class);

        int id = createdTodo.getId();

        createdTodoIds.add(id);

        todo.setTitle("TC05 - Patched Todo");
        todo.setCompleted(true);

        Response patchResponse =
                todoClient.patchTodo(id, todo);

        assertEquals(
                200,
                patchResponse.statusCode(),
                "PATCH request should return HTTP 200");

        Todo patchedTodo =
                patchResponse.as(Todo.class);

        assertEquals(id, patchedTodo.getId());

        assertEquals(
                userId,
                patchedTodo.getUserId(),
                "User ID should remain unchanged");

        assertEquals(
                "TC05 - Patched Todo",
                patchedTodo.getTitle(),
                "PATCH should return the updated title");

        assertTrue(
                patchedTodo.isCompleted(),
                "Todo should be completed after PATCH");
    }

    @Test
    @Tag("regression")
    @DisplayName("TC06 - Create and update Todo workflow")
    public void shouldUpdateTodoSuccessfully() {

        int userId = TodoTestData.generateUniqueUserId();

        Todo todo = new Todo();
        todo.setUserId(userId);
        todo.setCompleted(false);
        todo.setTitle("TC06 - Created Todo");

        Response createResponse =
                todoClient.createTodo(todo);

        assertEquals(
                201,
                createResponse.statusCode(),
                "Create request should return HTTP 201");

        Todo createdTodo =
                createResponse.as(Todo.class);

        int id = createdTodo.getId();

        createdTodoIds.add(id);

        assertEquals(id, createdTodo.getId());
        assertEquals(userId, createdTodo.getUserId());
        assertEquals(
                "TC06 - Created Todo",
                createdTodo.getTitle());
        assertFalse(createdTodo.isCompleted());

        todo.setCompleted(true);
        todo.setTitle("TC06 - Updated Todo");

        Response patchResponse =
                todoClient.patchTodo(id, todo);

        assertEquals(
                200,
                patchResponse.statusCode(),
                "Update request should return HTTP 200");

        Todo updatedTodo =
                patchResponse.as(Todo.class);

        assertEquals(id, updatedTodo.getId());
        assertEquals(userId, updatedTodo.getUserId());
        assertEquals(
                "TC06 - Updated Todo",
                updatedTodo.getTitle());
        assertTrue(updatedTodo.isCompleted());
    }

    @Test
    @Tag("smoke")
    @Tag("regression")
    @DisplayName("TC07 - DELETE removes an existing Todo")
    public void shouldDeleteTodoSuccessfully() {

        int userId = TodoTestData.generateUniqueUserId();

        Todo todo = new Todo();
        todo.setUserId(userId);
        todo.setTitle("TC07 - Todo to Delete");
        todo.setCompleted(false);

        Response createResponse =
                todoClient.createTodo(todo);

        assertEquals(
                201,
                createResponse.statusCode(),
                "Create request should return HTTP 201");

        Todo createdTodo =
                createResponse.as(Todo.class);

        int id = createdTodo.getId();

        createdTodoIds.add(id);

        Response deleteResponse =
                todoClient.deleteTodo(id);

        assertEquals(
                204,
                deleteResponse.statusCode(),
                "DELETE request should return HTTP 204");

        Response getResponse =
                todoClient.getTodoResponseById(id);

        assertEquals(
                404,
                getResponse.statusCode(),
                "Deleted Todo should no longer exist");
    }

    // ============================================================
    // DATA / UTILITY TESTS
    // ============================================================

    @Test
    @Tag("regression")
    @DisplayName("TC08 - Count Todos by user")
    public void shouldCountTodosByUser() {

        int userId1 = TodoTestData.generateUniqueUserId();
        int userId2 = TodoTestData.generateUniqueUserId();
        int userId3 = TodoTestData.generateUniqueUserId();

        registerForCleanup(
                TodoTestData.createTodo(
                        todoClient,
                        userId1,
                        "TC08 - Todo 1",
                        false));

        registerForCleanup(
                TodoTestData.createTodo(
                        todoClient,
                        userId1,
                        "TC08 - Todo 2",
                        false));

        registerForCleanup(
                TodoTestData.createTodo(
                        todoClient,
                        userId1,
                        "TC08 - Todo 3",
                        false));

        registerForCleanup(
                TodoTestData.createTodo(
                        todoClient,
                        userId2,
                        "TC08 - Todo 4",
                        false));

        registerForCleanup(
                TodoTestData.createTodo(
                        todoClient,
                        userId2,
                        "TC08 - Todo 5",
                        false));

        registerForCleanup(
                TodoTestData.createTodo(
                        todoClient,
                        userId3,
                        "TC08 - Todo 6",
                        false));

        List<Todo> todos =
                todoClient.getTodos()
                        .stream()
                        .filter(todo ->
                                todo.getUserId() == userId1
                                        || todo.getUserId() == userId2
                                        || todo.getUserId() == userId3)
                        .toList();

        Map<Integer, Integer> countByUserId =
                TodoUtils.countTodosByUser(todos);

        assertEquals(
                3,
                countByUserId.get(userId1),
                "First TC08 user should have 3 Todos");

        assertEquals(
                2,
                countByUserId.get(userId2),
                "Second TC08 user should have 2 Todos");

        assertEquals(
                1,
                countByUserId.get(userId3),
                "Third TC08 user should have 1 Todo");
    }

    @Test
    @Tag("regression")
    @DisplayName("TC09 - Calculate Todo completion percentage")
    public void shouldCalculateCompletionPercentage() {

        int userId = TodoTestData.generateUniqueUserId();

        registerForCleanup(
                TodoTestData.createTodo(
                        todoClient,
                        userId,
                        "TC09 - Todo 1",
                        true));

        registerForCleanup(
                TodoTestData.createTodo(
                        todoClient,
                        userId,
                        "TC09 - Todo 2",
                        true));

        registerForCleanup(
                TodoTestData.createTodo(
                        todoClient,
                        userId,
                        "TC09 - Todo 3",
                        false));

        registerForCleanup(
                TodoTestData.createTodo(
                        todoClient,
                        userId,
                        "TC09 - Todo 4",
                        false));

        List<Todo> todos =
                todoClient.getTodosByUserId(userId);

        double completedPercentage =
                TodoUtils.calculateCompletionPercentage(todos);

        assertEquals(
                50.0,
                completedPercentage,
                0.001,
                "TC09 user should have a 50% Todo completion rate");
    }

    @Test
    @Tag("regression")
    @DisplayName("TC10 - Identify user with most Todos")
    public void shouldIdentifyUserWithMostTodos() {

        /*
         * No API data is created here.
         * These IDs are controlled values for testing TodoUtils.
         */
        Map<Integer, Integer> countTodosByUser =
                Map.of(
                        110, 3,
                        210, 2,
                        310, 1);

        int userIdWithMostTodos =
                TodoUtils.findUserWithMostTodos(
                        countTodosByUser);

        int winningTodoCount =
                countTodosByUser.get(userIdWithMostTodos);

        assertEquals(
                110,
                userIdWithMostTodos,
                "User 110 should have the most Todos");

        assertEquals(
                3,
                winningTodoCount,
                "Winning user should have 3 Todos");
    }

    @Test
    @Tag("regression")
    @DisplayName("TC11 - Filter Todos by user ID")
    public void shouldFilterTodosByUserId() {

        int targetUserId =
                TodoTestData.generateUniqueUserId();

        int nonTargetUserId =
                TodoTestData.generateUniqueUserId();

        Todo matchingTodo =
                registerForCleanup(
                        TodoTestData.createTodo(
                                todoClient,
                                targetUserId,
                                "TC11 - Target User Todo",
                                false));

        Todo nonMatchingTodo =
                registerForCleanup(
                        TodoTestData.createTodo(
                                todoClient,
                                nonTargetUserId,
                                "TC11 - Non-Target User Todo",
                                true));

        List<Todo> todos =
                todoClient.getTodosByUserId(targetUserId);

        assertTrue(
                todos.stream()
                        .anyMatch(todo ->
                                todo.getId()
                                        == matchingTodo.getId()),
                "Filtered results should contain the target user's Todo");

        assertFalse(
                todos.stream()
                        .anyMatch(todo ->
                                todo.getId()
                                        == nonMatchingTodo.getId()),
                "Filtered results should not contain another user's Todo");

        assertTrue(
                todos.stream()
                        .allMatch(todo ->
                                todo.getUserId() == targetUserId),
                "Every returned Todo should belong to the target user");
    }

    // ============================================================
    // NEGATIVE / VALIDATION TESTS
    // ============================================================

    @Test
    @Tag("regression")
    @DisplayName("TC12 - GET nonexistent Todo returns 404")
    public void shouldReturn404ForInvalidTodoId() {

        int invalidTodoId = 999999;

        Response response =
                todoClient.getTodoResponseById(
                        invalidTodoId);

        assertEquals(
                404,
                response.statusCode(),
                "Nonexistent Todo should return HTTP 404");

        assertTrue(
                response.getBody().asString().isEmpty()
                        || response.getBody()
                                .asString()
                                .equals("{}"));
    }

    @Test
    @Tag("regression")
    @DisplayName("TC13 - GET negative Todo ID returns 404")
    public void shouldReturn404ForNegativeTodoId() {

        int negativeTodoId = -1;

        Response response =
                todoClient.getTodoResponseById(
                        negativeTodoId);

        assertEquals(
                404,
                response.statusCode(),
                "Negative Todo ID should return HTTP 404");

        assertTrue(
                response.getBody().asString().isEmpty()
                        || response.getBody()
                                .asString()
                                .equals("{}"));
    }

    @Test
    @Tag("regression")
    @DisplayName("TC14 - GET non-numeric Todo ID returns 400")
    public void shouldReturn400ForNonNumericTodoId() {

        String nonNumericTodoId = "abc";

        Response response =
                todoClient.getTodoResponseByIdStringId(
                        nonNumericTodoId);

        assertEquals(
                400,
                response.statusCode(),
                "Non-numeric Todo ID should return HTTP 400");
    }

    @Test
    @Tag("regression")
    @DisplayName("TC15 - GET Todo ID zero returns 404")
    public void shouldReturn404ForZeroTodoId() {

        int zeroTodoId = 0;

        Response response =
                todoClient.getTodoResponseById(
                        zeroTodoId);

        assertEquals(
                404,
                response.statusCode(),
                "Todo ID zero should return HTTP 404");

        assertTrue(
                response.getBody().asString().isEmpty()
                        || response.getBody()
                                .asString()
                                .equals("{}"));
    }

    @Test
    @Tag("regression")
    @DisplayName("TC16 - POST blank title returns 400")
    public void shouldRejectBlankTitle() {

        int userId = TodoTestData.generateUniqueUserId();

        Todo request = new Todo();
        request.setUserId(userId);
        request.setTitle("   ");
        request.setCompleted(false);

        Response response =
                todoClient.createTodo(request);

        assertEquals(
                400,
                response.statusCode(),
                "Blank title should return HTTP 400");

        assertEquals(
                "Todo title is required",
                response.jsonPath()
                        .getString("error"));
    }

    @Test
    @Tag("regression")
    @DisplayName("TC17 - DELETE nonexistent Todo returns 404")
    public void shouldReturn404WhenDeletingMissingTodo() {

        Response response =
                todoClient.deleteTodo(-1);

        assertEquals(
                404,
                response.statusCode(),
                "Deleting a nonexistent Todo should return HTTP 404");
    }

    @Test
    @Tag("regression")
    @DisplayName("TC18 - Filter Todos by user ID and completion status")
    public void shouldFilterTodosByUserIdAndCompletionStatus() {

        int targetUserId =
                TodoTestData.generateUniqueUserId();

        int wrongUserId =
                TodoTestData.generateUniqueUserId();

        Todo matchingTodo =
                registerForCleanup(
                        TodoTestData.createTodo(
                                todoClient,
                                targetUserId,
                                "TC18 - Matching Todo",
                                false));

        Todo wrongStatusTodo =
                registerForCleanup(
                        TodoTestData.createTodo(
                                todoClient,
                                targetUserId,
                                "TC18 - Wrong Status",
                                true));

        Todo wrongUserTodo =
                registerForCleanup(
                        TodoTestData.createTodo(
                                todoClient,
                                wrongUserId,
                                "TC18 - Wrong User",
                                false));

        List<Todo> results =
                todoClient
                        .getTodosByUserIdAndCompletionStatus(
                                targetUserId,
                                false);

        assertTrue(
                results.stream()
                        .anyMatch(todo ->
                                todo.getId()
                                        == matchingTodo.getId()),
                "Results should contain the matching Todo");

        assertFalse(
                results.stream()
                        .anyMatch(todo ->
                                todo.getId()
                                        == wrongStatusTodo.getId()),
                "Results should exclude Todos with the wrong completion status");

        assertFalse(
                results.stream()
                        .anyMatch(todo ->
                                todo.getId()
                                        == wrongUserTodo.getId()),
                "Results should exclude Todos belonging to another user");

        assertTrue(
                results.stream()
                        .allMatch(todo ->
                                todo.getUserId() == targetUserId
                                        && !todo.isCompleted()),
                "Every returned Todo should match both filters");
    }
}