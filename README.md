# QSync - Queue Management System (Desktop)

**Student ID:** 2207097

## 📋 Project Description

QSync Desktop is an intuitive and robust queue management system designed for healthcare facilities. Built with Java and JavaFX, it provides a powerful desktop interface for both administrators and users to manage appointments, track real-time queues, and streamline healthcare operations. The application leverages SQLite for reliable local data persistence and follows a modular service-oriented architecture.

### Key Features

- **Dual Interaction Portals**: Tailored interfaces for patient bookings and administrative oversight.
- **Real-time Queue Dashboard**: Dynamic monitoring of patient status across multiple departments.
- **Comprehensive Admin Control**: Tools for managing departments, doctors, and user accounts.
- **Priority-based Scheduling**: Systematic handling of Normal, Urgent, and Emergency cases.
- **Reporting & Analytics**: Built-in reporting modules for healthcare performance tracking.
- **Secure Authentication**: Role-based access control with session management.
- **Modern UI/UX**: Professional desktop interface using JavaFX and Material-inspired design principles.

---

## 🚀 Work Completed So Far

### ✅ Core Infrastructure

- **Database Architecture**: Implemented SQLite schema for users, appointments, doctors, and departments.
- **DAO Pattern**: Clean separation of data access logic using the Data Access Object pattern.
- **Service Layer**: Robust business logic handling for appointments, sessions, and security.

### ✅ User Portal

- **Dashboard**: Real-time view of available services and current queue status.
- **Booking System**: Streamlined appointment creation with department and doctor selection.
- **Profile Management**: Personalized settings and password management.
- **History & Files**: Tracking of past appointments and medical file management.

### ✅ Admin Portal

- **Live Monitoring**: Tree-view based live queue tracking with hierarchical organization.
- **Management Center**:
  - **Departments**: Full CRUD operations for healthcare units.
  - **Doctors**: Management of healthcare providers with department assignments.
  - **Users**: Oversight of registered patients and staff.
- **Queue Control**: Real-time status updates (Waiting, In Progress, Completed, Cancelled).
- **Reports**: Analytical views for facility management.

---

## 🛠️ Technology Stack

- **Language**: Java 25
- **UI Framework**: JavaFX 21 (Controls, FXML, Media, Web)
- **Database**: SQLite JDBC
- **Build System**: Maven
- **Libraries**:
  - **ControlsFX**: For advanced UI components.
  - **Ikonli**: For professional iconography.
  - **BootstrapFX**: For modern CSS styling.
  - **ValidatorFX**: For robust input validation.
  - **FormsFX**: For structured data entry.

---

## 📱 Application Structure

```
QSync Desktop
├── src/main/java/com/example/qsync_2207097_desktop
│   ├── admin/           # Admin portal controllers (Queues, Manage, Reports)
│   ├── user/            # User portal controllers (Booking, Home, Settings)
│   ├── dao/             # Data Access Objects (SQLite implementation)
│   ├── service/         # Business logic & Service layer
│   ├── model/           # POJO data models
│   ├── config/          # Database and app configurations
│   └── HelloApplication # Main entry point & session routing
└── src/main/resources
    ├── com/example/qsync_2207097_desktop/admin/  # Admin FXML layouts
    └── com/example/qsync_2207097_desktop/user/   # User FXML layouts
```

---

## 🎯 Current Status

The desktop application is fully functional with a complete end-to-end flow:

- ✅ Database initialization and migration.
- ✅ Robust authentication and session persistence.
- ✅ Functional User and Admin dashboards.
- ✅ Real-time data synchronization with SQLite.
- ✅ Material-style responsiveness and modern aesthetics.

---

## 📂 Project Documentation

- [QSync - Queue Management System (Desktop).pdf](./QSync%20-%20Queue%20Management%20System%20(Desktop).pdf)
<!-- - [QSync - Queue Management System (Desktop).pptx](./QSync%20-%20Queue%20Management%20System%20(Desktop).pptx) -->

---

## 📄 License

This is an academic project developed as part of coursework (Student ID: 2207097).

---

## 👨‍💻 Developer

**Student ID**: 2207097  
**Project**: QSync - Queue Management System for Healthcare Facilities
