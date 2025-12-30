package com.example.qsync_2207097_desktop;

import com.example.qsync_2207097_desktop.admin.AdminHomeFragmentController;
import com.example.qsync_2207097_desktop.admin.AdminUsersFragmentController;
import com.example.qsync_2207097_desktop.admin.AdminSettingsFragmentController;
import com.example.qsync_2207097_desktop.admin.AdminReportsFragmentController;
import com.example.qsync_2207097_desktop.config.DatabaseConfig;
import com.example.qsync_2207097_desktop.model.Admin;
import com.example.qsync_2207097_desktop.service.AdminService;
import com.example.qsync_2207097_desktop.service.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class AdminController {
    @FXML
    private TextField adminEmail;

    @FXML
    private PasswordField adminPassword;

    @FXML
    private StackPane contentStack;

    @FXML
    private Pane homePane;

    @FXML
    private Pane usersPane;

    @FXML
    private Pane settingsPane;

    @FXML
    private Pane reportsPane;

    @FXML
    private javafx.scene.control.Button btnDashboard;

    @FXML
    private javafx.scene.control.Button btnUsers;

    @FXML
    private javafx.scene.control.Button btnSettings;

    @FXML
    private javafx.scene.control.Button btnReports;

    @FXML
    private javafx.scene.control.Button btnSignOut;

    private AdminService adminService;

    private AdminService getAdminService() {
        if (adminService == null) adminService = new AdminService(new DatabaseConfig());
        return adminService;
    }
    @FXML
    public void initialize() {
        try {
            FXMLLoader homeLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("admin/admin-home-fragment.fxml")));
            Parent homeRoot = homeLoader.load();
            AdminHomeFragmentController homeController = homeLoader.getController();
            FXMLLoader usersLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource( "admin/admin-users-fragment.fxml")));
            Parent usersRoot = usersLoader.load();
            AdminUsersFragmentController usersController = usersLoader.getController();
            FXMLLoader settingsLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource( "admin/admin-settings-fragment.fxml")));
            Parent settingsRoot = settingsLoader.load();
            AdminSettingsFragmentController settingsController = settingsLoader.getController();
            FXMLLoader reportsLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("admin/admin-reports-fragment.fxml")));
            Parent reportsRoot = reportsLoader.load();
            AdminReportsFragmentController reportsController = reportsLoader.getController();
            if (homeController != null) homeController.setParent(this);
            if (usersController != null) usersController.setParent(this);
            if (settingsController != null) settingsController.setParent(this);
            if (reportsController != null) reportsController.setParent(this);
            homeRoot.setVisible(true);
            homeRoot.setManaged(true);
            usersRoot.setVisible(false);
            usersRoot.setManaged(false);
            settingsRoot.setVisible(false);
            settingsRoot.setManaged(false);
            reportsRoot.setVisible(false);
            reportsRoot.setManaged(false);

            contentStack.getChildren().addAll(homeRoot, usersRoot, settingsRoot, reportsRoot);

            homePane = (Pane) homeRoot;
            usersPane = (Pane) usersRoot;
            settingsPane = (Pane) settingsRoot;
            reportsPane = (Pane) reportsRoot;

            showPane(homePane);
        } catch (Exception ex) {
            System.err.println("[AdminController] Failed to load fragments: " + ex);
        }
    }
    @FXML
    protected void adminSignIn() {
        try {
            String email = adminEmail.getText().trim();
            char[] pwd = adminPassword.getText().toCharArray();
            Admin a = getAdminService().authenticate(email, pwd);
            if (a != null) {
                SessionManager.saveSession("admin", a.getEmail());
                Alert info = new Alert(Alert.AlertType.INFORMATION, "Admin signed in: " + a.getName());
                info.showAndWait();
                Stage stage = (Stage) adminEmail.getScene().getWindow();
                Parent home = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("admin/admin-home.fxml")));
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
        SessionManager.clearSession();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent login = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("user/hello-view.fxml")));
        stage.getScene().setRoot(login);
    }

    @FXML
    public void showHome(ActionEvent event) {
        Objects.requireNonNull(event);
        showPane(homePane);
    }

    @FXML
    public void showUsers(ActionEvent event) {
        Objects.requireNonNull(event);
        showPane(usersPane);
    }

    @FXML
    public void showSettings(ActionEvent event) {
        Objects.requireNonNull(event);
        showPane(settingsPane);
    }

    @FXML
    public void showReports(ActionEvent event) {
        Objects.requireNonNull(event);
        showPane(reportsPane);
    }

    private void showPane(Pane paneToShow) {
        if (contentStack == null) return;
        for (Node child : contentStack.getChildren()) {
            if (child instanceof Pane p) {
                boolean show = p == paneToShow;
                p.setVisible(show);
                p.setManaged(show);
            }
        }

        try {
            btnDashboard.getStyleClass().remove("active");
            btnUsers.getStyleClass().remove("active");
            btnSettings.getStyleClass().remove("active");
            btnReports.getStyleClass().remove("active");
        } catch (Exception ignored) {}

        if (paneToShow == homePane) addActive(btnDashboard);
        else if (paneToShow == usersPane) addActive(btnUsers);
        else if (paneToShow == settingsPane) addActive(btnSettings);
        else if (paneToShow == reportsPane) addActive(btnReports);
    }

    private void addActive(javafx.scene.control.Button btn) {
        if (btn == null) return;
        if (!btn.getStyleClass().contains("active")) btn.getStyleClass().add("active");
    }
}
