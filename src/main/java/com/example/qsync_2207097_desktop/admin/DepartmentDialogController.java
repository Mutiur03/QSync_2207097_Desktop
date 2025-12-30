package com.example.qsync_2207097_desktop.admin;

import com.example.qsync_2207097_desktop.model.Department;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class DepartmentDialogController {
    @FXML
    private TextField nameField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnCancel;

    private Stage dialogStage;
    private Department result;

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    public void setDepartment(Department d) {
        if (d != null) {
            nameField.setText(d.getName());
            descriptionField.setText(d.getDescription());
        }
    }

    @FXML
    private void initialize() {
        btnSave.setOnAction(e -> onSave());
        btnCancel.setOnAction(e -> onCancel());
    }

    private void onSave() {
        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        String desc = descriptionField.getText() == null ? "" : descriptionField.getText().trim();
        if (name.isEmpty()) {
            return;
        }
        Department d = new Department();
        d.setName(name);
        d.setDescription(desc);
        this.result = d;
        if (dialogStage != null) dialogStage.close();
    }

    private void onCancel() {
        this.result = null;
        if (dialogStage != null) dialogStage.close();
    }

    public Department getResult() {
        return result;
    }
}

