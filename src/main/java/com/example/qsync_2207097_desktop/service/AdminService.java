package com.example.qsync_2207097_desktop.service;

import com.example.qsync_2207097_desktop.config.DatabaseConfig;
import com.example.qsync_2207097_desktop.dao.AdminDao;
import com.example.qsync_2207097_desktop.dao.sqlite.SqliteAdminDao;
import com.example.qsync_2207097_desktop.model.Admin;

import java.time.Instant;

public class AdminService {
    private final AdminDao adminDao;

    public AdminService(DatabaseConfig dbConfig) {
        this.adminDao = new SqliteAdminDao(dbConfig);
        ensureDefaultAdmin();
    }

    private void ensureDefaultAdmin() {
        try {
            Admin a = adminDao.findByEmail("admin@example.com");
            if (a == null) {
                String salt = PasswordUtils.generateSalt();
                String hash = PasswordUtils.hashPassword("admin123".toCharArray(), salt);
                String stored = salt + "$" + hash;
                Admin admin = new Admin();
                admin.setName("Administrator");
                admin.setEmail("admin@example.com");
                admin.setPasswordHash(stored);
                admin.setCreatedAt(Instant.now().toEpochMilli());
                adminDao.insert(admin);
                System.out.println("Default admin created: admin@example.com / admin123");
            }
        } catch (Exception ex) {
            System.err.println("Failed to ensure default admin: " + ex.getMessage());
        }
    }

    public Admin authenticate(String email, char[] password) {
        Admin a = adminDao.findByEmail(email);
        if (a == null) return null;
        String stored = a.getPasswordHash();
        if (stored == null || !stored.contains("$")) return a;
        String[] parts = stored.split("\\$", 2);
        String salt = parts[0];
        String hash = parts[1];
        boolean ok = PasswordUtils.verifyPassword(password, salt, hash);
        return ok ? a : null;
    }
}

