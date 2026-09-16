package com.company.automation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TodoApiTest {

        private TodoClient todoClient;

        @BeforeEach
        public void setUp() {
                todoClient = new TodoClient();
        }

        @Test
        public void shouldGetAllTodos() {
                List<Todo> todos = todoClient.getTodos();

                assertNotNull(todos);
                assertFalse(todos.isEmpty());
        }

        @Test
        public void shouldGetTodoById() {
                Todo todo = todoClient.getTodoById(5);
                assertNotNull(todo);
                assertEquals(5, todo.getId());
                assertEquals(1, todo.getUserId());
                System.out.println("ID: " + todo.getId() + ", User ID: " + todo.getUserId());

        }

        @Test
        public void shouldCountTodosByUser() {
                List<Todo> todos = todoClient.getTodos();
                Map<Integer, Integer> countByUserId = TodoUtils.countTodosByUser(todos);
                assertFalse(countByUserId.isEmpty());
                System.out.println("Count of Todos by User ID: " + countByUserId);
        }

        @Test
        public void shouldCalculateCompletionPercentage() {
                List<Todo> todos = todoClient.getTodos();
                double completedPercentage = TodoUtils.calculateCompletionPercentage(todos);
                assertEquals(45.0, completedPercentage, 0.001);
        }

        @Test
        public void shouldIdentifyUserWithMostTodos() {
                List<Todo> todos = todoClient.getTodos();
                Map<Integer, Integer> countTodosByUser = TodoUtils.countTodosByUser(todos);
                int userIdWithMostTodos = TodoUtils.findUserWithMostTodos(countTodosByUser);
                int winningTodoCount = countTodosByUser.get(userIdWithMostTodos);
                assertFalse(countTodosByUser.isEmpty());
                assertTrue(userIdWithMostTodos > 0);
                assertTrue(winningTodoCount > 0);
                System.out.println(
                                " User Id: " + userIdWithMostTodos + " has the most Todos with " + winningTodoCount
                                                + " todos.");
        }

        @Test
        public void shouldFilterTodosByUserId() {
                List<Todo> todos = todoClient.getTodosByUserId(5);
                assertFalse(todos.isEmpty());
                for (Todo todo : todos) {
                        assertEquals(5, todo.getUserId());
                }
        }

        @Test
        public void shouldCreateTodoSuccessfully() {
                Todo todo = new Todo();
                todo.setUserId(1);
                todo.setCompleted(false);
                todo.setTitle("Practice POST request");
                Response createdTodo = todoClient.createTodo(todo);

                assertNotNull(createdTodo, "The POST response should not be null");
                assertEquals(201, createdTodo.statusCode(), "The POST should return status 201");
                Todo createdTodoBody = createdTodo.as(Todo.class);

                assertEquals(1, createdTodoBody.getUserId(), "The user ID should be 1");
                assertEquals("Practice POST request", createdTodoBody.getTitle(), "The title is incorrect");
                assertFalse(createdTodoBody.isCompleted(), "The new Todo should be incomplete");
                assertTrue(createdTodoBody.getId() > 0, "The created Todo should have an ID");
        }

        @Test
        public void shouldUpdateTodoSuccessfully() {
                System.out.println("Step 1: Create a new Todo to update");
                Todo todo = new Todo();
                todo.setUserId(1);
                todo.setCompleted(false);
                todo.setTitle("Created Todo");

                Response createdTodo = todoClient.createTodo(todo);
                assertEquals(201, createdTodo.statusCode(), "Create request should return HTTP 201");

                Todo createdTodoBody = createdTodo.as(Todo.class);
                System.out.println("Created Todo: " + createdTodoBody.getId() + ", " + createdTodoBody.getTitle()
                                + ", completed=" + createdTodoBody.isCompleted());
                assertFalse(createdTodoBody.isCompleted(), "A newly created Todo should start as incomplete");
                assertEquals("Created Todo", createdTodoBody.getTitle(), "The title should match the created value");
                assertEquals(1, createdTodoBody.getUserId(), "The user ID should match the test input");

                int id = createdTodoBody.getId();
                System.out.println("Step 2: Update the Todo with id=" + id);
                todo.setCompleted(true);
                todo.setTitle("Updated Todo");

                Response patchResp = todoClient.patchTodo(id, todo);
                assertEquals(200, patchResp.statusCode(), "Update request should return HTTP 200");

                Todo patchTodo = patchResp.as(Todo.class);
                System.out.println("Updated Todo: " + patchTodo.getId() + ", " + patchTodo.getTitle() + ", completed="
                                + patchTodo.isCompleted());
                assertTrue(patchTodo.isCompleted(), "The Todo should be marked completed after the update");
                assertEquals("Updated Todo", patchTodo.getTitle(), "The updated title should be returned from the API");
                assertEquals(1, patchTodo.getUserId(), "The user ID should remain unchanged after update");
        }

        @Test
        public void shouldPatchTodoSuccessfully() {

                /*
                 * In a real API test, the preferred approach would be:
                 *
                 * 1. Create a new Todo
                 * 2. Capture the generated ID
                 * 3. PATCH that newly created Todo
                 * 4. Verify the updated values
                 * 5. Clean up the test data
                 *
                 * JSONPlaceholder does not actually persist records created with POST.
                 * It returns a simulated created response, but the new Todo does not
                 * really exist in the backend afterward.
                 *
                 * Because of that limitation, this test uses an existing Todo ID
                 * so the PATCH request can be tested reliably.
                 */

                Todo todo = new Todo();
                todo.setId(5);
                todo.setUserId(1);
                todo.setCompleted(true);
                todo.setTitle("Updated Todo");

                Response patchResp = todoClient.patchTodo(5, todo);

                assertEquals(200, patchResp.statusCode(),
                                "PATCH request should return HTTP 200");

                Todo patchedTodo = patchResp.as(Todo.class);

                System.out.println(
                                "Updated Todo by Patch: "
                                                + patchedTodo.getId()
                                                + ", "
                                                + patchedTodo.getTitle()
                                                + ", completed="
                                                + patchedTodo.isCompleted());

                assertEquals(5, patchedTodo.getId(),
                                "The Todo ID should remain 5 for the requested resource");

                assertEquals(1, patchedTodo.getUserId(),
                                "The user ID should remain unchanged");

                assertEquals("Updated Todo", patchedTodo.getTitle(),
                                "The updated title should be returned");

                assertTrue(patchedTodo.isCompleted(),
                                "The Todo should be marked completed");
        }

        @Test
        public void shouldPutTodoSuccessfully() {
                /*
                 * PUT replaces the full Todo record for an existing ID.
                 * JSONPlaceholder is a mock API, so the response comes back as a
                 * simulated updated object, not from a real persisted database.
                 *
                 * We use a known ID (5) so the request is predictable and the
                 * response can be validated reliably.
                 */
                Todo todo = new Todo();
                todo.setId(5);
                todo.setUserId(1);
                todo.setTitle("PUT Updated Todo");
                todo.setCompleted(true);

                // Send the PUT request to replace the Todo at ID 5.
                Response putResp = todoClient.updateTodo(5, todo);

                assertEquals(200, putResp.getStatusCode(),
                                "PUT request should return HTTP 200");

                // Deserialize the response body from the API into a Todo object.
                Todo updatedTodo = putResp.as(Todo.class);

                System.out.println(
                                "Updated By PUT Todo: "
                                                + updatedTodo.getId()
                                                + ", "
                                                + updatedTodo.getTitle()
                                                + ", completed="
                                                + updatedTodo.isCompleted());

                // Validate the values returned by the mocked API response.
                assertEquals(5, updatedTodo.getId(),
                                "The Todo ID should remain 5 for the requested resource");
                assertEquals(1, updatedTodo.getUserId(),
                                "The user ID should remain unchanged");
                assertEquals("PUT Updated Todo", updatedTodo.getTitle(),
                                "The PUT response should return the updated title");
                assertTrue(updatedTodo.isCompleted(),
                                "The Todo should be marked completed after PUT");
        }

        @Test
        public void shouldDeleteTodoSuccessfully() {
                Response deleteResp = todoClient.deleteTodo(5);
                assertEquals(200, deleteResp.getStatusCode(),
                                "DELETE request should retrun HTTP 200");
        }

        // Neg test cases..

        @Test
        public void shouldReturn404ForInvalidTodoId() {
                Todo todo = new Todo();
                todo.setId(999999);
                Response todoResp = todoClient.getTodoResponseById(todo.getId());
                assertEquals(404,todoResp.getStatusCode());
        }
}
