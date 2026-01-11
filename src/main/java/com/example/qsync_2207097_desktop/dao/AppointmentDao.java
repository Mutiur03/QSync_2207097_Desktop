package com.example.qsync_2207097_desktop.dao;

import com.example.qsync_2207097_desktop.model.Appointment;

import java.util.List;

public interface AppointmentDao {
    long insert(Appointment a);
    int updateStatus(long appointmentId, String status);
    Appointment getById(long id);
    List<Appointment> listByDate(String date, int limit, int offset);
    List<Appointment> listByDoctorAndDate(String doctorId, String date);
    List<Appointment> listByPatient(long patientId, int limit, int offset);
    boolean hasConflict(long requestedStartTs, long requestedEndTs);
    int delete(long id);
    int getMaxTokenForDoctorOnDate(String doctorId, String date);
}
