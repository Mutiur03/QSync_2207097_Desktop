package com.example.qsync_2207097_desktop.admin;

import com.example.qsync_2207097_desktop.model.Appointment;
import com.example.qsync_2207097_desktop.service.AppointmentService;
import com.example.qsync_2207097_desktop.config.DatabaseConfig;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;

public class AdminAppointmentsFragmentController {

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button btnLoad;

    @FXML
    private TableView<Appointment> appointmentsTable;

    @FXML
    private TableColumn<Appointment, Integer> colToken;

    @FXML
    private TableColumn<Appointment, String> colTime;

    @FXML
    private TableColumn<Appointment, String> colPatient;

    @FXML
    private TableColumn<Appointment, String> colPhone;

    @FXML
    private TableColumn<Appointment, String> colDoctor;

    @FXML
    private TableColumn<Appointment, String> colStatus;

    @FXML
    private TableColumn<Appointment, String> colPriority;

    @FXML
    private Pagination pagination;

    private final AppointmentService appointmentService;
    private final ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

    public AdminAppointmentsFragmentController() {
        appointmentService = new AppointmentService(new DatabaseConfig());
    }

    @FXML
    public void initialize() {
        colToken.setCellValueFactory(new PropertyValueFactory<>("token"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        colPatient.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("patientPhone"));
        colDoctor.setCellValueFactory(new PropertyValueFactory<>("doctorId"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));

        appointmentsTable.setItems(appointmentList);
        
        datePicker.setValue(LocalDate.now());
        
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) onLoad();
        });
        onLoad();
    }

    @FXML
    protected void onLoad() {
        LocalDate date = datePicker.getValue();
        if (date == null) return;
        
        try {
            List<Appointment> items = appointmentService.listByDate(date.toString(), 100, 0);
            appointmentList.setAll(items);
        } catch (Exception ex) {
            System.err.println("Failed to load appointments: " + ex.getMessage());
        }
    }
}
