package com.example.qsync_2207097_desktop.dao.sqlite;

import com.example.qsync_2207097_desktop.config.DatabaseConfig;
import com.example.qsync_2207097_desktop.dao.DoctorDao;
import com.example.qsync_2207097_desktop.model.Doctor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqliteDoctorDao implements DoctorDao {
    private final DatabaseConfig dbConfig;

    public SqliteDoctorDao(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    @Override
    public List<Doctor> getAll() {
        String sql = "SELECT id,department_id,name,email,phone,specialty,start_time,vg_time_minutes,years_of_experience,created_at FROM doctors ORDER BY name ASC";
        List<Doctor> list = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Doctor d = mapRow(rs);
                list.add(d);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Doctor> getByDepartment(long departmentId) {
        String sql = "SELECT id,department_id,name,email,phone,specialty,start_time,vg_time_minutes,years_of_experience,created_at FROM doctors WHERE department_id = ? ORDER BY name ASC";
        List<Doctor> list = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Doctor d = mapRow(rs);
                    list.add(d);
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Doctor getById(String id) {
        String sql = "SELECT id,department_id,name,email,phone,specialty,start_time,vg_time_minutes,years_of_experience,created_at FROM doctors WHERE id = ?";
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int insert(Doctor d) {
        String sql = "INSERT INTO doctors(department_id,name,email,phone,specialty,start_time,vg_time_minutes,years_of_experience) VALUES(?,?,?,?,?,?,?,?)";
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, d.getDepartmentId());
            ps.setString(2, d.getName());
            ps.setString(3, d.getEmail());
            ps.setString(4, d.getPhone());
            ps.setString(5, d.getSpecialty());
            ps.setString(6, d.getStartTime());
            ps.setInt(7, d.getVgTimeMinutes());
            ps.setInt(8, d.getYearsOfExperience());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int update(Doctor d) {
        String sql = "UPDATE doctors SET department_id = ?, name = ?, email = ?, phone = ?, specialty = ?, start_time = ?, vg_time_minutes = ?, years_of_experience = ? WHERE id = ?";
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, d.getDepartmentId());
            ps.setString(2, d.getName());
            ps.setString(3, d.getEmail());
            ps.setString(4, d.getPhone());
            ps.setString(5, d.getSpecialty());
            ps.setString(6, d.getStartTime());
            ps.setInt(7, d.getVgTimeMinutes());
            ps.setInt(8, d.getYearsOfExperience());
            ps.setString(9, d.getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int delete(String id) {
        String sql = "DELETE FROM doctors WHERE id = ?";
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Doctor mapRow(ResultSet rs) throws SQLException {
        Doctor d = new Doctor();
        d.setId(rs.getString("id"));
        d.setDepartmentId(rs.getLong("department_id"));
        d.setName(rs.getString("name"));
        d.setEmail(rs.getString("email"));
        d.setPhone(rs.getString("phone"));
        d.setSpecialty(rs.getString("specialty"));
        d.setStartTime(rs.getString("start_time"));
        d.setVgTimeMinutes(rs.getInt("vg_time_minutes"));
        d.setYearsOfExperience(rs.getInt("years_of_experience"));
        d.setCreatedAt(rs.getLong("created_at"));
        return d;
    }
}

