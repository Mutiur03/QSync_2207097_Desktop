package com.example.qsync_2207097_desktop.admin;

import com.example.qsync_2207097_desktop.AdminController;
import com.example.qsync_2207097_desktop.config.DatabaseConfig;
import com.example.qsync_2207097_desktop.model.Department;
import com.example.qsync_2207097_desktop.service.DepartmentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.Optional;

public class AdminDepartmentsFragmentController {
    private AdminController parent;

    public void setParent(AdminController parent) { this.parent = parent; }

    @FXML
    private TableView<Department> departmentsTable;

    @FXML
    private TableColumn<Department, String> colName;

    @FXML
    private TableColumn<Department, String> colDescription;

    @FXML
    private TableColumn<Department, Void> colActions;

    @FXML
    private Button btnAdd;

    private DepartmentService departmentService;
    private ObservableList<Department> departments = FXCollections.observableArrayList();

    private DepartmentService getDepartmentService() {
        if (departmentService == null) departmentService = new DepartmentService(new DatabaseConfig());
        return departmentService;
    }

    @FXML
    public void initialize() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        colActions.setCellFactory(tc -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button delBtn = new Button("Delete");
            private final HBox box = new HBox(8, editBtn, delBtn);

            {
                editBtn.getStyleClass().add("btn-secondary");
                delBtn.getStyleClass().add("btn-danger");
                editBtn.setOnAction(e -> {
                    Department d = getTableView().getItems().get(getIndex());
                    onEditClicked(d);
                });
                delBtn.setOnAction(e -> {
                    Department d = getTableView().getItems().get(getIndex());
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
        departmentsTable.setItems(departments);
        loadDepartments();
    }

    private void loadDepartments() {
        try {
            List<Department> list = getDepartmentService().getAllDepartments();
            departments.setAll(list);
        } catch (Exception ex) {
            showAlert("Failed to load departments: " + ex.getMessage());
        }
    }

    @FXML
    protected void onAddClicked(ActionEvent event) {
        Optional<Department> result = showDepartmentDialog(null);
        result.ifPresent(d -> {
            try {
                getDepartmentService().createDepartment(d.getName(), d.getDescription());
                loadDepartments();
                if (parent != null) parent.notifyDepartmentsChanged();
            } catch (Exception ex) {
                showAlert("Failed to create department: " + ex.getMessage());
            }
        });
    }

    private void onEditClicked(Department d) {
        Optional<Department> result = showDepartmentDialog(d);
        result.ifPresent(updated -> {
            try {
                getDepartmentService().updateDepartment(d.getId(), updated.getName(), updated.getDescription());
                loadDepartments();
                if (parent != null) parent.notifyDepartmentsChanged();
            } catch (Exception ex) {
                showAlert("Failed to update department: " + ex.getMessage());
            }
        });
    }

    private void onDeleteClicked(Department d) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete department '" + d.getName() + "'? ");
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            try {
                boolean ok = getDepartmentService().deleteDepartment(d.getId());
                if (ok) {
                    loadDepartments();
                    if (parent != null) parent.notifyDepartmentsChanged();
                }
                else showAlert("Delete failed");
            } catch (Exception ex) {
                showAlert("Failed to delete department: " + ex.getMessage());
            }
        }
    }

    private Optional<Department> showDepartmentDialog(Department existing) {
        try {
            URL fxmlUrl = getClass().getResource("/com/example/qsync_2207097_desktop/admin/department-dialog.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(fxmlLoader.load());
            DepartmentDialogController dialogController = fxmlLoader.getController();
            Stage dialog = new Stage();
            dialog.setTitle(existing == null ? "Add Department" : "Edit Department");
            dialog.setScene(scene);

            dialogController.setDialogStage(dialog);
            dialogController.setDepartment(existing);
            dialog.showAndWait();
            Department res = dialogController.getResult();
            if (res == null) return Optional.empty();
            if (existing != null) res.setId(existing.getId());
            return Optional.of(res);
        } catch (Exception ex) {
            showAlert("Failed to load department: " + ex.getMessage());
            System.out.println("DEBUG: FXML load failed, using fallback dialog. Error: " + ex);}
            return Optional.empty();
    }


    private void showAlert(String message) {
        Alert a = new Alert(Alert.AlertType.ERROR, message);
        a.showAndWait();
    }
}
