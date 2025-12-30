package com.example.qsync_2207097_desktop.dao;

import com.example.qsync_2207097_desktop.model.Doctor;

import java.util.List;

public interface DoctorDao {
    List<Doctor> getAll();
    List<Doctor> getByDepartment(long departmentId);
    Doctor getById(String id);
    int insert(Doctor d);
    int update(Doctor d);
    int delete(String id);
}

