package com.example.qsync_2207097_desktop;

import com.example.qsync_2207097_desktop.admin.AdminHomeFragmentController;
import com.example.qsync_2207097_desktop.admin.AdminUsersFragmentController;

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
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class AdminController {
    @FXML
    private javafx.scene.layout.VBox sidebar;

    @FXML
    private TextField adminEmail;

    @FXML
    private PasswordField adminPassword;

    @FXML
    private StackPane contentStack;

    @FXML
    private Node homePane;

    @FXML
    private Node usersPane;



    @FXML
    private Node departmentsPane;

    @FXML
    private Node doctorsPane;

    @FXML
    private javafx.scene.control.Button btnDashboard;

    @FXML
    private javafx.scene.control.Button btnUsers;



    @FXML
    private javafx.scene.control.Button btnDepartments;

    @FXML
    private javafx.scene.control.Button btnDoctors;

    @FXML
    private javafx.scene.control.Button btnAppointments;

    @FXML
    private javafx.scene.control.Button btnQueues;

    @FXML
    private javafx.scene.control.Button btnSignOut;


    private Node appointmentsPane;
    private Node queuesPane;

    private AdminService adminService;

    private com.example.qsync_2207097_desktop.admin.AdminDepartmentsFragmentController departmentsControllerRef;
    private com.example.qsync_2207097_desktop.admin.AdminManageDoctorsController doctorsControllerRef;
    private com.example.qsync_2207097_desktop.admin.AdminQueuesFragmentController queuesControllerRef;

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

            FXMLLoader departmentsLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("admin/admin-departments-fragment.fxml")));
            Parent departmentsRoot = departmentsLoader.load();
            departmentsControllerRef = departmentsLoader.getController();
            FXMLLoader doctorsLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("admin/admin-manage-doctors.fxml")));
            Parent doctorsRoot = doctorsLoader.load();
            doctorsControllerRef = doctorsLoader.getController();
            try {
                FXMLLoader appointmentsLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("admin/admin-appointments-fragment.fxml")));
                Parent appointmentsRoot = appointmentsLoader.load();
                appointmentsPane = appointmentsRoot;
                appointmentsRoot.setVisible(false);
                appointmentsRoot.setManaged(false);
                contentStack.getChildren().add(appointmentsRoot);
            } catch (Exception ignored) {}
            if (homeController != null) homeController.setParent(this);
            if (usersController != null) usersController.setParent(this);

            if (departmentsControllerRef != null) departmentsControllerRef.setParent(this);
            if (doctorsControllerRef != null) doctorsControllerRef.setParent(this);
            try {
                FXMLLoader queuesLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("admin/admin-queues-fragment.fxml")));
                Parent queuesRoot = queuesLoader.load();
                queuesPane = queuesRoot;
                queuesControllerRef = queuesLoader.getController();
                if (queuesControllerRef != null) queuesControllerRef.setParent(this);
                queuesRoot.setVisible(false);
                queuesRoot.setManaged(false);
                contentStack.getChildren().add(queuesRoot);
            } catch (Exception ex) {
                System.err.println("[AdminController] Failed to load queues fragment: " + ex);
            }

            homeRoot.setVisible(true);
            homeRoot.setManaged(true);
            usersRoot.setVisible(false);
            usersRoot.setManaged(false);

            departmentsRoot.setVisible(false);
            departmentsRoot.setManaged(false);
            doctorsRoot.setVisible(false);
            doctorsRoot.setManaged(false);

            contentStack.getChildren().addAll(homeRoot, usersRoot, departmentsRoot, doctorsRoot);

            homePane = homeRoot;
            usersPane = usersRoot;

            departmentsPane = departmentsRoot;
            doctorsPane = doctorsRoot;

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
    protected void openUserLogin(ActionEvent event) throws IOException {
        Parent loginRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("user/hello-view.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(loginRoot);
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
    public void showDepartments(ActionEvent event) {
        Objects.requireNonNull(event);
        showPane(departmentsPane);
    }

    @FXML
    public void showDoctors(ActionEvent event) {
        Objects.requireNonNull(event);
        showPane(doctorsPane);
        try {
            if (doctorsControllerRef != null) doctorsControllerRef.loadDepartments();
        } catch (Exception ignored) {}
    }

    @FXML
    public void showAppointments(ActionEvent event) {
        showPane(appointmentsPane);
    }

    @FXML
    public void showQueues(ActionEvent event) {
        showPane(queuesPane);
        if (queuesControllerRef != null) {
            queuesControllerRef.refreshQueues();
        }
    }

    public void notifyDepartmentsChanged() {
        try {
            if (doctorsControllerRef != null) doctorsControllerRef.loadDepartments();
        } catch (Exception ignored) {}
    }

    private void showPane(Node nodeToShow) {
        if (contentStack == null || nodeToShow == null) return;
        for (Node child : contentStack.getChildren()) {
            boolean show = child == nodeToShow;
            child.setVisible(show);
            child.setManaged(show);
        }

        try {
            btnDashboard.getStyleClass().remove("active");
            btnUsers.getStyleClass().remove("active");

            if (btnDepartments != null) btnDepartments.getStyleClass().remove("active");
            if (btnDoctors != null) btnDoctors.getStyleClass().remove("active");
            if (btnAppointments != null) btnAppointments.getStyleClass().remove("active");
            if (btnQueues != null) btnQueues.getStyleClass().remove("active");
        } catch (Exception ignored) {}

        if (nodeToShow == homePane) addActive(btnDashboard);
        else if (nodeToShow == usersPane) addActive(btnUsers);

        else if (nodeToShow == departmentsPane) addActive(btnDepartments);
        else if (nodeToShow == doctorsPane) addActive(btnDoctors);
        else if (nodeToShow == appointmentsPane) addActive(btnAppointments);
        else if (nodeToShow == queuesPane) addActive(btnQueues);
    }

    private void addActive(javafx.scene.control.Button btn) {
        if (btn == null) return;
        if (!btn.getStyleClass().contains("active")) btn.getStyleClass().add("active");
    }
}
