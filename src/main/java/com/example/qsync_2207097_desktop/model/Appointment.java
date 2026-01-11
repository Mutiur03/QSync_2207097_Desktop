package com.example.qsync_2207097_desktop.model;

public class Appointment {
    private long id;
    private Long patientId;
    private String patientName;
    private String patientPhone;
    private String date; // YYYY-MM-DD
    private String startTime; // HH:MM
    private String endTime;   // HH:MM
    private long startTs;
    private long endTs;
    private String status; // waiting, in_progress, cancelled, completed
    private String notes;
    private long createdAt;
    private Long updatedAt;
    private String doctorId;
    private Long departmentId;
    private Integer token;
    private String priority;

    public Appointment() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getPatientPhone() { return patientPhone; }
    public void setPatientPhone(String patientPhone) { this.patientPhone = patientPhone; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public long getStartTs() { return startTs; }
    public void setStartTs(long startTs) { this.startTs = startTs; }

    public long getEndTs() { return endTs; }
    public void setEndTs(long endTs) { this.endTs = endTs; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public Integer getToken() { return token; }
    public void setToken(Integer token) { this.token = token; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
}
