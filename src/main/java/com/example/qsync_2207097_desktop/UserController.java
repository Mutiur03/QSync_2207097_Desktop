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
    private Button btnBooking;

    @FXML
    private StackPane contentStack;

    private Node homePane;
    private UserHomeFragmentController homeController;

    private Node filesPane;
    private UserFilesFragmentController filesController;

    private Node settingsPane;

    private Node bookingPane;

    @FXML
    public void initialize() {
        String email = SessionManager.getEmail();
        String welcomeText = "Welcome back";
        String emailText = (email != null ? email : "(unknown)");
         try {
            FXMLLoader homeLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("user/user-home-fragment.fxml")));
            javafx.scene.Parent homeRoot = homeLoader.load();
            homeController = homeLoader.getController();
            FXMLLoader filesLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("user/user-files-fragment.fxml")));
            javafx.scene.Parent filesRoot = filesLoader.load();
            filesController = filesLoader.getController();
            FXMLLoader settingsLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("user/user-settings-fragment.fxml")));
            javafx.scene.Parent settingsRoot = settingsLoader.load();
            UserSettingsFragmentController settingsController = settingsLoader.getController();
            FXMLLoader bookingLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("user/user-booking-fragment.fxml")));
            javafx.scene.Parent bookingRoot = bookingLoader.load();
            bookingRoot.setVisible(false);
            bookingRoot.setManaged(false);
            if (homeController != null) homeController.setParent(this);
            if (filesController != null) filesController.setParent(this);
            if (settingsController != null) settingsController.setParent(this);

            if (homeController != null) {
                homeController.setEmail(emailText);
                homeController.setWelcome(welcomeText);
            }
            contentStack.getChildren().addAll(homeRoot, filesRoot, settingsRoot, bookingRoot);
            homePane = homeRoot;
            filesPane = filesRoot;
            settingsPane = settingsRoot;
            bookingPane = bookingRoot;

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
        if (homeController != null) homeController.reload();
        showPane(homePane);
        setActiveButton(btnHome);
    }

    @FXML
    public void showFiles(ActionEvent event) {
        Objects.requireNonNull(event);
        if (filesController != null) filesController.reload();
        showPane(filesPane);
        setActiveButton(btnFiles);
    }

    @FXML
    public void showSettings(ActionEvent event) {
        Objects.requireNonNull(event);
        showPane(settingsPane);
        setActiveButton(btnSettings);
    }

    @FXML
    public void showBooking(ActionEvent event) {
        Objects.requireNonNull(event);
        showPane(bookingPane);
        setActiveButton(btnBooking);
    }

    private void showPane(Node paneToShow) {
        if (contentStack == null) return;
        for (Node child : contentStack.getChildren()) {
            boolean show = (child == paneToShow);
            child.setVisible(show);
            child.setManaged(show);
        }
    }

    private void setActiveButton(Button activeBtn) {
        btnHome.getStyleClass().remove("nav-button-active");
        btnFiles.getStyleClass().remove("nav-button-active");
        btnBooking.getStyleClass().remove("nav-button-active");
        btnSettings.getStyleClass().remove("nav-button-active");
        if (activeBtn != null) activeBtn.getStyleClass().add("nav-button-active");
    }
}
