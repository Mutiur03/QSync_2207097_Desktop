package com.example.qsync_2207097_desktop.user;

import com.example.qsync_2207097_desktop.config.DatabaseConfig;
import com.example.qsync_2207097_desktop.model.Appointment;
import com.example.qsync_2207097_desktop.model.Department;
import com.example.qsync_2207097_desktop.model.Doctor;
import com.example.qsync_2207097_desktop.service.AppointmentService;
import com.example.qsync_2207097_desktop.service.DepartmentService;
import com.example.qsync_2207097_desktop.service.DoctorService;
import com.example.qsync_2207097_desktop.service.SessionManager;
import com.example.qsync_2207097_desktop.service.UserService;
import com.example.qsync_2207097_desktop.model.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class UserBookingFragmentController {

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button btnBook;

    @FXML
    private ComboBox<Department> departmentCombo;

    @FXML
    private ComboBox<Doctor> doctorCombo;

    @FXML
    private ChoiceBox<String> priorityCombo;

    @FXML
    private TextArea symptomsField;

    private final AppointmentService appointmentService;
    private final DepartmentService departmentService;
    private final DoctorService doctorService;
    private UserService userService;

    public UserBookingFragmentController() {
        DatabaseConfig cfg = new DatabaseConfig();
        appointmentService = new AppointmentService(cfg);
        departmentService = new DepartmentService(cfg);
        doctorService = new DoctorService(cfg);
    }

    private UserService getUserService() {
        if (userService == null) userService = new UserService(new DatabaseConfig());
        return userService;
    }

    @FXML
    public void initialize() {
        datePicker.setValue(LocalDate.now());
        try {
            List<Department> deps = departmentService.getAllDepartments();
            departmentCombo.setItems(FXCollections.observableArrayList(deps));
            departmentCombo.setCellFactory(cb -> new ListCell<Department>() {
                @Override
                protected void updateItem(Department item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getName());
                }
            });
            departmentCombo.setButtonCell(new ListCell<Department>() {
                @Override
                protected void updateItem(Department item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getName());
                }
            });

            doctorCombo.setCellFactory(cb -> new ListCell<Doctor>() {
                @Override
                protected void updateItem(Doctor item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getName());
                }
            });
            doctorCombo.setButtonCell(new ListCell<Doctor>() {
                @Override
                protected void updateItem(Doctor item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getName());
                }
            });

            departmentCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
                if (newV == null) {
                    doctorCombo.setItems(FXCollections.observableArrayList());
                } else {
                    List<Doctor> doctors = doctorService.listByDepartment(newV.getId());
                    doctorCombo.setItems(FXCollections.observableArrayList(doctors));
                }
            });

            priorityCombo.setItems(FXCollections.observableArrayList("normal", "urgent", "emergency"));
            priorityCombo.setValue("normal");

        } catch (Exception ex) {
            Alert a = new Alert(Alert.AlertType.ERROR, "Failed to load departments: " + ex.getMessage());
            a.showAndWait();
        }
    }

    @FXML
    protected void onBook() {
        try {
            Department selDept = departmentCombo.getValue();
            Doctor selDoc = doctorCombo.getValue();
            String priority = priorityCombo.getValue();
            String symptoms = symptomsField.getText();
            if (selDept == null) { Alert a = new Alert(Alert.AlertType.ERROR, "Please select a department"); a.showAndWait(); return; }
            if (selDoc == null) { Alert a = new Alert(Alert.AlertType.ERROR, "Please select a doctor"); a.showAndWait(); return; }

            LocalDate ld = datePicker.getValue();
            if (ld == null) { Alert a = new Alert(Alert.AlertType.ERROR, "Please select a date"); a.showAndWait(); return; }

            LocalTime st;
            int durationMinutes = selDoc.getVgTimeMinutes() > 0 ? selDoc.getVgTimeMinutes() : 30;
            DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");
            try {
                if (selDoc.getStartTime() != null && !selDoc.getStartTime().trim().isEmpty()) {
                    st = LocalTime.parse(selDoc.getStartTime(), tf);
                } else {
                    st = LocalTime.of(9, 0);
                }
            } catch (Exception e) {
                st = LocalTime.of(9,0);
            }
            LocalTime et = st.plusMinutes(durationMinutes);
            long startTs = ld.atTime(st).toEpochSecond(ZoneOffset.UTC);
            long endTs = ld.atTime(et).toEpochSecond(ZoneOffset.UTC);

            Long patientId = null;
            String patientName = "Guest";
            String patientPhone = "";
            String email = SessionManager.getEmail();
            if (email != null) {
                try {
                    User current = getUserService().getByEmail(email);
                    if (current != null) {
                        patientId = current.getId();
                        patientName = current.getName() == null ? patientName : current.getName();
                        patientPhone = current.getPhone() == null ? "" : current.getPhone();
                    }
                } catch (Exception ignored) {}
            }

            Appointment ap = new Appointment();
            ap.setPatientId(patientId);
            ap.setPatientName(patientName);
            ap.setPatientPhone(patientPhone);
            ap.setDate(ld.toString());
            ap.setStartTime(st.toString());
            ap.setEndTime(et.toString());
            ap.setStartTs(startTs);
            ap.setEndTs(endTs);
            ap.setStatus("waiting");
            ap.setDoctorId(selDoc.getId());
            ap.setDepartmentId(selDept.getId());
            String notes = "priority:" + (priority == null ? "normal" : priority) + ";symptoms:" + (symptoms == null ? "" : symptoms.replaceAll(";", ","));
            ap.setNotes(notes);

            long id = appointmentService.bookAppointmentTransactional(ap);
            if (id > 0) {
                com.example.qsync_2207097_desktop.model.Appointment saved = appointmentService.getById(id);
                String tokenInfo = "";
                if (saved != null && saved.getToken() != null) tokenInfo = " (token: " + saved.getToken() + ")";
                Alert ok = new Alert(Alert.AlertType.INFORMATION, "Booked appointment id: " + id + tokenInfo);
                ok.showAndWait();
            } else {
                Alert fail = new Alert(Alert.AlertType.WARNING, "Requested time is not available");
                fail.showAndWait();
            }
        } catch (Exception ex) {
            Alert er = new Alert(Alert.AlertType.ERROR, "Booking failed: " + ex.getMessage());
            er.showAndWait();
        }
    }
}
