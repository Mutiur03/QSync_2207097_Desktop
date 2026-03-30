package com.example.qsync_2207097_desktop.model;

public class Doctor {
    private String id;
    private long departmentId;
    private String name;
    private String email;
    private String phone;
    private String specialty;
    private String startTime;
    private int vgTimeMinutes;
    private int yearsOfExperience;
    private long createdAt;

    public Doctor() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(long departmentId) {
        this.departmentId = departmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getVgTimeMinutes() {
        return vgTimeMinutes;
    }

    public void setVgTimeMinutes(int vgTimeMinutes) {
        this.vgTimeMinutes = vgTimeMinutes;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "id='" + id + '\'' +
                ", departmentId=" + departmentId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", specialty='" + specialty + '\'' +
                ", startTime='" + startTime + '\'' +
                ", vgTimeMinutes=" + vgTimeMinutes +
                ", yearsOfExperience=" + yearsOfExperience +
                '}';
    }
}

