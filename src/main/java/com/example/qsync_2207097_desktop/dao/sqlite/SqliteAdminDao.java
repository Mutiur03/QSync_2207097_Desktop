package com.example.qsync_2207097_desktop.dao.sqlite;

import com.example.qsync_2207097_desktop.config.DatabaseConfig;
import com.example.qsync_2207097_desktop.dao.AdminDao;
import com.example.qsync_2207097_desktop.model.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqliteAdminDao implements AdminDao {
    private final DatabaseConfig dbConfig;

    public SqliteAdminDao(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    @Override
    public long insert(Admin admin) {
        String sql = "INSERT INTO admins(name,email,password_hash,created_at) VALUES(?,?,?,?)";
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, admin.getName());
            ps.setString(2, admin.getEmail());
            ps.setString(3, admin.getPasswordHash());
            ps.setLong(4, admin.getCreatedAt());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Admin findByEmail(String email) {
        String sql = "SELECT id,name,email,password_hash,created_at FROM admins WHERE email = ?";
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Admin a = new Admin();
                    a.setId(rs.getLong("id"));
                    a.setName(rs.getString("name"));
                    a.setEmail(rs.getString("email"));
                    a.setPasswordHash(rs.getString("password_hash"));
                    a.setCreatedAt(rs.getLong("created_at"));
                    return a;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

