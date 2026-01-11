package com.example.qsync_2207097_desktop.user;

import com.example.qsync_2207097_desktop.UserController;
import com.example.qsync_2207097_desktop.model.User;
import com.example.qsync_2207097_desktop.service.UserService;
import com.example.qsync_2207097_desktop.service.SessionManager;
import com.example.qsync_2207097_desktop.config.DatabaseConfig;
import com.example.qsync_2207097_desktop.service.PasswordUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.beans.binding.Bindings;

public class UserSettingsFragmentController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private DatePicker dobPicker;

    @FXML
    private ChoiceBox<String> genderChoiceBox;

    @FXML
    private Button saveProfileButton;

    @FXML
    private Label profileMessageLabel;

    @FXML
    private PasswordField currentPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button changePasswordButton;

    @FXML
    private Label passwordMessageLabel;

    @FXML
    private ScrollPane settingsScroll;

    @FXML
    private StackPane centerStack;

    private UserService userService;
    private User currentUser;

    public void setParent(UserController parent) {
    }

    @FXML
    public void initialize() {
        userService = new UserService(new DatabaseConfig());
        genderChoiceBox.getItems().addAll("Male", "Female", "Other", "Prefer not to say");
        centerStack.minHeightProperty().bind(Bindings.createDoubleBinding(
                () -> settingsScroll.getViewportBounds().getHeight(),
                settingsScroll.viewportBoundsProperty()
        ));

        loadCurrentUser();
    }

    private void loadCurrentUser() {
        String email = SessionManager.getEmail();
        if (email == null) return;
        Task<User> task = new Task<>() {
            @Override
            protected User call() {
                return userService.getByEmail(email);
            }
        };
        task.setOnSucceeded(ev -> {
            currentUser = task.getValue();
            if (currentUser != null) {
                nameField.setText(currentUser.getName());
                emailField.setText(currentUser.getEmail());
                phoneField.setText(currentUser.getPhone());
                try {
                    if (currentUser.getDob() != null && !currentUser.getDob().isEmpty()) {
                        dobPicker.setValue(java.time.LocalDate.parse(currentUser.getDob()));
                    }
                } catch (Exception ignored) {}
                if (currentUser.getGender() != null) genderChoiceBox.setValue(currentUser.getGender());
            }
        });
        task.setOnFailed(ev -> {
            profileMessageLabel.setText("Failed to load user");
        });
        new Thread(task).start();
    }

    @FXML
    private void onSaveProfile() {
        clearProfileMessage();
        String name = nameField.getText() != null ? nameField.getText().trim() : "";
        String email = emailField.getText() != null ? emailField.getText().trim() : "";
        String phone = phoneField.getText() != null ? phoneField.getText().trim() : "";
        String dob = dobPicker.getValue() != null ? dobPicker.getValue().toString() : null;
        String gender = genderChoiceBox.getValue() != null ? genderChoiceBox.getValue() : null;

        if (name.isEmpty()) {
            profileMessageLabel.setText("Name is required");
            return;
        }
        if (email.isEmpty() || !email.contains("@")) {
            profileMessageLabel.setText("Valid email is required");
            return;
        }

        saveProfileButton.setDisable(true);
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                try {
                    User u = new User();
                    u.setId(currentUser != null ? currentUser.getId() : -1);
                    u.setName(name);
                    u.setEmail(email);
                    u.setPhone(phone);
                    u.setDob(dob);
                    u.setGender(gender);
                    if (currentUser != null) u.setPasswordHash(currentUser.getPasswordHash());
                    u.setCreatedAt(currentUser != null ? currentUser.getCreatedAt() : System.currentTimeMillis());
                    int updated = userService.updateProfile(u);
                    return updated > 0;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return false;
                }
            }
        };
        task.setOnSucceeded(ev -> {
            saveProfileButton.setDisable(false);
            boolean ok = task.getValue();
            if (ok) {
                String currentRole = SessionManager.getRole();
                if (currentRole != null) {
                    SessionManager.saveSession(currentRole, email);
                }
                profileMessageLabel.setText("Profile saved");
                loadCurrentUser();
            } else {
                profileMessageLabel.setText("Failed to save profile");
            }
        });
        task.setOnFailed(ev -> {
            saveProfileButton.setDisable(false);
            profileMessageLabel.setText("Failed to save profile");
        });
        new Thread(task).start();
    }

    @FXML
    private void onChangePassword() {
        clearPasswordMessage();
        char[] current = currentPasswordField.getText() != null ? currentPasswordField.getText().toCharArray() : new char[0];
        char[] nw = newPasswordField.getText() != null ? newPasswordField.getText().toCharArray() : new char[0];
        char[] confirm = confirmPasswordField.getText() != null ? confirmPasswordField.getText().toCharArray() : new char[0];

        if (current.length == 0) {
            passwordMessageLabel.setText("Current password required");
            return;
        }
        if (nw.length < 8) {
            passwordMessageLabel.setText("New password must be at least 8 chars");
            return;
        }
        if (!java.util.Arrays.equals(nw, confirm)) {
            passwordMessageLabel.setText("Passwords do not match");
            return;
        }

        changePasswordButton.setDisable(true);
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                try {
                    String email = currentUser != null ? currentUser.getEmail() : SessionManager.getEmail();
                    if (email == null) return false;
                    User u = userService.authenticate(email, current);
                    if (u == null) return false;
                    String salt = PasswordUtils.generateSalt();
                    String hash = PasswordUtils.hashPassword(nw, salt);
                    String stored = salt + "$" + hash;
                    int updated = userService.updatePasswordByEmail(u.getEmail(), stored);
                    return updated > 0;
                } finally {
                    java.util.Arrays.fill(current, '\0');
                    java.util.Arrays.fill(nw, '\0');
                    java.util.Arrays.fill(confirm, '\0');
                }
            }
        };
        task.setOnSucceeded(ev -> {
            changePasswordButton.setDisable(false);
            boolean ok = task.getValue();
            if (ok) {
                passwordMessageLabel.setText("Password changed");
                currentPasswordField.clear();
                newPasswordField.clear();
                confirmPasswordField.clear();
            } else {
                passwordMessageLabel.setText("Failed to change password (check current password)");
            }
        });
        task.setOnFailed(ev -> {
            changePasswordButton.setDisable(false);
            Throwable ex = task.getException();
            passwordMessageLabel.setText("Failed: " + (ex != null ? ex.getMessage() : "Unknown error"));
            if (ex != null) ex.printStackTrace();
        });
        new Thread(task).start();
    }

    private void clearProfileMessage() {
        Platform.runLater(() -> profileMessageLabel.setText(""));
    }

    private void clearPasswordMessage() {
        passwordMessageLabel.setText("");
    }
}
