package com.example.qsync_2207097_desktop.admin;

import com.example.qsync_2207097_desktop.model.Appointment;
import com.example.qsync_2207097_desktop.service.AppointmentService;
import com.example.qsync_2207097_desktop.config.DatabaseConfig;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.Pagination;

import java.time.LocalDate;
import java.util.List;

public class AdminAppointmentsFragmentController {

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button btnLoad;

    @FXML
    private ListView<Appointment> listView;

    @FXML
    private Pagination pagination;

    private final AppointmentService appointmentService;

    public AdminAppointmentsFragmentController() {
        appointmentService = new AppointmentService(new DatabaseConfig());
    }

    @FXML
    public void initialize() {
        datePicker.setValue(LocalDate.now());
    }

    @FXML
    protected void onLoad() {
        LocalDate date = datePicker.getValue();
        if (date == null) return;
        List<Appointment> items = appointmentService.listByDate(date.toString(), 50, 0);
        listView.getItems().setAll(items);
    }
}
