package com.example.qsync_2207097_desktop.dao.sqlite;

import com.example.qsync_2207097_desktop.config.DatabaseConfig;
import com.example.qsync_2207097_desktop.dao.AppointmentDao;
import com.example.qsync_2207097_desktop.model.Appointment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SqliteAppointmentDao implements AppointmentDao {
    private final DatabaseConfig dbConfig;

    public SqliteAppointmentDao(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    @Override
    public long insert(Appointment a) {
        String sql = "INSERT INTO appointments (patient_id, patient_name, patient_phone, date, start_time, end_time, start_ts, end_ts, status, notes, priority, doctor_id, department_id, token, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, strftime('%s','now'))";
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (a.getPatientId() != null) ps.setLong(1, a.getPatientId()); else ps.setObject(1, null);
            ps.setString(2, a.getPatientName());
            ps.setString(3, a.getPatientPhone());
            ps.setString(4, a.getDate());
            ps.setString(5, a.getStartTime());
            ps.setString(6, a.getEndTime());
            ps.setLong(7, a.getStartTs());
            ps.setLong(8, a.getEndTs());
            ps.setString(9, a.getStatus());
            ps.setString(10, a.getNotes());
            ps.setString(11, a.getPriority());
            ps.setString(12, a.getDoctorId());
            if (a.getDepartmentId() != null) ps.setLong(13, a.getDepartmentId()); else ps.setObject(13, null);
            if (a.getToken() != null) ps.setInt(14, a.getToken()); else ps.setObject(14, null);
            int affected = ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
            return affected;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int updateStatus(long appointmentId, String status) {
        String sql = "UPDATE appointments SET status = ?, updated_at = strftime('%s','now') WHERE id = ?";
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, appointmentId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Appointment getById(long id) {
        String sql = "SELECT id,patient_id,patient_name,patient_phone,date,start_time,end_time,start_ts,end_ts,status,notes,priority,doctor_id,department_id,token,created_at,updated_at FROM appointments WHERE id = ?";
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Appointment> listByDate(String date, int limit, int offset) {
        String sql = "SELECT id,patient_id,patient_name,patient_phone,date,start_time,end_time,start_ts,end_ts,status,notes,priority,doctor_id,department_id,token,created_at,updated_at FROM appointments WHERE date = ? ORDER BY start_ts ASC LIMIT ? OFFSET ?";
        List<Appointment> list = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, date);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Appointment> listByPatient(long patientId, int limit, int offset) {
        String sql = "SELECT id,patient_id,patient_name,patient_phone,date,start_time,end_time,start_ts,end_ts,status,notes,priority,doctor_id,department_id,token,created_at,updated_at FROM appointments WHERE patient_id = ? ORDER BY start_ts DESC LIMIT ? OFFSET ?";
        List<Appointment> list = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasConflict(long requestedStartTs, long requestedEndTs) {
        String sql = "SELECT COUNT(1) FROM appointments WHERE status IN ('waiting','in_progress') AND NOT (end_ts <= ? OR start_ts >= ?)";
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, requestedStartTs);
            ps.setLong(2, requestedEndTs);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int delete(long id) {
        String sql = "DELETE FROM appointments WHERE id = ?";
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getMaxTokenForDoctorOnDate(String doctorId, String date) {
        String sql = "SELECT MAX(token) FROM appointments WHERE doctor_id = ? AND date = ?";
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, doctorId);
            ps.setString(2, date);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setId(rs.getLong("id"));
        long pid = rs.getLong("patient_id");
        if (!rs.wasNull()) a.setPatientId(pid);
        a.setPatientName(rs.getString("patient_name"));
        a.setPatientPhone(rs.getString("patient_phone"));
        a.setDate(rs.getString("date"));
        a.setStartTime(rs.getString("start_time"));
        a.setEndTime(rs.getString("end_time"));
        a.setStartTs(rs.getLong("start_ts"));
        a.setEndTs(rs.getLong("end_ts"));
        a.setStatus(rs.getString("status"));
        a.setNotes(rs.getString("notes"));
        a.setPriority(rs.getString("priority"));
        a.setCreatedAt(rs.getLong("created_at"));
        long u = rs.getLong("updated_at"); if (!rs.wasNull()) a.setUpdatedAt(u);
        a.setDoctorId(rs.getString("doctor_id"));
        long did = rs.getLong("department_id"); if (!rs.wasNull()) a.setDepartmentId(did);
        int t = rs.getInt("token"); if (!rs.wasNull()) a.setToken(t);
        return a;
    }
}

