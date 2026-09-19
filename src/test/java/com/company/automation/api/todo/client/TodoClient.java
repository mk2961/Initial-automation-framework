package com.company.automation.api.todo.client;

import static io.restassured.RestAssured.given;

import java.util.List;

import com.company.automation.api.todo.model.Todo;
import com.company.automation.config.ConfigManager;
import com.company.automation.core.RequestSpecFactory;

import io.restassured.response.Response;

/**
 * REST client for the Todo API.
 *
 * Keeping HTTP details here prevents tests from duplicating endpoint paths,
 * request specifications, path parameters, and query-parameter wiring.
 * Methods return domain objects when the caller only needs data and raw
 * {@link Response} objects when a test needs to validate the HTTP contract.
 */
public class TodoClient {

    private static final String BASE_URL = ConfigManager.get("todo.api.url");
    private static final String TODOS_ENDPOINT = "/todos";

    public List<Todo> getTodos() {
        return given()
                .spec(RequestSpecFactory.getRequestSpec(BASE_URL))
                .get(TODOS_ENDPOINT)
                .jsonPath()
                .getList("$", Todo.class);
    }

    public Todo getTodoById(int id) {
        return given()
                .spec(RequestSpecFactory.getRequestSpec(BASE_URL))
                .pathParam("id", id)
                .get(TODOS_ENDPOINT + "/{id}")
                .as(Todo.class);
    }

    public Response getTodoResponseById(int id) {
        return given()
                .spec(RequestSpecFactory.getRequestSpec(BASE_URL))
                .pathParam("id", id)
                .get(TODOS_ENDPOINT + "/{id}");
    }

    /**
     * String overload is intentional: negative-contract tests can send values
     * that cannot be represented by the normal integer ID method.
     */
    public Response getTodoResponseByIdStringId(String id) {
        return given()
                .spec(RequestSpecFactory.getRequestSpec(BASE_URL))
                .pathParam("id", id)
                .get(TODOS_ENDPOINT + "/{id}");
    }

    public List<Todo> getTodosByUserId(int userId) {
        return given()
                .spec(RequestSpecFactory.getRequestSpec(BASE_URL))
                .queryParam("userId", userId)
                .get(TODOS_ENDPOINT)
                .jsonPath()
                .getList("$", Todo.class);
    }

    public List<Todo> getTodosByCompletionStatus(boolean completed) {
        return given()
                .spec(RequestSpecFactory.getRequestSpec(BASE_URL))
                .queryParam("completed", completed)
                .get(TODOS_ENDPOINT)
                .jsonPath()
                .getList("$", Todo.class);
    }

    public List<Todo> getTodosByUserIdAndCompletionStatus(int userId, boolean completed) {
        return given()
                .spec(RequestSpecFactory.getRequestSpec(BASE_URL))
                .queryParam("userId", userId)
                .queryParam("completed", completed)
                .get(TODOS_ENDPOINT)
                .jsonPath()
                .getList("$", Todo.class);
    }

    public List<Todo> getTodosByTitle(String title) {
        return given()
                .spec(RequestSpecFactory.getRequestSpec(BASE_URL))
                .queryParam("title", title)
                .get(TODOS_ENDPOINT)
                .jsonPath()
                .getList("$", Todo.class);
    }

    public List<Todo> getTodosByUserIdAndTitle(int userId, String title) {
        return given()
                .spec(RequestSpecFactory.getRequestSpec(BASE_URL))
                .queryParam("userId", userId)
                .queryParam("title", title)
                .get(TODOS_ENDPOINT)
                .jsonPath()
                .getList("$", Todo.class);
    }

    public List<Todo> getTodosByCompletionStatusAndTitle(boolean completed, String title) {
        return given()
                .spec(RequestSpecFactory.getRequestSpec(BASE_URL))
                .queryParam("completed", completed)
                .queryParam("title", title)
                .get(TODOS_ENDPOINT)
                .jsonPath()
                .getList("$", Todo.class);
    }

    public List<Todo> getTodosByUserIdCompletionStatusAndTitle(
            int userId,
            boolean completed,
            String title) {

        return given()
                .spec(RequestSpecFactory.getRequestSpec(BASE_URL))
                .queryParam("userId", userId)
                .queryParam("completed", completed)
                .queryParam("title", title)
                .get(TODOS_ENDPOINT)
                .jsonPath()
                .getList("$", Todo.class);
    }

    public Response createTodo(Todo todo) {
        return given()
                .spec(RequestSpecFactory.getRequestSpec(BASE_URL))
                .body(todo)
                .post(TODOS_ENDPOINT);
    }

    public Response updateTodo(int id, Todo todo) {
        return given()
                .spec(RequestSpecFactory.getRequestSpec(BASE_URL))
                .pathParam("id", id)
                .body(todo)
                .put(TODOS_ENDPOINT + "/{id}");
    }

    public Response patchTodo(int id, Todo todo) {
        return given()
                .spec(RequestSpecFactory.getRequestSpec(BASE_URL))
                .pathParam("id", id)
                .body(todo)
                .patch(TODOS_ENDPOINT + "/{id}");
    }

    public Response deleteTodo(int id) {
        return given()
                .spec(RequestSpecFactory.getRequestSpec(BASE_URL))
                .pathParam("id", id)
                .delete(TODOS_ENDPOINT + "/{id}");
    }
}
