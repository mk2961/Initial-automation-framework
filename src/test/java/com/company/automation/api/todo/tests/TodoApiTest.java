package com.company.automation.api.todo.tests;

import static org.junit.jupiter.api.Assertions.*;

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

public class TodoApiTest extends BaseTest {

    private TodoClient todoClient;

    /*
     * Tracks Todos created during each test.
     *
     * @AfterEach removes registered test data so tests remain isolated
     * and do not depend on execution order.
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
                 * 204 = cleanup successfully deleted the Todo.
                 * 404 = the test already deleted the Todo.
                 *
                 * Both are valid cleanup outcomes.
                 */
                if (status != 204 && status != 404) {
                    failures.add("Todo " + id + ": HTTP " + status);
                }

            } catch (Exception exception) {
                failures.add(
                        "Todo " + id + ": " + exception.getMessage()
                );
            }
        }

        createdTodoIds.clear();

        assertTrue(
                failures.isEmpty(),
                "Cleanup failures: " + String.join("; ", failures)
        );
    }


    // ============================================================
    // CORE API / CRUD TESTS
    // ============================================================

    @Test
    @Tag("smoke")
    @Tag("regression")
    @DisplayName("TC01 - GET all Todos")
    public void shouldGetAllTodos() {

        // Arrange - create test-owned data.
        Todo todo = new Todo();
        todo.setUserId(101);
        todo.setTitle("TC01 - GET All Todo");
        todo.setCompleted(false);

        Response createResponse = todoClient.createTodo(todo);

        assertEquals(
                201,
                createResponse.statusCode(),
                "Create request should return HTTP 201"
        );

        Todo createdTodo = createResponse.as(Todo.class);
        createdTodoIds.add(createdTodo.getId());

        // Act.
        List<Todo> todos = todoClient.getTodos();

        // Assert.
        assertNotNull(todos);

        boolean found = false;

        for (Todo todoItem : todos) {
            if (todoItem.getId() == createdTodo.getId()) {
                found = true;
                break;
            }
        }

        assertTrue(
                found,
                "GET all should contain the Todo created by TC01"
        );
    }


    @Test
    @Tag("smoke")
    @Tag("regression")
    @DisplayName("TC02 - GET Todo by ID")
    public void shouldGetTodoById() {

        // Arrange.
        Todo request = new Todo();
        request.setUserId(102);
        request.setTitle("TC02 - GET Todo by ID");
        request.setCompleted(false);

        Response createResponse = todoClient.createTodo(request);

        assertEquals(
                201,
                createResponse.statusCode(),
                "Create request should return HTTP 201"
        );

        Todo createdTodo = createResponse.as(Todo.class);
        int id = createdTodo.getId();
        createdTodoIds.add(id);

        // Act.
        Todo retrievedTodo = todoClient.getTodoById(id);

        // Assert.
        assertNotNull(retrievedTodo);
        assertEquals(id, retrievedTodo.getId());
        assertEquals(102, retrievedTodo.getUserId());
        assertEquals(
                "TC02 - GET Todo by ID",
                retrievedTodo.getTitle()
        );
        assertFalse(retrievedTodo.isCompleted());
    }


    @Test
    @Tag("smoke")
    @Tag("regression")
    @DisplayName("TC03 - POST creates a Todo")
    public void shouldCreateTodoSuccessfully() {

        // Arrange.
        Todo todo = new Todo();
        todo.setUserId(103);
        todo.setTitle("TC03 - POST Todo");
        todo.setCompleted(false);

        // Act.
        Response createResponse = todoClient.createTodo(todo);

        // Assert HTTP response.
        assertNotNull(
                createResponse,
                "POST response should not be null"
        );

        assertEquals(
                201,
                createResponse.statusCode(),
                "POST should return HTTP 201"
        );

        Todo createdTodo = createResponse.as(Todo.class);
        createdTodoIds.add(createdTodo.getId());

        // Assert created resource.
        assertTrue(
                createdTodo.getId() > 0,
                "Created Todo should have a generated ID"
        );

        assertEquals(
                103,
                createdTodo.getUserId(),
                "User ID should match the request"
        );

        assertEquals(
                "TC03 - POST Todo",
                createdTodo.getTitle(),
                "Title should match the request"
        );

        assertFalse(
                createdTodo.isCompleted(),
                "New Todo should be incomplete"
        );
    }


    @Test
    @Tag("regression")
    @DisplayName("TC04 - PUT updates and persists an existing Todo")
    public void shouldPutTodoSuccessfully() {

        // Arrange - create the original resource.
        Todo todo = new Todo();
        todo.setUserId(104);
        todo.setTitle("TC04 - Original Todo");
        todo.setCompleted(false);

        Response createResponse = todoClient.createTodo(todo);

        assertEquals(
                201,
                createResponse.statusCode(),
                "Create request should return HTTP 201"
        );

        Todo createdTodo = createResponse.as(Todo.class);
        int id = createdTodo.getId();
        createdTodoIds.add(id);

        // Modify the resource.
        todo.setTitle("TC04 - PUT Updated Todo");
        todo.setCompleted(true);

        // Act.
        Response putResponse = todoClient.updateTodo(id, todo);

        assertEquals(
                200,
                putResponse.statusCode(),
                "PUT request should return HTTP 200"
        );

        Todo updatedTodo = putResponse.as(Todo.class);

        // Assert PUT response.
        assertEquals(id, updatedTodo.getId());
        assertEquals(104, updatedTodo.getUserId());
        assertEquals(
                "TC04 - PUT Updated Todo",
                updatedTodo.getTitle()
        );
        assertTrue(updatedTodo.isCompleted());

        /*
         * Verify the update persisted by retrieving the Todo again.
         */
        Todo persistedTodo = todoClient.getTodoById(id);

        assertNotNull(persistedTodo);
        assertEquals(id, persistedTodo.getId());
        assertEquals(104, persistedTodo.getUserId());
        assertEquals(
                "TC04 - PUT Updated Todo",
                persistedTodo.getTitle()
        );
        assertTrue(persistedTodo.isCompleted());
    }


    @Test
    @Tag("smoke")
    @Tag("regression")
    @DisplayName("TC05 - PATCH updates an existing Todo")
    public void shouldPatchTodoSuccessfully() {

        // Arrange.
        Todo todo = new Todo();
        todo.setUserId(105);
        todo.setTitle("TC05 - Original Todo");
        todo.setCompleted(false);

        Response createResponse = todoClient.createTodo(todo);

        assertEquals(
                201,
                createResponse.statusCode(),
                "Create request should return HTTP 201"
        );

        Todo createdTodo = createResponse.as(Todo.class);
        int id = createdTodo.getId();
        createdTodoIds.add(id);

        // Modify values for PATCH.
        todo.setTitle("TC05 - Patched Todo");
        todo.setCompleted(true);

        // Act.
        Response patchResponse = todoClient.patchTodo(id, todo);

        assertEquals(
                200,
                patchResponse.statusCode(),
                "PATCH request should return HTTP 200"
        );

        Todo patchedTodo = patchResponse.as(Todo.class);

        // Assert.
        assertEquals(id, patchedTodo.getId());
        assertEquals(105, patchedTodo.getUserId());
        assertEquals(
                "TC05 - Patched Todo",
                patchedTodo.getTitle()
        );
        assertTrue(patchedTodo.isCompleted());
    }


    @Test
    @Tag("regression")
    @DisplayName("TC06 - Create and update Todo workflow")
    public void shouldUpdateTodoSuccessfully() {

        // Arrange.
        Todo todo = new Todo();
        todo.setUserId(106);
        todo.setTitle("TC06 - Created Todo");
        todo.setCompleted(false);

        Response createResponse = todoClient.createTodo(todo);

        assertEquals(
                201,
                createResponse.statusCode(),
                "Create request should return HTTP 201"
        );

        Todo createdTodo = createResponse.as(Todo.class);
        int id = createdTodo.getId();
        createdTodoIds.add(id);

        // Verify initial state.
        assertEquals(id, createdTodo.getId());
        assertEquals(106, createdTodo.getUserId());
        assertEquals(
                "TC06 - Created Todo",
                createdTodo.getTitle()
        );
        assertFalse(createdTodo.isCompleted());

        // Act - update the Todo.
        todo.setTitle("TC06 - Updated Todo");
        todo.setCompleted(true);

        Response patchResponse = todoClient.patchTodo(id, todo);

        assertEquals(
                200,
                patchResponse.statusCode(),
                "Update request should return HTTP 200"
        );

        Todo updatedTodo = patchResponse.as(Todo.class);

        // Assert updated state.
        assertEquals(id, updatedTodo.getId());
        assertEquals(106, updatedTodo.getUserId());
        assertEquals(
                "TC06 - Updated Todo",
                updatedTodo.getTitle()
        );
        assertTrue(updatedTodo.isCompleted());
    }


    @Test
    @Tag("smoke")
    @Tag("regression")
    @DisplayName("TC07 - DELETE removes an existing Todo")
    public void shouldDeleteTodoSuccessfully() {

        // Arrange.
        Todo todo = new Todo();
        todo.setUserId(107);
        todo.setTitle("TC07 - Todo to Delete");
        todo.setCompleted(false);

        Response createResponse = todoClient.createTodo(todo);

        assertEquals(
                201,
                createResponse.statusCode(),
                "Create request should return HTTP 201"
        );

        Todo createdTodo = createResponse.as(Todo.class);
        int id = createdTodo.getId();

        /*
         * Keep the ID registered for cleanup.
         * Cleanup accepts 404 if this test already deleted it.
         */
        createdTodoIds.add(id);

        // Act.
        Response deleteResponse = todoClient.deleteTodo(id);

        assertEquals(
                204,
                deleteResponse.statusCode(),
                "DELETE request should return HTTP 204"
        );

        // Verify deletion.
        Response getResponse =
                todoClient.getTodoResponseById(id);

        assertEquals(
                404,
                getResponse.statusCode(),
                "Deleted Todo should no longer exist"
        );
    }


    // ============================================================
    // DATA / UTILITY TESTS
    // ============================================================

    @Test
    @Tag("regression")
    @DisplayName("TC08 - Count Todos by user")
    public void shouldCountTodosByUser() {

        TodoTestData.createTodoByUserId(
                108, "TC08 - Todo 1", false, createdTodoIds
        );
        TodoTestData.createTodoByUserId(
                108, "TC08 - Todo 2", false, createdTodoIds
        );
        TodoTestData.createTodoByUserId(
                108, "TC08 - Todo 3", false, createdTodoIds
        );
        TodoTestData.createTodoByUserId(
                208, "TC08 - Todo 4", false, createdTodoIds
        );
        TodoTestData.createTodoByUserId(
                208, "TC08 - Todo 5", false, createdTodoIds
        );
        TodoTestData.createTodoByUserId(
                308, "TC08 - Todo 6", false, createdTodoIds
        );

        List<Todo> todos = todoClient.getTodos();

        Map<Integer, Integer> countByUserId =
                TodoUtils.countTodosByUser(todos);

        assertEquals(
                3,
                countByUserId.get(108),
                "User 108 should have 3 Todos"
        );

        assertEquals(
                2,
                countByUserId.get(208),
                "User 208 should have 2 Todos"
        );

        assertEquals(
                1,
                countByUserId.get(308),
                "User 308 should have 1 Todo"
        );
    }


    @Test
    @Tag("regression")
    @DisplayName("TC09 - Calculate Todo completion percentage")
    public void shouldCalculateCompletionPercentage() {

        /*
         * Two completed + two incomplete = 50% completion.
         */
        TodoTestData.createTodoByUserId(
                109, "TC09 - Todo 1", true, createdTodoIds
        );
        TodoTestData.createTodoByUserId(
                109, "TC09 - Todo 2", true, createdTodoIds
        );
        TodoTestData.createTodoByUserId(
                109, "TC09 - Todo 3", false, createdTodoIds
        );
        TodoTestData.createTodoByUserId(
                109, "TC09 - Todo 4", false, createdTodoIds
        );

        /*
         * Filter to TC09-owned data so unrelated API data cannot
         * affect the percentage.
         */
        List<Todo> todos =
                todoClient.getTodosByUserId(109);

        double completedPercentage =
                TodoUtils.calculateCompletionPercentage(todos);

        assertEquals(
                50.0,
                completedPercentage,
                0.001,
                "User 109 should have a 50% Todo completion rate"
        );
    }


    @Test
    @Tag("regression")
    @DisplayName("TC10 - Identify user with most Todos")
    public void shouldIdentifyUserWithMostTodos() {

        /*
         * Pure utility test. No API setup is necessary because the
         * method under test operates directly on a user/count map.
         */
        Map<Integer, Integer> countTodosByUser = Map.of(
                110, 3,
                210, 2,
                310, 1
        );

        int userIdWithMostTodos =
                TodoUtils.findUserWithMostTodos(countTodosByUser);

        int winningTodoCount =
                countTodosByUser.get(userIdWithMostTodos);

        assertEquals(
                110,
                userIdWithMostTodos,
                "User 110 should have the most Todos"
        );

        assertEquals(
                3,
                winningTodoCount,
                "Winning user should have 3 Todos"
        );
    }


    // ============================================================
    // FILTER TESTS
    // ============================================================

    @Test
    @Tag("regression")
    @DisplayName("TC11 - Filter Todos by user ID")
    public void shouldFilterTodosByUserId() {

        /*
         * Create matching and non-matching data to verify
         * that the endpoint excludes other users.
         */
        TodoTestData.createTodoByUserId(
                111,
                "TC11 - Target User Todo",
                false,
                createdTodoIds
        );

        TodoTestData.createTodoByUserId(
                999,
                "TC11 - Non-Target User Todo",
                true,
                createdTodoIds
        );

        // Act.
        List<Todo> todos =
                todoClient.getTodosByUserId(111);

        // Assert.
        assertFalse(
                todos.isEmpty(),
                "Filter should return at least one Todo for user 111"
        );

        for (Todo todo : todos) {
            assertEquals(
                    111,
                    todo.getUserId(),
                    "Every returned Todo should belong to user 111"
            );
        }
    }


    // ============================================================
    // GET NEGATIVE / VALIDATION TESTS
    // ============================================================

    @Test
    @Tag("regression")
    @DisplayName("TC12 - GET nonexistent Todo returns 404")
    public void shouldReturn404ForInvalidTodoId() {

        int invalidTodoId = 999999;

        Response response =
                todoClient.getTodoResponseById(invalidTodoId);

        assertEquals(
                404,
                response.statusCode(),
                "Nonexistent Todo should return HTTP 404"
        );

        assertTrue(
                response.getBody().asString().isEmpty()
                        || response.getBody().asString().equals("{}"),
                "404 response body should be empty or {}"
        );
    }


    @Test
    @Tag("regression")
    @DisplayName("TC13 - GET negative Todo ID returns 404")
    public void shouldReturn404ForNegativeTodoId() {

        int negativeTodoId = -1;

        Response response =
                todoClient.getTodoResponseById(negativeTodoId);

        assertEquals(
                404,
                response.statusCode(),
                "Negative Todo ID should return HTTP 404"
        );

        assertTrue(
                response.getBody().asString().isEmpty()
                        || response.getBody().asString().equals("{}"),
                "404 response body should be empty or {}"
        );
    }


    @Test
    @Tag("regression")
    @DisplayName("TC14 - GET non-numeric Todo ID returns 400")
    public void shouldReturn400ForNonNumericTodoId() {

        String nonNumericTodoId = "abc";

        Response response =
                todoClient.getTodoResponseByIdStringId(nonNumericTodoId);

        assertEquals(
                400,
                response.statusCode(),
                "Non-numeric Todo ID should return HTTP 400"
        );
    }


    @Test
    @Tag("regression")
    @DisplayName("TC15 - GET Todo ID zero returns 404")
    public void shouldReturn404ForZeroTodoId() {

        int zeroTodoId = 0;

        Response response =
                todoClient.getTodoResponseById(zeroTodoId);

        assertEquals(
                404,
                response.statusCode(),
                "Todo ID zero should return HTTP 404"
        );

        assertTrue(
                response.getBody().asString().isEmpty()
                        || response.getBody().asString().equals("{}"),
                "404 response body should be empty or {}"
        );
    }


    // ============================================================
    // POST / DELETE VALIDATION TESTS
    // ============================================================

    @Test
    @Tag("regression")
    @DisplayName("TC16 - POST blank title returns 400")
    public void shouldReturn400WhenTitleIsBlank() {

        // Arrange.
        Todo todo = new Todo();
        todo.setUserId(116);
        todo.setTitle("   ");
        todo.setCompleted(false);

        // Act.
        Response response =
                todoClient.createTodo(todo);

        // Assert.
        assertEquals(
                400,
                response.statusCode(),
                "Blank Todo title should return HTTP 400"
        );

        assertTrue(
                response.asString().contains("Todo title is required"),
                "Error response should explain that the Todo title is required"
        );
    }


    @Test
    @Tag("regression")
    @DisplayName("TC17 - DELETE nonexistent Todo returns 404")
    public void shouldReturn404WhenDeletingNonexistentTodo() {

        int nonexistentTodoId = 999999;

        Response response =
                todoClient.deleteTodo(nonexistentTodoId);

        assertEquals(
                404,
                response.statusCode(),
                "Deleting a nonexistent Todo should return HTTP 404"
        );
    }


    // ============================================================
    // COMBINED FILTER TESTS
    // ============================================================

    @Test
    @Tag("regression")
    @DisplayName("TC18 - Filter Todos by user ID and completion status")
    public void shouldFilterTodosByUserIdAndCompletionStatus() {

        /*
         * Create three controlled records:
         *
         * 118 / completed=true  -> should be returned
         * 118 / completed=false -> should be excluded
         * 218 / completed=true  -> should be excluded
         */
        TodoTestData.createTodoByUserId(
                118,
                "TC18 - Matching Todo",
                true,
                createdTodoIds
        );

        TodoTestData.createTodoByUserId(
                118,
                "TC18 - Wrong Completion Status",
                false,
                createdTodoIds
        );

        TodoTestData.createTodoByUserId(
                218,
                "TC18 - Wrong User",
                true,
                createdTodoIds
        );

        // Act - filter by both user ID and completion status.
        List<Todo> todos =
                todoClient.getTodosByUserIdAndCompletionStatus(
                        118,
                        true
                );

        // Assert.
        assertFalse(
                todos.isEmpty(),
                "Combined filter should return the matching Todo"
        );

        assertEquals(
                1,
                todos.size(),
                "Only one TC18 Todo should match both filters"
        );

        Todo filteredTodo = todos.get(0);

        assertEquals(
                118,
                filteredTodo.getUserId(),
                "Returned Todo should belong to user 118"
        );

        assertTrue(
                filteredTodo.isCompleted(),
                "Returned Todo should be completed"
        );

        assertEquals(
                "TC18 - Matching Todo",
                filteredTodo.getTitle(),
                "Combined filter should return the expected Todo"
        );
    }
}