package com.example.qsync_2207097_desktop.dao;

import com.example.qsync_2207097_desktop.model.User;

public interface UserDao {
    long insert(User user);
    User findByEmail(String email);
}

