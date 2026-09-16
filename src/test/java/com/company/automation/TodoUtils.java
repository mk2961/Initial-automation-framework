package com.company.automation;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class TodoUtils {

    public static Map<Integer, Integer> countIncompleteTodosByUser(List<Todo> todos) {
        Map<Integer, Integer> incompleteUsers = new HashMap<>();
        for (Todo todo : todos) {
            int userId = todo.getUserId();
            boolean isCompleted = todo.isCompleted();
            if (!isCompleted) {
                int currentCount = incompleteUsers.getOrDefault(userId, 0);
                incompleteUsers.put(userId, currentCount + 1);
            }
        }
        return incompleteUsers;
    }

    public static Map<Integer, Integer> countCompletedTodosByUser(List<Todo> todos) {
        Map<Integer, Integer> completedUsers = new HashMap<>();
        for (Todo todo : todos) {
            int userId = todo.getUserId();
            boolean isCompleted = todo.isCompleted();
            if (isCompleted) {
                int currentCount = completedUsers.getOrDefault(userId, 0);
                completedUsers.put(userId, currentCount + 1);
            }
        }
        return completedUsers;
    }

    public static List<Integer> findUsersAboveThreshold(Map<Integer, Integer> countByUserId, int threshold) {
        List<Integer> ids = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : countByUserId.entrySet()) {
            if (entry.getValue() > threshold) {
                ids.add(entry.getKey());
            }
        }
        return ids;
    }

    public static Map<Integer, Integer> countTodosByUser(List<Todo> todos) {
        Map<Integer, Integer> countByUserId = new HashMap<>();
        for (Todo todo : todos) {
            int userId = todo.getUserId();
            int currentCount = countByUserId.getOrDefault(userId, 0);
            countByUserId.put(userId, currentCount + 1);
        }
        return countByUserId;
    }

    public static int findUserWithMostTodos(Map<Integer, Integer> countByUserId) {
        int maxCount = 0;
        int userIdWithMaxTodos = -1;
        for (Map.Entry<Integer, Integer> entry : countByUserId.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                userIdWithMaxTodos = entry.getKey();
            }
        }
        return userIdWithMaxTodos;

    }

    public static double calculateCompletionPercentage(List<Todo> todos) {
        if (todos.isEmpty()) {
            return 0.0; // Avoid division by zero
        }
        int totalTodos = todos.size();
        int completedTodos = 0;
        for (Todo todo : todos) {
            if (todo.isCompleted()) {
                completedTodos++;
            }
        }
        return (double) completedTodos / totalTodos * 100;
    }

    public static void prettyPrintTodos(List<Todo> todos) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            System.out.println(mapper.writeValueAsString(todos));
        } catch (Exception e) {
            throw new RuntimeException("Unable to format todos as JSON", e);
        }
    }
}