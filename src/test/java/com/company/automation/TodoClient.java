package com.company.automation;
import java.util.List;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;




public class TodoClient {

    private static final String TODOS_ENDPOINT = "/todos";

    public List<Todo> getTodos() {
        Response resp = given()
            .spec(RequestSpecFactory.getRequestSpec())
            .get(TODOS_ENDPOINT);
        List<Todo> todos = resp.jsonPath().getList("$", Todo.class);    
        return todos;  
    }

    public Todo getTodoById(int id) {
        Response resp = given()
            .spec(RequestSpecFactory.getRequestSpec())
            .pathParam("id", id)
            .get(TODOS_ENDPOINT + "/{id}");
        return resp.as(Todo.class); 
    }

    public Response getTodoResponseById(int id){
        Response resp = given()
            .spec(RequestSpecFactory.getRequestSpec())
            .pathParam("id", id)
            .get(TODOS_ENDPOINT + "/{id}");
        return resp;
    }

    public List<Todo> getTodosByUserId(int userId) {
        Response resp = given()
            .spec(RequestSpecFactory.getRequestSpec())
            .queryParam("userId", userId)
            .get(TODOS_ENDPOINT);
        List<Todo> todos = resp.jsonPath().getList("$", Todo.class);    
        return todos;  
    }

    public List<Todo> getTodosByCompletionStatus(boolean completed) {
        Response resp = given()
            .spec(RequestSpecFactory.getRequestSpec())
            .queryParam("completed", completed)
            .get(TODOS_ENDPOINT);
        List<Todo> todos = resp.jsonPath().getList("$", Todo.class);    
        return todos;  
    }

    public List<Todo> getTodosByUserIdAndCompletionStatus(int userId, boolean completed) {
        Response resp = given()
            .spec(RequestSpecFactory.getRequestSpec())
            .queryParam("userId", userId)
            .queryParam("completed", completed)
            .get(TODOS_ENDPOINT);
        List<Todo> todos = resp.jsonPath().getList("$", Todo.class);    
        return todos;  
    }

    public List<Todo> getTodosByTitle(String title) {
        Response resp = given()
            .spec(RequestSpecFactory.getRequestSpec())
            .queryParam("title",title)
            .get(TODOS_ENDPOINT);
        List<Todo> todos = resp.jsonPath().getList("$", Todo.class);    
        return todos;  
    }
    
    public List<Todo> getTodosByUserIdAndTitle(int userId, String title) {
        Response resp = given()
            .spec(RequestSpecFactory.getRequestSpec())
            .queryParam("userId",userId)
            .queryParam("title", title)
            .get(TODOS_ENDPOINT);
        List<Todo> todos = resp.jsonPath().getList("$", Todo.class);    
        return todos;  
    }

    public List<Todo> getTodosByCompletionStatusAndTitle(boolean completed, String title) {
        Response resp = given()
            .spec(RequestSpecFactory.getRequestSpec())
            .queryParam("completed", completed)
            .queryParam("title", title)
            .get(TODOS_ENDPOINT);
        List<Todo> todos = resp.jsonPath().getList("$", Todo.class);    
        return todos;  
    }
    
    public List<Todo> getTodosByUserIdCompletionStatusAndTitle(int userId, boolean completed, String title) {
        Response resp = given()
            .spec(RequestSpecFactory.getRequestSpec())
            .queryParam("userId", userId)
            .queryParam("completed", completed)
            .queryParam("title", title)
            .get(TODOS_ENDPOINT);
        List<Todo> todos = resp.jsonPath().getList("$", Todo.class);    
        return todos;  
    }

    public Response createTodo(Todo todo){
        Response resp = given()
            .spec(RequestSpecFactory.getRequestSpec())
            .body(todo)
            .post(TODOS_ENDPOINT);

        return resp;
    }

    public Response updateTodo(int id, Todo todo){
        Response resp =  given()
            .spec(RequestSpecFactory.getRequestSpec())
            .pathParam("id", id)
            .body(todo)
            .put(TODOS_ENDPOINT + "/{id}" );    
        return resp;
    }

    public Response patchTodo(int id, Todo todo){
        Response resp = given()
        .spec(RequestSpecFactory.getRequestSpec())
        .pathParam("id", id)
        .body(todo)
        .patch(TODOS_ENDPOINT + "/{id}");

        return resp;
    }

    public Response deleteTodo(int id ){

        Response resp = given()
        .spec(RequestSpecFactory.getRequestSpec())
        .pathParam("id", id)
        .delete(TODOS_ENDPOINT + "/{id}");

        return resp;
    }
    
}