package com.example.qsync_2207097_desktop.dao;

import com.example.qsync_2207097_desktop.model.Department;

import java.util.List;

public interface DepartmentDao {
    List<Department> getAll();
    Department getById(long id);
    long insert(Department d);
    int update(Department d);
    int delete(long id);
}

