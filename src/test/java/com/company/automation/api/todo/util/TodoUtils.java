package com.company.automation.api.todo.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.company.automation.api.todo.model.Todo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


/**
 * Pure collection/analysis helpers for Todo data.
 *
 * Keep HTTP calls and test-data creation out of this class so these methods
 * remain deterministic and independently testable.
 */
public final class TodoUtils {

    private TodoUtils() {
        // Utility class; do not instantiate.
    }

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
            return 0.0;
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