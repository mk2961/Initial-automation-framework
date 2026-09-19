package com.company.automation.integration.todo.database;

/**
 * Database-facing representation of a TODO row.
 *
 * This stays separate from the API Todo model so persistence tests make the
 * source of each value explicit and can tolerate future API/DB schema drift.
 */
public record TodoDbRecord(
        int id,
        int userId,
        String title,
        boolean completed) {
}
