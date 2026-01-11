package com.example.qsync_2207097_desktop.dao.sqlite;

import com.example.qsync_2207097_desktop.config.DatabaseConfig;
import com.example.qsync_2207097_desktop.dao.UserDao;
import com.example.qsync_2207097_desktop.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqliteUserDao implements UserDao {
    private final DatabaseConfig dbConfig;

    public SqliteUserDao(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    @Override
    public long insert(User user) {
        String sql = "INSERT INTO users(name,email,password_hash,dob,gender,phone,created_at) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getDob());
            ps.setString(5, user.getGender());
            ps.setString(6, user.getPhone());
            ps.setLong(7, user.getCreatedAt());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User findByEmail(String email) {
        String sql = "SELECT id,name,email,password_hash,dob,gender,phone,created_at FROM users WHERE email = ?";
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setId(rs.getLong("id"));
                    u.setName(rs.getString("name"));
                    u.setEmail(rs.getString("email"));
                    u.setPasswordHash(rs.getString("password_hash"));
                    u.setDob(rs.getString("dob"));
                    u.setGender(rs.getString("gender"));
                    u.setPhone(rs.getString("phone"));
                    u.setCreatedAt(rs.getLong("created_at"));
                    return u;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int update(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, password_hash = ?, dob = ?, gender = ?, phone = ? WHERE id = ?";
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getDob());
            ps.setString(5, user.getGender());
            ps.setString(6, user.getPhone());
            ps.setLong(7, user.getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int updatePasswordByEmail(String email, String passwordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE email = ?";
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, passwordHash);
            ps.setString(2, email);
            return ps.executeUpdate();
        } catch (SQLException e) {
             throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT id,name,email,password_hash,dob,gender,phone,created_at FROM users";
        List<User> list = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getLong("id"));
                u.setName(rs.getString("name"));
                u.setEmail(rs.getString("email"));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setDob(rs.getString("dob"));
                u.setGender(rs.getString("gender"));
                u.setPhone(rs.getString("phone"));
                u.setCreatedAt(rs.getLong("created_at"));
                list.add(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}
