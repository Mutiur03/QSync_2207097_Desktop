package com.example.qsync_2207097_desktop.service;

import com.example.qsync_2207097_desktop.config.DatabaseConfig;
import com.example.qsync_2207097_desktop.dao.DoctorDao;
import com.example.qsync_2207097_desktop.dao.sqlite.SqliteDoctorDao;
import com.example.qsync_2207097_desktop.model.Doctor;

import java.util.List;
import java.util.regex.Pattern;

public class DoctorService {
    private final DoctorDao dao;
    private final DepartmentService departmentService;

    private static final Pattern EMAIL_REGEX = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public DoctorService(DatabaseConfig dbConfig) {
        this.dao = new SqliteDoctorDao(dbConfig);
        this.departmentService = new DepartmentService(dbConfig);
    }

    public List<Doctor> listByDepartment(long departmentId) {
        return dao.getByDepartment(departmentId);
    }

    public Doctor getById(String id) {
        return dao.getById(id);
    }

    public void create(Doctor d) {
        validate(d, true);
        int inserted = dao.insert(d);
        if (inserted <= 0) throw new RuntimeException("Insert failed");
    }

    public void update(Doctor d) {
        validate(d, false);
        int updated = dao.update(d);
        if (updated <= 0) throw new RuntimeException("Update failed");
    }

    public void delete(String id) {
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("Doctor id required");
        dao.delete(id);
    }

    private void validate(Doctor d, boolean requireId) {
        if (d == null) throw new IllegalArgumentException("Doctor is required");
        if (d.getDepartmentId() <= 0) throw new IllegalArgumentException("Valid department required");
        if (departmentService.getAllDepartments().stream().noneMatch(dep -> dep.getId() == d.getDepartmentId())) throw new IllegalArgumentException("Department does not exist");
        if (d.getName() == null || d.getName().trim().isEmpty()) throw new IllegalArgumentException("Doctor name required");
        if (d.getEmail() != null && !d.getEmail().trim().isEmpty()) {
            if (!EMAIL_REGEX.matcher(d.getEmail().trim()).matches()) throw new IllegalArgumentException("Invalid email");
        }
        if (d.getVgTimeMinutes() < 0) throw new IllegalArgumentException("vgTimeMinutes must be >= 0");
        if (d.getYearsOfExperience() < 0) throw new IllegalArgumentException("yearsOfExperience must be >= 0");
        if (d.getStartTime() != null && !d.getStartTime().trim().isEmpty()) {
            if (!d.getStartTime().matches("^(?:[01]\\d|2[0-3]):[0-5]\\d$")) throw new IllegalArgumentException("startTime must be in HH:mm format");
        }
    }
}
