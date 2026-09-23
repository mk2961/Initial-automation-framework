package com.company.automation.integration.todo.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.company.automation.config.ConfigManager;
import com.company.automation.config.SecretManager;

/**
 * Minimal JDBC client used only by persistence/integration tests.
 *
 * API contract tests should stay at the HTTP boundary. Direct SQL belongs in
 * this integration layer when a test specifically needs to prove that an API
 * operation persisted (or removed) the expected database state.
 */
public class TodoDbClient {

    private static final String DB_URL = ConfigManager.get("db.url");
    private static final String DB_USER = SecretManager.get("DB_USERNAME");
    private static final String DB_PASS = SecretManager.get("DB_PASSWORD");

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    public boolean todoExistsById(int id) throws SQLException {
        String sql = """
                SELECT ID
                FROM TODO
                WHERE ID = ?
                """;

        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public TodoDbRecord getTodoById(int id) throws SQLException {
        String sql = """
                SELECT ID, USER_ID, TITLE, COMPLETED
                FROM TODO
                WHERE ID = ?
                """;

        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                return new TodoDbRecord(
                        resultSet.getInt("ID"),
                        resultSet.getInt("USER_ID"),
                        resultSet.getString("TITLE"),
                        resultSet.getBoolean("COMPLETED"));
            }
        }
    }
}
