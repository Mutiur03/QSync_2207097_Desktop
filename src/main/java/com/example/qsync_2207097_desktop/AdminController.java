package com.example.qsync_2207097_desktop;

import com.example.qsync_2207097_desktop.config.DatabaseConfig;
import com.example.qsync_2207097_desktop.model.Admin;
import com.example.qsync_2207097_desktop.service.AdminService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class AdminController {
    @FXML
    private TextField adminEmail;

    @FXML
    private PasswordField adminPassword;

    private AdminService adminService;

    private AdminService getAdminService() {
        if (adminService == null) adminService = new AdminService(new DatabaseConfig());
        return adminService;
    }

    @FXML
    protected void adminSignIn() {
        try {
            String email = adminEmail.getText().trim();
            char[] pwd = adminPassword.getText().toCharArray();
            Admin a = getAdminService().authenticate(email, pwd);
            if (a != null) {
                Alert info = new Alert(Alert.AlertType.INFORMATION, "Admin signed in: " + a.getName());
                info.showAndWait();
                Stage stage = (Stage) adminEmail.getScene().getWindow();
                Parent home = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("admin-home.fxml")));
                stage.getScene().setRoot(home);
            } else {
                Alert err = new Alert(Alert.AlertType.ERROR, "Invalid admin credentials");
                err.showAndWait();
            }
        } catch (Exception ex) {
            Alert err = new Alert(Alert.AlertType.ERROR, "Admin sign-in failed: " + ex.getMessage());
            err.showAndWait();
        }
    }

    @FXML
    protected void signOut(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent login = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("hello-view.fxml")));
        stage.getScene().setRoot(login);
    }
}
