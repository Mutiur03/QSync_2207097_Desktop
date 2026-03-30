package com.example.qsync_2207097_desktop.user;

import com.example.qsync_2207097_desktop.UserController;
import com.example.qsync_2207097_desktop.model.Appointment;
import com.example.qsync_2207097_desktop.model.Doctor;
import com.example.qsync_2207097_desktop.service.AppointmentService;
import com.example.qsync_2207097_desktop.service.DoctorService;
import com.example.qsync_2207097_desktop.service.SessionManager;
import com.example.qsync_2207097_desktop.service.UserService;
import com.example.qsync_2207097_desktop.model.User;
import com.example.qsync_2207097_desktop.config.DatabaseConfig;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;
import javafx.geometry.Pos;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

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
    private TableColumn<Appointment, String> colPriority;

    @FXML
    private TableColumn<Appointment, Void> colActions;

    @FXML
    private Button btnRefresh;

    @FXML
    private Button btnBook;

    @FXML
    private FlowPane todayCardsContainer;

    @FXML
    private HBox undoContainer;

    private Long lastCancelledId;
    private String lastCancelledStatus;
    private PauseTransition undoTimer;

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
            appointmentService = new AppointmentService(new DatabaseConfig());
            doctorService = new DoctorService(new DatabaseConfig());

            colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
            colToken.setCellValueFactory(new PropertyValueFactory<>("token"));
            colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
            colPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));

            centerAlignColumn(colDate);
            centerAlignColumn(colDoctor);
            centerAlignColumn(colToken);
            centerAlignColumn(colStatus);
            centerAlignColumn(colPriority);
            centerAlignColumn(colActions);

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
                        getStyleClass().removeAll("status-badge", "status-completed", "status-cancelled", "status-waiting", "status-in_progress");
                    } else {
                        String s = status.replace('_', ' ');
                        setText(s.substring(0,1).toUpperCase() + s.substring(1));
                        getStyleClass().add("status-badge");
                        getStyleClass().removeAll("status-completed", "status-cancelled", "status-waiting", "status-in_progress");
                        if (status.equals("in_progress")) getStyleClass().add("status-in_progress");
                        else if (status.equals("waiting")) getStyleClass().add("status-waiting");
                        else if (status.equals("cancelled")) getStyleClass().add("status-cancelled");
                        else if (status.equals("completed")) getStyleClass().add("status-completed");
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
                                        String oldStatus = ap.getStatus();
                                        appointmentService.changeStatus(ap.getId(), "cancelled");
                                        reload();
                                        showUndo(ap.getId(), oldStatus);
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

    public void reload() {
        loadAppointmentsForCurrentUser();
    }

    private void loadAppointmentsForCurrentUser() {
        try {
            String email = SessionManager.getEmail();
            if (email == null) return;
            UserService us = new UserService(new DatabaseConfig());
            User u = us.getByEmail(email);
            if (u == null) return;
            List<Appointment> list = appointmentService.listByPatient(u.getId(), 100, 0);
            
            String today = LocalDate.now().toString(); 
            
            List<Appointment> filtered = list.stream()
                .filter(a -> a.getStatus() != null && (a.getStatus().equals("waiting") || a.getStatus().equals("in_progress")))
                .collect(Collectors.toList());

            List<Appointment> todayList = filtered.stream()
                .filter(a -> a.getDate().equals(today))
                .sorted(Comparator.comparing(Appointment::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

            List<Appointment> upcomingList = filtered.stream()
                .filter(a -> a.getDate().compareTo(today) > 0)
                .sorted(Comparator.comparing(Appointment::getDate).thenComparing(Appointment::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

            populateTodayCards(todayList);
            
            ObservableList<Appointment> obs = FXCollections.observableArrayList(upcomingList);
            appointmentsTable.setItems(obs);
        } catch (Exception ex) {
            System.err.println("Failed to load appointments: " + ex.getMessage());
        }
    }

    private void populateTodayCards(List<Appointment> todayAppointments) {
        if (todayCardsContainer == null) return;
        todayCardsContainer.getChildren().clear();
        
        if (todayAppointments.isEmpty()) {
            Label noData = new Label("No appointments for today");
            noData.getStyleClass().add("muted");
            todayCardsContainer.getChildren().add(noData);
            return;
        }

        for (Appointment a : todayAppointments) {
            VBox card = new VBox(8);
            card.getStyleClass().add("appointment-card");
            
            String docName = "Doctor";
            try {
                Doctor d = doctorService.getById(a.getDoctorId());
                if (d != null) docName = d.getName();
            } catch (Exception ignored) {}

            Label nameLabel = new Label(docName);
            nameLabel.getStyleClass().add("card-doctor");
            
            Label timeLabel = new Label("Scheduled at: " + a.getStartTime());
            timeLabel.getStyleClass().add("card-scheduled-time");
            
            HBox bottomBox = new HBox(8);
            bottomBox.setAlignment(Pos.CENTER_LEFT);
            Label tokenLabel = new Label("Token: " + (a.getToken() != null ? a.getToken() : "N/A"));
            tokenLabel.getStyleClass().add("card-token");
            bottomBox.getChildren().add(tokenLabel);
            
            Label statusBadge = new Label(a.getStatus());
            statusBadge.getStyleClass().addAll("status-badge", "status-" + a.getStatus());
            bottomBox.getChildren().add(statusBadge);

            Button cancelBtn = new Button("Cancel");
            cancelBtn.getStyleClass().add("btn-danger");
            cancelBtn.setMaxWidth(Double.MAX_VALUE);
            cancelBtn.setOnAction(e -> {
                try {
                    String oldStatus = a.getStatus();
                    appointmentService.changeStatus(a.getId(), "cancelled");
                    reload();
                    showUndo(a.getId(), oldStatus);
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, "Failed to cancel: " + ex.getMessage()).showAndWait();
                }
            });

            card.getChildren().addAll(nameLabel, timeLabel, bottomBox);
            
            if (a.getPriority() != null && !a.getPriority().isEmpty()) {
                Label priorityLabel = new Label(a.getPriority());
                priorityLabel.getStyleClass().addAll("status-badge", "status-waiting");

                priorityLabel.setStyle("-fx-font-size: 10px;");
                card.getChildren().add(priorityLabel);
            }

            card.getChildren().add(cancelBtn);
            todayCardsContainer.getChildren().add(card);
        }
    }

    private void centerAlignColumn(TableColumn<?, ?> column) {
        column.setStyle("-fx-alignment: CENTER;");
    }

    @FXML
    public void onUndo(ActionEvent event) {
        if (lastCancelledId != null && appointmentService != null) {
            try {
                appointmentService.changeStatus(lastCancelledId, lastCancelledStatus != null ? lastCancelledStatus : "waiting");
                reload();
                closeUndo(null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    public void closeUndo(ActionEvent event) {
        if (undoContainer != null) {
            undoContainer.setVisible(false);
            undoContainer.setManaged(false);
        }
    }

    private void showUndo(Long aid, String oldStatus) {
        this.lastCancelledId = aid;
        this.lastCancelledStatus = oldStatus;
        if (undoContainer != null) {
            undoContainer.setVisible(true);
            undoContainer.setManaged(true);
            if (undoTimer != null) undoTimer.stop();
            undoTimer = new PauseTransition(Duration.seconds(10));
            undoTimer.setOnFinished(e -> closeUndo(null));
            undoTimer.play();
        }
    }

}
