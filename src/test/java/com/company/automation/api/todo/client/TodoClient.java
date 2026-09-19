package com.company.automation.api.todo.client;

import java.util.List;

import com.company.automation.api.todo.model.Todo;
import com.company.automation.config.ConfigManager;
import com.company.automation.core.RequestSpecFactory;


import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class TodoClient {

    private static final String BASE_URL =
            ConfigManager.get("todo.api.url");

    private static final String TODOS_ENDPOINT = "/todos";

    

    public List<Todo> getTodos() {
        Response resp = given()
                .spec(RequestSpecFactory.getRequestSpec(BASE_URL))
                .get(TODOS_ENDPOINT);

        List<Todo> todos =
                resp.jsonPath().getList("$", Todo.class);

        return todos;
    }

    public Todo getTodoById(int id) {
        Response resp = given()
                .spec(RequestSpecFactory.getRequestSpec(BASE_URL))
                .pathParam("id", id)
                .get(TODOS_ENDPOINT + "/{id}");

        return resp.as(Todo.class);
    }

    public Response getTodoResponseById(int id) {
        Response resp = given()
                .spec(RequestSpecFactory.getRequestSpec(BASE_URL))
                .pathParam("id", id)
                .get(TODOS_ENDPOINT + "/{id}");

        return resp;
    }

    public Response getTodoResponseByIdStringId(String id) {
        Response resp = given()
                .spec(RequestSpecFactory.getRequestSpec(BASE_URL))
                .pathParam("id", id)
                .get(TODOS_ENDPOINT + "/{id}");

        return resp;
    }

    public List<Todo> getTodosByUserId(int userId) {
        Response resp = given()
                .spec(RequestSpecFactory.getRequestSpec(BASE_URL))
                .queryParam("userId", userId)
                .get(TODOS_ENDPOINT);

        List<Todo> todos =
                resp.jsonPath().getList("$", Todo.class);

        return todos;
    }

    public List<Todo> getTodosByCompletionStatus(boolean completed) {
        Response resp = given()
                .spec(RequestSpecFactory.getRequestSpec(BASE_URL))
                .queryParam("completed", completed)
                .get(TODOS_ENDPOINT);

        List<Todo> todos =
                resp.jsonPath().getList("$", Todo.class);

        return todos;
    }

    public List<Todo> getTodosByUserIdAndCompletionStatus(
            int userId,
            boolean completed) {

        Response resp = given()
                .spec(RequestSpecFactory.getRequestSpec(BASE_URL))
                .queryParam("userId", userId)
                .queryParam("completed", completed)
                .get(TODOS_ENDPOINT);

        List<Todo> todos =
                resp.jsonPath().getList("$", Todo.class);

        return todos;
    }

    public List<Todo> getTodosByTitle(String title) {
        Response resp = given()
                .spec(RequestSpecFactory.getRequestSpec(BASE_URL))
                .queryParam("title", title)
                .get(TODOS_ENDPOINT);

        List<Todo> todos =
                resp.jsonPath().getList("$", Todo.class);

        return todos;
    }

    public List<Todo> getTodosByUserIdAndTitle(
            int userId,
            String title) {

        Response resp = given()
                .spec(RequestSpecFactory.getRequestSpec(BASE_URL))
                .queryParam("userId", userId)
                .queryParam("title", title)
                .get(TODOS_ENDPOINT);

        List<Todo> todos =
                resp.jsonPath().getList("$", Todo.class);

        return todos;
    }

    public List<Todo> getTodosByCompletionStatusAndTitle(
            boolean completed,
            String title) {

        Response resp = given()
                .spec(RequestSpecFactory.getRequestSpec(BASE_URL))
                .queryParam("completed", completed)
                .queryParam("title", title)
                .get(TODOS_ENDPOINT);

        List<Todo> todos =
                resp.jsonPath().getList("$", Todo.class);

        return todos;
    }

    public List<Todo> getTodosByUserIdCompletionStatusAndTitle(
            int userId,
            boolean completed,
            String title) {

        Response resp = given()
                .spec(RequestSpecFactory.getRequestSpec(BASE_URL))
                .queryParam("userId", userId)
                .queryParam("completed", completed)
                .queryParam("title", title)
                .get(TODOS_ENDPOINT);

        List<Todo> todos =
                resp.jsonPath().getList("$", Todo.class);

        return todos;
    }

    public Response createTodo(Todo todo) {
        Response resp = given()
                .spec(RequestSpecFactory.getRequestSpec(BASE_URL))
                .body(todo)
                .post(TODOS_ENDPOINT);

        return resp;
    }

    public Response updateTodo(int id, Todo todo) {
        Response resp = given()
                .spec(RequestSpecFactory.getRequestSpec(BASE_URL))
                .pathParam("id", id)
                .body(todo)
                .put(TODOS_ENDPOINT + "/{id}");

        return resp;
    }

    public Response patchTodo(int id, Todo todo) {
        Response resp = given()
                .spec(RequestSpecFactory.getRequestSpec(BASE_URL))
                .pathParam("id", id)
                .body(todo)
                .patch(TODOS_ENDPOINT + "/{id}");

        return resp;
    }

    public Response deleteTodo(int id) {
        Response resp = given()
                .spec(RequestSpecFactory.getRequestSpec(BASE_URL))
                .pathParam("id", id)
                .delete(TODOS_ENDPOINT + "/{id}");

        return resp;
    }
}