package com.example.qsync_2207097_desktop.dao;

import com.example.qsync_2207097_desktop.model.User;

import java.util.List;

public interface UserDao {
    long insert(User user);
    User findByEmail(String email);
    int update(User user);
    int updatePasswordByEmail(String email, String passwordHash);
    List<User> findAll();
}
