package com.example.qsync_2207097_desktop;

import com.example.qsync_2207097_desktop.config.DatabaseConfig;
import com.example.qsync_2207097_desktop.model.User;
import com.example.qsync_2207097_desktop.service.SessionManager;
import com.example.qsync_2207097_desktop.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

public class HelloController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private DatePicker dobPicker;

    @FXML
    private ChoiceBox<String> genderChoice;

    @FXML
    private TextField phoneField;

    @FXML
    private PasswordField registerPassword;

    @FXML
    private TextField loginEmail;

    @FXML
    private PasswordField loginPassword;

    private UserService userService;

    private UserService getUserService() {
        if (userService == null) {
            userService = new UserService(new DatabaseConfig());
        }
        return userService;
    }

    @FXML
    public void initialize() {
        try {
            if (genderChoice != null) {
                genderChoice.getItems().clear();
                genderChoice.getItems().addAll("Male", "Female", "Other");
                genderChoice.setValue("Male");
            }
        } catch (Exception ex) {
            System.err.println("Controller init warning: " + ex.getMessage());
        }
    }

    @FXML
    protected void openRegistration(ActionEvent event) throws IOException {
        Parent regRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("registration-view.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(regRoot);
    }

    @FXML
    protected void openLogin(ActionEvent event) throws IOException {
        Parent loginRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("hello-view.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(loginRoot);
    }

    @FXML
    protected void openAdminLogin(ActionEvent event) throws IOException {
        Parent adminRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("admin-login.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(adminRoot);
    }

    @FXML
    protected void signIn() {
        try {
            String email = loginEmail.getText().trim();
            char[] pwd = loginPassword.getText().toCharArray();
            User u = getUserService().authenticate(email, pwd);
            if (u != null) {
                SessionManager.saveSession("user", u.getEmail());
                System.out.println("Login success: " + u.getEmail());
                Alert a = new Alert(Alert.AlertType.INFORMATION, "Login successful. Welcome " + u.getName());
                a.showAndWait();
                Stage stage = (Stage) loginEmail.getScene().getWindow();
                Parent home = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("user-home.fxml")));
                stage.getScene().setRoot(home);
            } else {
                Alert a = new Alert(Alert.AlertType.ERROR, "Invalid credentials");
                a.showAndWait();
            }
        } catch (Exception ex) {
            Alert a = new Alert(Alert.AlertType.ERROR, "Login failed: " + ex.getMessage());
            a.showAndWait();
        }
    }

    @FXML
    protected void register() {
        try {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String gender = genderChoice.getValue();
            LocalDate d = dobPicker.getValue();
            String dob = d != null ? d.toString() : null;
            if (name.isEmpty() || email.isEmpty()) {
                Alert a = new Alert(Alert.AlertType.WARNING, "Name and email are required");
                a.showAndWait();
                return;
            }
            char[] pwd = registerPassword != null ? registerPassword.getText().toCharArray() : new char[0];
            if (pwd.length == 0) {
                Alert a = new Alert(Alert.AlertType.WARNING, "Please enter a password");
                a.showAndWait();
                return;
            }
            long id = getUserService().register(name, email, pwd, dob, gender, phone);
            if (id > 0) {
                Alert a = new Alert(Alert.AlertType.INFORMATION, "Registration successful. You can now sign in.");
                a.showAndWait();
                Stage stage = (Stage) nameField.getScene().getWindow();
                Parent loginRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("hello-view.fxml")));
                stage.getScene().setRoot(loginRoot);
            }
        } catch (IllegalArgumentException ia) {
            Alert a = new Alert(Alert.AlertType.WARNING, ia.getMessage());
            a.showAndWait();
        } catch (Exception ex) {
            Alert a = new Alert(Alert.AlertType.ERROR, "Registration failed: " + ex.getMessage());
            a.showAndWait();
        }
    }
}
