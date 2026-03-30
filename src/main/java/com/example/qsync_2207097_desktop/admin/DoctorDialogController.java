package com.example.qsync_2207097_desktop.admin;

import com.example.qsync_2207097_desktop.model.Department;
import com.example.qsync_2207097_desktop.model.Doctor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class DoctorDialogController {
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtPhone;
    @FXML
    private TextField txtSpecialty;
    @FXML
    private TextField txtStartTime;
    @FXML
    private Spinner<Integer> spnVgTime;
    @FXML
    private Spinner<Integer> spnYears;
    @FXML
    private Button btnSave;

    private Stage dialogStage;
    private Doctor result;
    private Department selectedDepartment;

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    public void setSelectedDepartment(Department d) {
        this.selectedDepartment = d;
    }

    public void setDoctor(Doctor d) {
        if (d != null) {
            txtName.setText(d.getName());
            txtEmail.setText(d.getEmail());
            txtPhone.setText(d.getPhone());
            txtSpecialty.setText(d.getSpecialty());
            txtStartTime.setText(d.getStartTime());
            spnVgTime.getValueFactory().setValue(d.getVgTimeMinutes());
            spnYears.getValueFactory().setValue(d.getYearsOfExperience());
        } else {
            txtName.setText("");
            txtEmail.setText("");
            txtPhone.setText("");
            txtSpecialty.setText("");
            txtStartTime.setText("");
            if (spnVgTime.getValueFactory() != null) spnVgTime.getValueFactory().setValue(0);
            if (spnYears.getValueFactory() != null) spnYears.getValueFactory().setValue(0);
        }
    }

    @FXML
    private void initialize() {
        spnVgTime.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 0));
        spnYears.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 200, 0));
        btnSave.setOnAction(e -> onSave());
    }

    private void onSave() {
        Doctor d = new Doctor();
        d.setName(txtName.getText() == null ? "" : txtName.getText().trim());
        d.setEmail(txtEmail.getText() == null ? "" : txtEmail.getText().trim());
        d.setPhone(txtPhone.getText() == null ? "" : txtPhone.getText().trim());
        d.setSpecialty(txtSpecialty.getText() == null ? "" : txtSpecialty.getText().trim());
        d.setStartTime(txtStartTime.getText() == null ? "" : txtStartTime.getText().trim());
        d.setVgTimeMinutes(spnVgTime.getValue() == null ? 0 : spnVgTime.getValue());
        d.setYearsOfExperience(spnYears.getValue() == null ? 0 : spnYears.getValue());
        if (selectedDepartment != null) d.setDepartmentId(selectedDepartment.getId());
        this.result = d;
        if (dialogStage != null) dialogStage.close();
    }

    @FXML
    private void onSaveClicked(ActionEvent event) {
        onSave();
    }

    @FXML
    private void onCancelClicked() {
        this.result = null;
        if (dialogStage != null) dialogStage.close();
    }

    public Doctor getResult() {
        return result;
    }
}
