package com.example.qsync_2207097_desktop;

import com.example.qsync_2207097_desktop.service.SessionManager;
import com.example.qsync_2207097_desktop.user.UserFilesFragmentController;
import com.example.qsync_2207097_desktop.user.UserHomeFragmentController;
import com.example.qsync_2207097_desktop.user.UserSettingsFragmentController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserController {

    private static final Logger LOGGER = Logger.getLogger(UserController.class.getName());

    @FXML
    private Button btnHome;

    @FXML
    private Button btnFiles;

    @FXML
    private Button btnSettings;

    @FXML
    private StackPane contentStack;

    private Node homePane;

    private Node filesPane;

    private Node settingsPane;

    @FXML
    public void initialize() {
        String email = SessionManager.getEmail();
        String welcomeText = "Welcome back";
        String emailText = (email != null ? email : "(unknown)");
         try {
            FXMLLoader homeLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("user/user-home-fragment.fxml")));
            javafx.scene.Parent homeRoot = homeLoader.load();
            UserHomeFragmentController homeController = homeLoader.getController();
            FXMLLoader filesLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("user/user-files-fragment.fxml")));
            javafx.scene.Parent filesRoot = filesLoader.load();
            UserFilesFragmentController filesController = filesLoader.getController();
            FXMLLoader settingsLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("user/user-settings-fragment.fxml")));
            javafx.scene.Parent settingsRoot = settingsLoader.load();
            UserSettingsFragmentController settingsController = settingsLoader.getController();
            if (homeController != null) homeController.setParent(this);
            if (filesController != null) filesController.setParent(this);
            if (settingsController != null) settingsController.setParent(this);

            if (homeController != null) {
                homeController.setEmail(emailText);
                homeController.setWelcome(welcomeText);
            }
            contentStack.getChildren().addAll(homeRoot, filesRoot, settingsRoot);
            homePane = homeRoot;
            filesPane = filesRoot;
            settingsPane = settingsRoot;

            showPane(homePane);
         } catch (Exception ex) {
             LOGGER.log(Level.SEVERE, "Failed to load user fragments", ex);
         }
     }

    @FXML
    public void signOut(ActionEvent event) throws IOException {
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
    public void showFiles(ActionEvent event) {
        Objects.requireNonNull(event);
        showPane(filesPane);
    }

    @FXML
    public void showSettings(ActionEvent event) {
        Objects.requireNonNull(event);
        showPane(settingsPane);
    }

    private void showPane(Node paneToShow) {
        if (contentStack == null) return;
        for (Node child : contentStack.getChildren()) {
            boolean show = child == paneToShow;
            child.setVisible(show);
            child.setManaged(show);
        }

        try {
            btnHome.getStyleClass().remove("active");
            btnFiles.getStyleClass().remove("active");
            btnSettings.getStyleClass().remove("active");
        } catch (Exception ignored) {}

        if (paneToShow == homePane) addActive(btnHome);
        else if (paneToShow == filesPane) addActive(btnFiles);
        else if (paneToShow == settingsPane) addActive(btnSettings);
    }

    private void addActive(Button btn) {
        if (btn == null) return;
        if (!btn.getStyleClass().contains("active")) btn.getStyleClass().add("active");
    }
}
