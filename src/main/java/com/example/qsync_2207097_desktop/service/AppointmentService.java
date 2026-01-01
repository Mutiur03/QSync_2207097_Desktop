package com.example.qsync_2207097_desktop.service;

import com.example.qsync_2207097_desktop.config.DatabaseConfig;
import com.example.qsync_2207097_desktop.dao.AppointmentDao;
import com.example.qsync_2207097_desktop.dao.sqlite.SqliteAppointmentDao;
import com.example.qsync_2207097_desktop.model.Appointment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class AppointmentService {
    private final DatabaseConfig dbConfig;
    private final AppointmentDao appointmentDao;

    public AppointmentService(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
        this.appointmentDao = new SqliteAppointmentDao(dbConfig);
    }

    public long bookAppointmentTransactional(Appointment a) {
        try (Connection conn = dbConfig.getConnection()) {
            try {
                conn.setAutoCommit(false);

                Integer nextToken = null;
                if (a.getDoctorId() != null && a.getDate() != null) {
                    String tokenSql = "SELECT MAX(token) FROM appointments WHERE doctor_id = ? AND date = ?";
                    try (PreparedStatement tps = conn.prepareStatement(tokenSql)) {
                        tps.setString(1, a.getDoctorId());
                        tps.setString(2, a.getDate());
                        try (ResultSet trs = tps.executeQuery()) {
                            int current = 0;
                            if (trs.next()) current = trs.getInt(1);
                            nextToken = current + 1;
                        }
                    }
                }

                String insertSql = "INSERT INTO appointments (patient_id, patient_name, patient_phone, date, start_time, end_time, start_ts, end_ts, status, notes, doctor_id, department_id, token, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, strftime('%s','now'))";
                try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
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
                    ps.setString(11, a.getDoctorId());
                    if (a.getDepartmentId() != null) ps.setLong(12, a.getDepartmentId()); else ps.setObject(12, null);
                    if (nextToken != null) ps.setInt(13, nextToken); else ps.setObject(13, null);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            long id = keys.getLong(1);
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

    public boolean isAvailable(long startTs, long endTs) {
        return !appointmentDao.hasConflict(startTs, endTs);
    }

    public int changeStatus(long appointmentId, String status) {
        return appointmentDao.updateStatus(appointmentId, status);
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
