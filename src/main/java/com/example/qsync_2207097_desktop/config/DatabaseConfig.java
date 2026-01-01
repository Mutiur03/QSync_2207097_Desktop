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
            "CREATE TABLE IF NOT EXISTS users (\n" +
            "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    name TEXT NOT NULL,\n" +
            "    email TEXT NOT NULL UNIQUE,\n" +
            "    password_hash TEXT NOT NULL,\n" +
            "    dob TEXT,\n" +
            "    gender TEXT,\n" +
            "    phone TEXT,\n" +
            "    created_at INTEGER NOT NULL DEFAULT (strftime('%s','now'))\n" +
            ");",
            "CREATE TABLE IF NOT EXISTS admins (\n" +
            "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    name TEXT NOT NULL,\n" +
            "    email TEXT NOT NULL UNIQUE,\n" +
            "    password_hash TEXT NOT NULL,\n" +
            "    created_at INTEGER NOT NULL DEFAULT (strftime('%s','now'))\n" +
            ");",
            "INSERT INTO users (name, email, password_hash) VALUES ('Default User', 'mutiur5bb@gmail.com', '') ON CONFLICT(email) DO NOTHING",
            "INSERT INTO admins (name, email, password_hash) VALUES ('Administrator', 'admin@example.com', '') ON CONFLICT(email) DO NOTHING",
            "CREATE TABLE IF NOT EXISTS departments (\n" +
            "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    name TEXT NOT NULL UNIQUE,\n" +
            "    description TEXT\n" +
            ");",
            "CREATE TABLE IF NOT EXISTS doctors (\n" +
            "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    department_id INTEGER NOT NULL,\n" +
            "    name TEXT NOT NULL,\n" +
            "    email TEXT,\n" +
            "    phone TEXT,\n" +
            "    specialty TEXT,\n" +
            "    start_time TEXT,\n" +
            "    vg_time_minutes INTEGER DEFAULT 0,\n" +
            "    years_of_experience INTEGER DEFAULT 0,\n" +
            "    created_at INTEGER NOT NULL DEFAULT (strftime('%s','now')),\n" +
            "    FOREIGN KEY(department_id) REFERENCES departments(id) ON DELETE CASCADE\n" +
            ");",
            "CREATE TABLE IF NOT EXISTS appointments (\n" +
            "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    patient_id INTEGER,\n" +
            "    patient_name TEXT NOT NULL,\n" +
            "    patient_phone TEXT,\n" + "doctor_id TEXT,\n"+ "department_id INTEGER,\n"+
            "    date TEXT NOT NULL,\n" + "token INTEGER,\n"+
            "    start_time TEXT NOT NULL,\n" +
            "    end_time TEXT NOT NULL,\n" +
            "    start_ts INTEGER NOT NULL,\n" +
            "    end_ts INTEGER NOT NULL,\n" +
            "    status TEXT NOT NULL DEFAULT 'waiting',\n" +
            "    notes TEXT,\n" +
            "    created_at INTEGER NOT NULL DEFAULT (strftime('%s','now')),\n" +
            "    updated_at INTEGER,\n" +
            "    CHECK (start_ts < end_ts)\n" +
            ");",
            "INSERT OR IGNORE INTO departments (name) VALUES ('Cardiology')",
            "INSERT OR IGNORE INTO doctors (department_id, name) SELECT id, 'Mutiur Rahman' FROM departments WHERE name = 'Cardiology'",
        };

        try (Statement stmt = conn.createStatement()) {
            for (String sql : sqlCommands) {
                stmt.execute(sql);
            }
        }
    }
}
