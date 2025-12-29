package com.example.qsync_2207097_desktop;

import com.example.qsync_2207097_desktop.service.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class UserHomeController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label emailLabel;

    @FXML
    public void initialize() {
        String email = SessionManager.getEmail();
        emailLabel.setText(email != null ? email : "(unknown)");
        welcomeLabel.setText("Welcome back");
    }

    @FXML
    protected void signOut(ActionEvent event) throws IOException {
        SessionManager.clearSession();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent login = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("hello-view.fxml")));
        stage.getScene().setRoot(login);
    }
}

