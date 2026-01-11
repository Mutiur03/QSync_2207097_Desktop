package com.example.qsync_2207097_desktop.admin;

import com.example.qsync_2207097_desktop.AdminController;
import com.example.qsync_2207097_desktop.config.DatabaseConfig;
import com.example.qsync_2207097_desktop.model.User;
import com.example.qsync_2207097_desktop.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class AdminUsersFragmentController {
    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, String> colName;
    @FXML
    private TableColumn<User, String> colEmail;
    @FXML
    private TableColumn<User, String> colPhone;
    @FXML
    private TableColumn<User, String> colGender;
    @FXML
    private TableColumn<User, String> colDob;

    private UserService userService;
    private final ObservableList<User> userList = FXCollections.observableArrayList();

    public void setParent(AdminController parent) { }

    @FXML
    public void initialize() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colDob.setCellValueFactory(new PropertyValueFactory<>("dob"));

        usersTable.setItems(userList);
        loadUsers();
    }

    private void loadUsers() {
        if (userService == null) userService = new UserService(new DatabaseConfig());
        userList.setAll(userService.getAllUsers());
    }
}

