package com.example.qsync_2207097_desktop.service;

import com.example.qsync_2207097_desktop.config.DatabaseConfig;
import com.example.qsync_2207097_desktop.dao.UserDao;
import com.example.qsync_2207097_desktop.dao.sqlite.SqliteUserDao;
import com.example.qsync_2207097_desktop.model.User;

import java.time.Instant;

public class UserService {
    private final UserDao userDao;

    public UserService(DatabaseConfig dbConfig) {
        this.userDao = new SqliteUserDao(dbConfig);
    }

    public long register(String name, String email, char[] password, String dob, String gender, String phone) {
        // check exists
        if (userDao.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email already registered");
        }
        String salt = PasswordUtils.generateSalt();
        String hash = PasswordUtils.hashPassword(password, salt);
        // store salt and hash together: salt$hash
        String stored = salt + "$" + hash;
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPasswordHash(stored);
        u.setDob(dob);
        u.setGender(gender);
        u.setPhone(phone);
        u.setCreatedAt(Instant.now().toEpochMilli());
        return userDao.insert(u);
    }

    public User authenticate(String email, char[] password) {
        User u = userDao.findByEmail(email);
        if (u == null) return null;
        String stored = u.getPasswordHash();
        if (stored == null || !stored.contains("$")) return null;
        String[] parts = stored.split("\\$", 2);
        String salt = parts[0];
        String hash = parts[1];
        boolean ok = PasswordUtils.verifyPassword(password, salt, hash);
        return ok ? u : null;
    }
}

