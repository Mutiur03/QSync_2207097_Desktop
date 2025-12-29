package com.example.qsync_2207097_desktop.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConfig {

    private final String jdbcUrl;

    public DatabaseConfig() {
        this.jdbcUrl = "jdbc:sqlite:qsync.db";
    }


    public Connection getConnection() throws SQLException {
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

    public void initializeIfNeeded() {
        try (Connection conn = getConnection()) {
            runInitScript(conn);
        } catch (Exception e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void runInitScript(Connection conn) throws IOException, SQLException {
        try (InputStream in = DatabaseConfig.class.getResourceAsStream("/db/init.sql")) {
            if (in == null) {
                throw new IOException("Could not find /db/init.sql on classpath");
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                String line;
                StringBuilder sql = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sql.append(line).append('\n');
                }
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(sql.toString());
                }
            }
        }
    }

}
