package com.example.qsync_2207097_desktop.user;

import com.example.qsync_2207097_desktop.UserController;
import com.example.qsync_2207097_desktop.service.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class UserHomeFragmentController {

    @FXML
    private Label welcomeLabelFragment;

    @FXML
    private Label emailLabelFragment;

    private UserController parent;

    public void setParent(UserController parent) {
        this.parent = parent;
    }

    public void setEmail(String email) {
        if (emailLabelFragment != null) emailLabelFragment.setText(email);
    }

    public void setWelcome(String text) {
        if (welcomeLabelFragment != null) welcomeLabelFragment.setText(text);
    }

    @FXML
    protected void onSignOut(ActionEvent event) {
        if (parent != null) {
            try {
                parent.signOut(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            SessionManager.clearSession();
        }
    }
}

