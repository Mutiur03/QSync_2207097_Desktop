package com.example.qsync_2207097_desktop.service;

import com.example.qsync_2207097_desktop.config.DatabaseConfig;
import com.example.qsync_2207097_desktop.dao.DepartmentDao;
import com.example.qsync_2207097_desktop.dao.sqlite.SqliteDepartmentDao;
import com.example.qsync_2207097_desktop.model.Department;

import java.util.List;

public class DepartmentService {
    private final DepartmentDao dao;

    public DepartmentService(DatabaseConfig dbConfig) {
        this.dao = new SqliteDepartmentDao(dbConfig);
    }

    public List<Department> getAllDepartments() {
        return dao.getAll();
    }

    public Department createDepartment(String name, String description) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Department name required");
        Department d = new Department();
        d.setName(name.trim());
        d.setDescription(description == null ? "" : description.trim());
        long id = dao.insert(d);
        d.setId(id);
        return d;
    }

    public Department updateDepartment(long id, String name, String description) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Department name required");
        Department d = new Department();
        d.setId(id);
        d.setName(name.trim());
        d.setDescription(description == null ? "" : description.trim());
        int updated = dao.update(d);
        if (updated <= 0) throw new RuntimeException("Update failed");
        return d;
    }

    public boolean deleteDepartment(long id) {
        return dao.delete(id) > 0;
    }
}

