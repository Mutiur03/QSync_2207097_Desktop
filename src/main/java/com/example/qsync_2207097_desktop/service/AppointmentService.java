package com.example.qsync_2207097_desktop.service;

import com.example.qsync_2207097_desktop.config.DatabaseConfig;
import com.example.qsync_2207097_desktop.dao.AppointmentDao;
import com.example.qsync_2207097_desktop.dao.sqlite.SqliteAppointmentDao;
import com.example.qsync_2207097_desktop.model.Appointment;
import com.example.qsync_2207097_desktop.service.DoctorService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class AppointmentService {
    private final DatabaseConfig dbConfig;
    private final AppointmentDao appointmentDao;
    private final DoctorService doctorService; 

    public AppointmentService(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
        this.appointmentDao = new SqliteAppointmentDao(dbConfig);
        this.doctorService = new DoctorService(dbConfig); 
    }

    public long bookAppointmentTransactional(Appointment a) {
        try (Connection conn = dbConfig.getConnection()) {
            try {
                conn.setAutoCommit(false);

                Integer nextToken = null;
                int activeCount = 0;
                if (a.getDoctorId() != null && a.getDate() != null) {
                    String tokenSql = "SELECT MAX(token) FROM appointments WHERE doctor_id = ? AND date = ?";
                    try (PreparedStatement tps = conn.prepareStatement(tokenSql)) {
                        tps.setString(1, a.getDoctorId());
                        tps.setString(2, a.getDate());
                        try (ResultSet trs = tps.executeQuery()) {
                            int currentMax = 0;
                            if (trs.next()) currentMax = trs.getInt(1);
                            nextToken = currentMax + 1;
                        }
                    }
                    
                    String countSql = "SELECT COUNT(id) FROM appointments WHERE doctor_id = ? AND date = ? AND status IN ('waiting', 'in_progress')";
                    try (PreparedStatement cps = conn.prepareStatement(countSql)) {
                        cps.setString(1, a.getDoctorId());
                        cps.setString(2, a.getDate());
                        try (ResultSet crs = cps.executeQuery()) {
                            if (crs.next()) activeCount = crs.getInt(1);
                        }
                    }
                }

                String insertSql = "INSERT INTO appointments (patient_id, patient_name, patient_phone, date, start_time, end_time, start_ts, end_ts, status, notes, priority, doctor_id, department_id, token, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, strftime('%s','now'))";
                try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    String doctorStartTime = "09:00";
                    int avgVisitTime = 15;
                    if (a.getDoctorId() != null) {
                        try {
                            com.example.qsync_2207097_desktop.model.Doctor d = doctorService.getById(a.getDoctorId());
                            if (d != null) {
                                if (d.getStartTime() != null && !d.getStartTime().isEmpty()) doctorStartTime = d.getStartTime();
                                if (d.getVgTimeMinutes() > 0) avgVisitTime = d.getVgTimeMinutes();
                            }
                        } catch (Exception ignored) {}
                    }

                    java.time.LocalTime baseTime = java.time.LocalTime.parse(doctorStartTime);
                    java.time.LocalTime startTime = baseTime.plusMinutes((long) activeCount * avgVisitTime);
                    java.time.LocalTime endTime = startTime.plusMinutes(avgVisitTime);
                    
                    a.setStartTime(startTime.toString());
                    a.setEndTime(endTime.toString());
                    
                    try {
                        java.time.LocalDate ld = java.time.LocalDate.parse(a.getDate());
                        a.setStartTs(ld.atTime(startTime).toEpochSecond(java.time.ZoneOffset.UTC));
                        a.setEndTs(ld.atTime(endTime).toEpochSecond(java.time.ZoneOffset.UTC));
                    } catch (Exception ignored) {}

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
                    if (a.getDepartmentId() != null) ps.setLong(13, a.getDepartmentId()); else ps.setNull(13, java.sql.Types.BIGINT);
                    if (nextToken != null) ps.setInt(14, nextToken); else ps.setNull(14, java.sql.Types.INTEGER);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        long id = keys.getLong(1);
                        recalculateDoctorQueueTimes(a.getDoctorId(), a.getDate(), conn);
                        conn.commit();
                        return id;
                    }
                    }
                }
                conn.commit();
                return -1L;
            } catch (SQLException ex) {
                conn.rollback();
                throw new RuntimeException(ex);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void recalculateDoctorQueueTimes(String doctorId, String date, Connection conn) throws SQLException {
        if (doctorId == null || date == null) return;
        
        String doctorStartTime = "09:00";
        int avgVisitTime = 15;
        try {
            com.example.qsync_2207097_desktop.model.Doctor d = doctorService.getById(doctorId);
            if (d != null) {
                if (d.getStartTime() != null && !d.getStartTime().isEmpty()) doctorStartTime = d.getStartTime();
                if (d.getVgTimeMinutes() > 0) avgVisitTime = d.getVgTimeMinutes();
            }
        } catch (Exception ignored) {}

        java.time.LocalTime baseTime = java.time.LocalTime.parse(doctorStartTime);
        java.time.LocalDate ld = java.time.LocalDate.parse(date);

        String sql = "SELECT id FROM appointments WHERE doctor_id = ? AND date = ? AND status IN ('waiting', 'in_progress') ORDER BY token ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, doctorId);
            ps.setString(2, date);
            try (ResultSet rs = ps.executeQuery()) {
                int index = 0;
                while (rs.next()) {
                    long id = rs.getLong("id");
                    java.time.LocalTime startTime = baseTime.plusMinutes((long) index * avgVisitTime);
                    java.time.LocalTime endTime = startTime.plusMinutes(avgVisitTime);
                    long startTs = ld.atTime(startTime).toEpochSecond(java.time.ZoneOffset.UTC);
                    long endTs = ld.atTime(endTime).toEpochSecond(java.time.ZoneOffset.UTC);

                    String upSql = "UPDATE appointments SET start_time = ?, end_time = ?, start_ts = ?, end_ts = ? WHERE id = ?";
                    try (PreparedStatement ups = conn.prepareStatement(upSql)) {
                        ups.setString(1, startTime.toString());
                        ups.setString(2, endTime.toString());
                        ups.setLong(3, startTs);
                        ups.setLong(4, endTs);
                        ups.setLong(5, id);
                        ups.executeUpdate();
                    }
                    index++;
                }
            }
        }
    }

    public boolean isAvailable(long startTs, long endTs) {
        return !appointmentDao.hasConflict(startTs, endTs);
    }

    public int changeStatus(long appointmentId, String status) {
        int result = appointmentDao.updateStatus(appointmentId, status);
        if (result > 0 && "cancelled".equalsIgnoreCase(status)) {
            Appointment a = appointmentDao.getById(appointmentId);
            if (a != null) {
                try (Connection conn = dbConfig.getConnection()) {
                    recalculateDoctorQueueTimes(a.getDoctorId(), a.getDate(), conn);
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to recalculate queue: " + e.getMessage());
                }
            }
        }
        return result;
    }

    public List<Appointment> listByDate(String date, int limit, int offset) {
        return appointmentDao.listByDate(date, limit, offset);
    }

    public List<Appointment> listByPatient(long patientId, int limit, int offset) {
        return appointmentDao.listByPatient(patientId, limit, offset);
    }

    public Appointment getById(long id) {
        return appointmentDao.getById(id);
    }
}
