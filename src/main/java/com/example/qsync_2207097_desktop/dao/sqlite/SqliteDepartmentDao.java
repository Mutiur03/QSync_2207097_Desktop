package com.example.qsync_2207097_desktop.dao.sqlite;

import com.example.qsync_2207097_desktop.config.DatabaseConfig;
import com.example.qsync_2207097_desktop.dao.DepartmentDao;
import com.example.qsync_2207097_desktop.model.Department;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqliteDepartmentDao implements DepartmentDao {
    private final DatabaseConfig dbConfig;

    public SqliteDepartmentDao(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    @Override
    public List<Department> getAll() {
        String sql = "SELECT id,name,description FROM departments ORDER BY name ASC";
        List<Department> list = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Department d = new Department();
                d.setId(rs.getLong("id"));
                d.setName(rs.getString("name"));
                d.setDescription(rs.getString("description"));
                list.add(d);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Department getById(long id) {
        String sql = "SELECT id,name,description FROM departments WHERE id = ?";
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Department d = new Department();
                    d.setId(rs.getLong("id"));
                    d.setName(rs.getString("name"));
                    d.setDescription(rs.getString("description"));
                    return d;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long insert(Department d) {
        String sql = "INSERT INTO departments(name,description) VALUES(?,?)";
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, d.getName());
            ps.setString(2, d.getDescription());
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
    public int update(Department d) {
        String sql = "UPDATE departments SET name = ?, description = ? WHERE id = ?";
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getName());
            ps.setString(2, d.getDescription());
            ps.setLong(3, d.getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int delete(long id) {
        String sql = "DELETE FROM departments WHERE id = ?";
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

