package com.example.qsync_2207097_desktop.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConfig {

    public Connection getConnection() throws SQLException {
        String jdbcUrl = "jdbc:sqlite:qsync.db";
        Connection conn = DriverManager.getConnection(jdbcUrl);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
            stmt.execute("PRAGMA busy_timeout = 5000");
            System.out.println("Database connection established");
        }
        catch (SQLException ex) {
            Logger.getLogger(DatabaseConfig.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Database connection failed");
        }
        return conn;
    }

    public void initializeDatabase() {
        try (Connection conn = getConnection()) {
            executeSqlCommands(conn);
        } catch (Exception e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void executeSqlCommands(Connection conn) throws SQLException {
        String[] sqlCommands = {
            "PRAGMA foreign_keys = ON;",
            "CREATE TABLE IF NOT EXISTS queues (\n" +
            "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    name TEXT NOT NULL,\n" +
            "    avg_service_time INTEGER NOT NULL DEFAULT 5,\n" +
            "    current_token INTEGER NOT NULL DEFAULT 0,\n" +
            "    created_at INTEGER NOT NULL\n" +
            ");",
            "CREATE TABLE IF NOT EXISTS queue_entries (\n" +
            "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    queue_id INTEGER NOT NULL REFERENCES queues(id) ON DELETE CASCADE,\n" +
            "    token_number INTEGER NOT NULL,\n" +
            "    joined_at INTEGER NOT NULL\n" +
            ");",
            "CREATE TABLE IF NOT EXISTS users (\n" +
            "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    name TEXT NOT NULL,\n" +
            "    email TEXT NOT NULL UNIQUE,\n" +
            "    password_hash TEXT NOT NULL,\n" +
            "    dob TEXT,\n" +
            "    gender TEXT,\n" +
            "    phone TEXT,\n" +
            "    created_at INTEGER NOT NULL\n" +
            ");",
            "CREATE TABLE IF NOT EXISTS admins (\n" +
            "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    name TEXT NOT NULL,\n" +
            "    email TEXT NOT NULL UNIQUE,\n" +
            "    password_hash TEXT NOT NULL,\n" +
            "    created_at INTEGER NOT NULL\n" +
            ");",
            "CREATE INDEX IF NOT EXISTS idx_queue_entries_queue ON queue_entries(queue_id);",
            "CREATE INDEX IF NOT EXISTS idx_queues_name ON queues(name);",
            "CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);",
            "CREATE INDEX IF NOT EXISTS idx_admins_email ON admins(email);"
        };

        try (Statement stmt = conn.createStatement()) {
            for (String sql : sqlCommands) {
                stmt.execute(sql);
            }
        }
    }
}
