package com.example.qsync_2207097_desktop.service;

import java.util.prefs.Preferences;

public class SessionManager {
    private static final Preferences PREFS = Preferences.userRoot().node("/com/example/qsync_2207097_desktop/session");
    private static final String KEY_ROLE = "role";
    private static final String KEY_EMAIL = "email";

    public static void saveSession(String role, String email) {
        if (role == null || email == null) return;
        PREFS.put(KEY_ROLE, role);
        PREFS.put(KEY_EMAIL, email);
    }

    public static void clearSession() {
        PREFS.remove(KEY_ROLE);
        PREFS.remove(KEY_EMAIL);
    }

    public static boolean hasSession() {
        return PREFS.get(KEY_ROLE, null) != null;
    }

    public static String getRole() {
        return PREFS.get(KEY_ROLE, null);
    }

    public static String getEmail() {
        return PREFS.get(KEY_EMAIL, null);
    }
}


