package com.example.qsync_2207097_desktop.user;

import com.example.qsync_2207097_desktop.UserController;
import com.example.qsync_2207097_desktop.model.Appointment;
import com.example.qsync_2207097_desktop.model.Doctor;
import com.example.qsync_2207097_desktop.service.AppointmentService;
import com.example.qsync_2207097_desktop.service.DoctorService;
import com.example.qsync_2207097_desktop.service.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class UserHomeFragmentController {

    @FXML
    private Label welcomeLabelFragment;

    @FXML
    private Label emailLabelFragment;

    @FXML
    private TableView<Appointment> appointmentsTable;

    @FXML
    private TableColumn<Appointment, String> colDate;

    @FXML
    private TableColumn<Appointment, String> colDoctor;

    @FXML
    private TableColumn<Appointment, Integer> colToken;

    @FXML
    private TableColumn<Appointment, String> colStatus;

    @FXML
    private TableColumn<Appointment, String> colNotes;

    @FXML
    private TableColumn<Appointment, Void> colActions;

    @FXML
    private Button btnRefresh;

    @FXML
    private Button btnBook;

    private UserController parent;
    private AppointmentService appointmentService;
    private DoctorService doctorService;

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
    public void initialize() {
        try {
            appointmentService = new AppointmentService(new com.example.qsync_2207097_desktop.config.DatabaseConfig());
            doctorService = new DoctorService(new com.example.qsync_2207097_desktop.config.DatabaseConfig());

            colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
            colToken.setCellValueFactory(new PropertyValueFactory<>("token"));
            colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
            colNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));

            colDoctor.setCellValueFactory(cellData -> {
                Appointment a = cellData.getValue();
                String did = a.getDoctorId();
                String name = "";
                if (did != null) {
                    try {
                        Doctor d = doctorService.getById(did);
                        if (d != null) name = d.getName();
                    } catch (Exception ignored) {}
                }
                return new SimpleStringProperty(name);
            });

            colToken.setCellFactory(tc -> new TableCell<>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(String.valueOf(item));
                        setStyle("-fx-alignment: CENTER;");
                    }
                }
            });

            colStatus.setCellFactory(tc -> new TableCell<>() {
                @Override
                protected void updateItem(String status, boolean empty) {
                    super.updateItem(status, empty);
                    if (empty || status == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        String s = status.replace('_', ' ');
                        setText(s.substring(0,1).toUpperCase() + s.substring(1));
                        if (status.equals("in_progress")) setStyle("-fx-background-color: lightgreen; -fx-alignment: CENTER; -fx-padding: 4px;");
                        else if (status.equals("waiting")) setStyle("-fx-background-color: lightgoldenrodyellow; -fx-alignment: CENTER; -fx-padding: 4px;");
                        else if (status.equals("cancelled")) setStyle("-fx-background-color: lightgray; -fx-alignment: CENTER; -fx-padding: 4px;");
                        else setStyle("");
                    }
                }
            });

            Callback<TableColumn<Appointment, Void>, TableCell<Appointment, Void>> cellFactory = new Callback<>() {
                @Override
                public TableCell<Appointment, Void> call(final TableColumn<Appointment, Void> param) {
                    final TableCell<Appointment, Void> cell = new TableCell<>() {

                        private final Button btn = new Button("Cancel");

                        {
                            btn.setOnAction((ActionEvent event) -> {
                                Appointment ap = getTableView().getItems().get(getIndex());
                                if (ap != null) {
                                    try {
                                        appointmentService.changeStatus(ap.getId(), "cancelled");
                                        reload();
                                    } catch (Exception ex) {
                                        Alert a = new Alert(Alert.AlertType.ERROR, "Failed to cancel: " + ex.getMessage());
                                        a.showAndWait();
                                    }
                                }
                            });
                        }

                        @Override
                        public void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                setGraphic(btn);
                            }
                        }
                    };
                    return cell;
                }
            };
            colActions.setCellFactory(cellFactory);

            appointmentsTable.setPlaceholder(new Label("No upcoming appointments"));

            btnRefresh.setOnAction(e -> reload());
            btnBook.setOnAction(e -> {
                if (parent != null) {
                    try {
                        parent.showBooking(new ActionEvent());
                    } catch (Exception ignored) {}
                }
            });
            appointmentsTable.setRowFactory(tv -> {
                TableRow<Appointment> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (! row.isEmpty() && event.getButton()==MouseButton.PRIMARY && event.getClickCount() == 2) {
                        Appointment clicked = row.getItem();
                        StringBuilder details = new StringBuilder();
                        details.append("Date: ").append(clicked.getDate()).append("\n");
                        details.append("Doctor: ").append(clicked.getDoctorId()).append("\n");
                        details.append("Token: ").append(clicked.getToken()).append("\n");
                        details.append("Status: ").append(clicked.getStatus()).append("\n\n");
                        details.append("Notes: \n").append(clicked.getNotes());
                        Alert info = new Alert(Alert.AlertType.INFORMATION);
                        info.setHeaderText("Appointment details");
                        info.setContentText(details.toString());
                        info.showAndWait();
                    }
                });
                return row ;
            });

            loadAppointmentsForCurrentUser();
        } catch (Exception ex) {
            System.err.println("Failed to init user home fragment: " + ex.getMessage());
        }
    }

    private void reload() {
        loadAppointmentsForCurrentUser();
    }

    private void loadAppointmentsForCurrentUser() {
        try {
            String email = SessionManager.getEmail();
            if (email == null) return;
            com.example.qsync_2207097_desktop.service.UserService us = new com.example.qsync_2207097_desktop.service.UserService(new com.example.qsync_2207097_desktop.config.DatabaseConfig());
            com.example.qsync_2207097_desktop.model.User u = us.getByEmail(email);
            if (u == null) return;
            List<Appointment> list = appointmentService.listByPatient(u.getId(), 100, 0);
            List<Appointment> filtered = list.stream().filter(a -> a.getStatus() != null && (a.getStatus().equals("waiting") || a.getStatus().equals("in_progress"))).collect(Collectors.toList());
            ObservableList<Appointment> obs = FXCollections.observableArrayList(filtered);
            appointmentsTable.setItems(obs);
        } catch (Exception ex) {
            System.err.println("Failed to load appointments: " + ex.getMessage());
        }
    }

    @FXML
    protected void onSignOut(ActionEvent event) {
        if (parent != null) {
            try {
                parent.signOut(event);
            } catch (IOException e) {
                System.err.println("Sign out failed: " + e.getMessage());
            }
        } else {
            SessionManager.clearSession();
        }
    }
}
