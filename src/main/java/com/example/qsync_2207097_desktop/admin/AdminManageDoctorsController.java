package com.example.qsync_2207097_desktop.admin;

import com.example.qsync_2207097_desktop.AdminController;
import com.example.qsync_2207097_desktop.config.DatabaseConfig;
import com.example.qsync_2207097_desktop.model.Department;
import com.example.qsync_2207097_desktop.model.Doctor;
import com.example.qsync_2207097_desktop.service.DepartmentService;
import com.example.qsync_2207097_desktop.service.DoctorService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.Optional;

public class AdminManageDoctorsController {
    private AdminController parent;

    public void setParent(AdminController parent) { this.parent = parent; }

    @FXML
    private ComboBox<Department> cmbDepartments;

    @FXML
    private TableView<Doctor> doctorsTable;

    @FXML
    private TableColumn<Doctor, String> colName;

    @FXML
    private TableColumn<Doctor, String> colEmail;

    @FXML
    private TableColumn<Doctor, String> colPhone;

    @FXML
    private TableColumn<Doctor, String> colSpecialty;

    @FXML
    private TableColumn<Doctor, String> colStartTime;

    @FXML
    private TableColumn<Doctor, Integer> colVgTime;

    @FXML
    private TableColumn<Doctor, Integer> colYears;

    @FXML
    private TableColumn<Doctor, Void> colActions;

    @FXML
    private Button btnAdd;

    private DoctorService doctorService;
    private DepartmentService departmentService;

    private ObservableList<Doctor> doctors = FXCollections.observableArrayList();
    private ObservableList<Department> departments = FXCollections.observableArrayList();

    private DoctorService getDoctorService() { if (doctorService == null) doctorService = new DoctorService(new DatabaseConfig()); return doctorService; }
    private DepartmentService getDepartmentService() { if (departmentService == null) departmentService = new DepartmentService(new DatabaseConfig()); return departmentService; }

    @FXML
    public void initialize() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colSpecialty.setCellValueFactory(new PropertyValueFactory<>("specialty"));
        colStartTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        colVgTime.setCellValueFactory(new PropertyValueFactory<>("vgTimeMinutes"));
        colYears.setCellValueFactory(new PropertyValueFactory<>("yearsOfExperience"));

        colActions.setCellFactory(tc -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button delBtn = new Button("Delete");
            private final HBox box = new HBox(8, editBtn, delBtn);

            {
                editBtn.setOnAction(e -> {
                    Doctor d = getTableView().getItems().get(getIndex());
                    onEditClicked(d);
                });
                delBtn.setOnAction(e -> {
                    Doctor d = getTableView().getItems().get(getIndex());
                    onDeleteClicked(d);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(box);
            }
        });

        doctorsTable.setItems(doctors);
        cmbDepartments.setItems(departments);
        cmbDepartments.setCellFactory(cb -> new ListCell<>(){
            @Override
            protected void updateItem(Department item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        cmbDepartments.setButtonCell(new ListCell<>(){
            @Override
            protected void updateItem(Department item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        cmbDepartments.setOnAction(e -> onDepartmentSelected());
        loadDepartments();
    }

    public void loadDepartments() {
        try {
            Department previouslySelected = cmbDepartments.getSelectionModel().getSelectedItem();
            Long prevId = previouslySelected == null ? null : previouslySelected.getId();

            List<Department> list = getDepartmentService().getAllDepartments();
            departments.setAll(list);

            if (prevId != null) {
                for (Department d : list) {
                    if (d.getId() == prevId.longValue()) {
                        cmbDepartments.getSelectionModel().select(d);
                        onDepartmentSelected();
                        return;
                    }
                }
            }

            if (!list.isEmpty()) {
                cmbDepartments.getSelectionModel().selectFirst();
                onDepartmentSelected();
            } else {
                doctors.clear();
            }
        } catch (Exception ex) {
            showAlert("Failed to load departments: " + ex.getMessage());
        }
    }

    private void onDepartmentSelected() {
        Department sel = cmbDepartments.getSelectionModel().getSelectedItem();
        if (sel == null) {
            doctors.clear();
            return;
        }
        try {
            List<Doctor> list = getDoctorService().listByDepartment(sel.getId());
            doctors.setAll(list);
        } catch (Exception ex) {
            showAlert("Failed to load doctors: " + ex.getMessage());
        }
    }

    @FXML
    protected void onAddClicked(ActionEvent event) {
        Optional<Doctor> result = showDoctorDialog(null);
        result.ifPresent(d -> {
            try {
                getDoctorService().create(d);
                onDepartmentSelected();
            } catch (Exception ex) {
                showAlert("Failed to create doctor: " + ex.getMessage());
                System.out.println(ex.getMessage());
            }
        });
    }

    private void onEditClicked(Doctor d) {
        Optional<Doctor> result = showDoctorDialog(d);
        result.ifPresent(updated -> {
            try {
                updated.setId(d.getId());
                getDoctorService().update(updated);
                onDepartmentSelected();
            } catch (Exception ex) {
                showAlert("Failed to update doctor: " + ex.getMessage());
            }
        });
    }

    private void onDeleteClicked(Doctor d) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete doctor '" + d.getName() + "'? ");
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            try {
                getDoctorService().delete(d.getId());
                onDepartmentSelected();
            } catch (Exception ex) {
                showAlert("Failed to delete doctor: " + ex.getMessage());
            }
        }
    }

    private Optional<Doctor> showDoctorDialog(Doctor existing) {
        try {
            URL fxmlUrl = getClass().getResource("/com/example/qsync_2207097_desktop/admin/doctor-dialog.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(fxmlLoader.load());
            DoctorDialogController dialogController = fxmlLoader.getController();
            Stage dialog = new Stage();
            dialog.setTitle(existing == null ? "Add Doctor" : "Edit Doctor");
            dialog.setScene(scene);
            dialogController.setDialogStage(dialog);
            dialogController.setDoctor(existing);
            Department selDep = cmbDepartments.getSelectionModel().getSelectedItem();
            if (selDep != null) dialogController.setSelectedDepartment(selDep);
            dialog.showAndWait();
            Doctor res = dialogController.getResult();
            if (res == null) return Optional.empty();
            return Optional.of(res);
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Failed to load doctor dialog: " + ex.getMessage());
            return Optional.empty();
        }
    }

    private void showAlert(String message) {
        Alert a = new Alert(Alert.AlertType.ERROR, message);
        a.showAndWait();
    }
}
