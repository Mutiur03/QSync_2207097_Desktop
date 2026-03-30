package com.example.qsync_2207097_desktop.user;

import com.example.qsync_2207097_desktop.UserController;
import com.example.qsync_2207097_desktop.config.DatabaseConfig;
import com.example.qsync_2207097_desktop.model.Appointment;
import com.example.qsync_2207097_desktop.model.Doctor;
import com.example.qsync_2207097_desktop.model.User;
import com.example.qsync_2207097_desktop.service.AppointmentService;
import com.example.qsync_2207097_desktop.service.DoctorService;
import com.example.qsync_2207097_desktop.service.SessionManager;
import com.example.qsync_2207097_desktop.service.UserService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.geometry.Pos;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class UserFilesFragmentController {

    @FXML
    private TableView<Appointment> historyTable;
    @FXML
    private TableColumn<Appointment, String> colDate;
    @FXML
    private TableColumn<Appointment, String> colDoctor;
    @FXML
    private TableColumn<Appointment, Integer> colToken;
    @FXML
    private TableColumn<Appointment, String> colStatus;
    @FXML
    private TableColumn<Appointment, String> colPriority;
    @FXML
    private Button btnRefresh;

    private AppointmentService appointmentService;
    private DoctorService doctorService;

    public void setParent(UserController parent) {
    }

    @FXML
    public void initialize() {
        DatabaseConfig cfg = new DatabaseConfig();
        appointmentService = new AppointmentService(cfg);
        doctorService = new DoctorService(cfg);

        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colToken.setCellValueFactory(new PropertyValueFactory<>("token"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));

        colDoctor.setCellValueFactory(cellData -> {
            Appointment a = cellData.getValue();
            String did = a.getDoctorId();
            String name = "N/A";
            if (did != null) {
                try {
                    Doctor d = doctorService.getById(did);
                    if (d != null) name = d.getName();
                } catch (Exception ignored) {}
            }
            return new SimpleStringProperty(name);
        });

        colStatus.setCellFactory(tc -> new TableCell<>() {
            private final Label badge = new Label();
            {
                badge.getStyleClass().add("status-badge");
                setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    String s = status.replace('_', ' ');
                    badge.setText(s.substring(0, 1).toUpperCase() + s.substring(1));
                    badge.getStyleClass().removeAll("status-completed", "status-cancelled", "status-waiting", "status-in_progress");
                    
                    if (status.equals("completed")) badge.getStyleClass().add("status-completed");
                    else if (status.equals("cancelled")) badge.getStyleClass().add("status-cancelled");
                    else if (status.equals("in_progress")) badge.getStyleClass().add("status-in_progress");
                    else if (status.equals("waiting")) badge.getStyleClass().add("status-waiting");
                    
                    setGraphic(badge);
                }
            }
        });

        btnRefresh.setOnAction(e -> reload());
        loadHistory();
    }

    public void reload() {
        loadHistory();
    }

    private void loadHistory() {
        try {
            String email = SessionManager.getEmail();
            if (email == null) return;
            UserService us = new UserService(new DatabaseConfig());
            User u = us.getByEmail(email);
            if (u == null) return;
            
            List<Appointment> list = appointmentService.listByPatient(u.getId(), 500, 0);
            List<Appointment> history = list.stream()
                    .sorted(Comparator.comparing(Appointment::getDate).reversed()
                            .thenComparing(Comparator.comparing(Appointment::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())).reversed()))
                    .collect(Collectors.toList());

            ObservableList<Appointment> obs = FXCollections.observableArrayList(history);
            historyTable.setItems(obs);
        } catch (Exception ex) {
            System.err.println("Failed to load appointment history: " + ex.getMessage());
        }
    }
}
