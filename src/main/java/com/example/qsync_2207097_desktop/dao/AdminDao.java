package com.example.qsync_2207097_desktop.dao;

import com.example.qsync_2207097_desktop.model.Admin;

public interface AdminDao {
    long insert(Admin admin);
    Admin findByEmail(String email);
}

