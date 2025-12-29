package com.example.qsync_2207097_desktop;

import com.example.qsync_2207097_desktop.config.DatabaseConfig;
import com.example.qsync_2207097_desktop.service.SessionManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HelloApplication extends Application {

    private static final Logger LOGGER = Logger.getLogger(HelloApplication.class.getName());

    @Override
    public void start(Stage stage) throws IOException {
        try {
            new DatabaseConfig().initializeDatabase();
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize database: " + e.getMessage(), e);
        }
        String initialFxml = "hello-view.fxml";
        try {
            if (SessionManager.hasSession()) {
                String role = SessionManager.getRole();
                if ("admin".equals(role)) {
                    initialFxml = "admin-home.fxml";
                } else if ("user".equals(role)) {
                    initialFxml = "user-home.fxml";
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Session check failed: " + e.getMessage(), e);
        }

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(initialFxml));
        Scene scene = new Scene(fxmlLoader.load(), 600, 550);
        stage.minHeightProperty().bind(scene.heightProperty());
        stage.minWidthProperty().bind(scene.widthProperty());
        stage.setTitle("QSync - Login");
        stage.setScene(scene);
        stage.show();
    }
}
