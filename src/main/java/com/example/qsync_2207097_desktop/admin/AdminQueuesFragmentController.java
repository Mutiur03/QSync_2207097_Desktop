package com.example.qsync_2207097_desktop.admin;

import com.example.qsync_2207097_desktop.AdminController;
import com.example.qsync_2207097_desktop.config.DatabaseConfig;
import com.example.qsync_2207097_desktop.model.Appointment;
import com.example.qsync_2207097_desktop.model.Department;
import com.example.qsync_2207097_desktop.model.Doctor;
import com.example.qsync_2207097_desktop.service.AppointmentService;
import com.example.qsync_2207097_desktop.service.DepartmentService;
import com.example.qsync_2207097_desktop.service.DoctorService;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.time.LocalDate;
import java.util.List;

public class AdminQueuesFragmentController {

    @FXML
    private TreeView<Object> queuesTreeView;
    @FXML
    private HBox undoContainer;

    private Long lastCancelledId = null;
    private String lastStatus = null;

    private DepartmentService departmentService;
    private DoctorService doctorService;
    private AppointmentService appointmentService;

    public void setParent(AdminController parent) {
        // Not used for now, but following the pattern
    }

    @FXML
    public void initialize() {
        DatabaseConfig dbConfig = new DatabaseConfig();
        departmentService = new DepartmentService(dbConfig);
        doctorService = new DoctorService(dbConfig);
        appointmentService = new AppointmentService(dbConfig);

        queuesTreeView.setCellFactory(param -> new TreeCell<Object>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (item instanceof Department) {
                        Department d = (Department) item;
                        Label label = new Label(d.getName());
                        label.getStyleClass().add("department-label");
                        label.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-padding: 5 0 5 0;");
                        setGraphic(label);
                        setText(null);
                    } else if (item instanceof Doctor) {
                        Doctor d = (Doctor) item;
                        Label label = new Label("Dr. " + d.getName());
                        label.getStyleClass().add("doctor-label");
                        label.setStyle("-fx-font-weight: 600; -fx-font-size: 14px; -fx-padding: 3 0 3 0;");
                        setGraphic(label);
                        setText(null);
                    } else if (item instanceof Appointment) {
                        Appointment a = (Appointment) item;
                        HBox hbox = new HBox(12);
                        hbox.setAlignment(Pos.CENTER_LEFT);
                        hbox.setStyle("-fx-padding: 5 10 5 10; -fx-background-color: #f8fafc; -fx-background-radius: 6;");
                        
                        Label token = new Label("#" + a.getToken());
                        token.setStyle("-fx-font-weight: 800; -fx-text-fill: -fx-theme-primary; -fx-min-width: 40;");
                        
                        Label name = new Label(a.getPatientName());
                        name.setStyle("-fx-font-weight: 500; -fx-min-width: 180; -fx-text-fill: #1e293b;");
                        
                        Label time = new Label(a.getStartTime());
                        time.setStyle("-fx-text-fill: #64748b; -fx-min-width: 70;");

                        Label priority = new Label(a.getPriority());
                        priority.getStyleClass().add("badge");
                        if ("emergency".equalsIgnoreCase(a.getPriority())) {
                            priority.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #ef4444; -fx-padding: 2 8 2 8; -fx-background-radius: 4; -fx-font-size: 11px;");
                        } else {
                            priority.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #64748b; -fx-padding: 2 8 2 8; -fx-background-radius: 4; -fx-font-size: 11px;");
                        }

                        Region spacer = new Region();
                        HBox.setHgrow(spacer, Priority.ALWAYS);

                        ComboBox<String> statusCombo = new ComboBox<>();
                        statusCombo.getItems().addAll("waiting", "in_progress", "completed", "cancelled");
                        statusCombo.setValue(a.getStatus());
                        statusCombo.setStyle("-fx-font-size: 12px; -fx-pref-width: 120; -fx-background-radius: 4;");
                        
                        statusCombo.setOnAction(e -> {
                            String newStatus = statusCombo.getValue();
                            if (newStatus != null && !newStatus.equals(a.getStatus())) {
                                if ("cancelled".equals(newStatus)) {
                                    lastCancelledId = a.getId();
                                    lastStatus = a.getStatus();
                                    undoContainer.setVisible(true);
                                    new Thread(() -> {
                                        try {
                                            Thread.sleep(5000);
                                            javafx.application.Platform.runLater(() -> undoContainer.setVisible(false));
                                        } catch (InterruptedException ignored) {}
                                    }).start();
                                } else {
                                    undoContainer.setVisible(false);
                                }
                                
                                appointmentService.changeStatus(a.getId(), newStatus);
                                a.setStatus(newStatus);
                                if ("cancelled".equals(newStatus)) {
                                    refreshQueues(); 
                                }
                            }
                        });

                        hbox.getChildren().addAll(token, name, time, priority, spacer, statusCombo);
                        setGraphic(hbox);
                        setText(null);
                    }
                }
            }
        });

        refreshQueues();
    }

    @FXML
    public void refreshQueues() {
        TreeItem<Object> root = new TreeItem<>("Root");
        String today = LocalDate.now().toString();

        List<Department> departments = departmentService.getAllDepartments();
        for (Department dept : departments) {
            TreeItem<Object> deptItem = new TreeItem<>(dept);
            deptItem.setExpanded(true);
            
            List<Doctor> doctors = doctorService.listByDepartment(dept.getId());
            for (Doctor doc : doctors) {
                TreeItem<Object> docItem = new TreeItem<>(doc);
                docItem.setExpanded(true);
                
                List<Appointment> appointments = appointmentService.listByDoctorAndDate(doc.getId(), today);
                for (Appointment appt : appointments) {
                    docItem.getChildren().add(new TreeItem<>(appt));
                }
                
                if (!docItem.getChildren().isEmpty()) {
                     deptItem.getChildren().add(docItem);
                }
            }
            
            if (!deptItem.getChildren().isEmpty()) {
                root.getChildren().add(deptItem);
            }
        }

        queuesTreeView.setRoot(root);
    }

    @FXML
    public void undoCancel() {
        if (lastCancelledId != null && lastStatus != null) {
            appointmentService.changeStatus(lastCancelledId, lastStatus);
            undoContainer.setVisible(false);
            lastCancelledId = null;
            lastStatus = null;
            refreshQueues();
        }
    }

    @FXML
    public void closeUndo() {
        undoContainer.setVisible(false);
    }
}
