package com.example.qsync_2207097_desktop.admin;

import com.example.qsync_2207097_desktop.AdminController;
import com.example.qsync_2207097_desktop.config.DatabaseConfig;
import com.example.qsync_2207097_desktop.service.AppointmentService;
import com.example.qsync_2207097_desktop.service.DepartmentService;
import com.example.qsync_2207097_desktop.service.DoctorService;
import com.example.qsync_2207097_desktop.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.LocalDate;

public class AdminHomeFragmentController {

    @FXML
    private Label lblTotalUsers;

    @FXML
    private Label lblDepartments;

    @FXML
    private Label lblActiveDoctors;

    @FXML
    private Label lblTodayAppointments;

    private UserService userService;
    private DepartmentService departmentService;
    private DoctorService doctorService;
    private AppointmentService appointmentService;

    private AdminController parent;

    public void setParent(AdminController parent) {
        this.parent = parent;
        initializeData();
    }

    @FXML
    public void initialize() {
        DatabaseConfig config = new DatabaseConfig();
        userService = new UserService(config);
        departmentService = new DepartmentService(config);
        doctorService = new DoctorService(config);
        appointmentService = new AppointmentService(config);
    }

    private void initializeData() {
        try {
            int userCount = userService.getAllUsers().size();
            lblTotalUsers.setText(String.format("%,d", userCount));

            int deptCount = departmentService.getAllDepartments().size();
            lblDepartments.setText(String.valueOf(deptCount));

            int doctorCount = doctorService.getAllDoctors().size();
            lblActiveDoctors.setText(String.valueOf(doctorCount));

            int appointmentCount = appointmentService.getCountByDate(LocalDate.now().toString());
            lblTodayAppointments.setText(String.valueOf(appointmentCount));
        } catch (Exception ex) {
            System.err.println("Failed to load dashboard stats: " + ex.getMessage());
        }
    }

    @FXML
    protected void onAddDoctor() {
        if (parent != null) parent.showDoctors(null);
    }

    @FXML
    protected void onGenerateReport() {
        if (parent != null) parent.showReports(null);
    }

    @FXML
    protected void onShowSettings() {
        if (parent != null) parent.showSettings(null);
    }
}

