package com.example.techfix_app.models;

import com.google.firebase.firestore.FieldValue;

public class Appointment {

    private String appointmentId;
    private String customerId;
    private String customerName;
    private String serviceId;
    private String branchId;
    private String technicianId;

    private String prefferedDate;
    private String timeSlot;
    private String deviceName;
    private String deviceCategory;
    private String problemDescription;

    private String status;
    private FieldValue createdAt;

    public Appointment() {
    }

    public Appointment(
            String appointmentId,
            String customerId,
            String customerName,
            String serviceId,
            String branchId,
            String technicianId,
            String prefferedDate,
            String timeSlot,
            String deviceName,
            String deviceCategory,
            String problemDescription,
            String status,
            FieldValue createdAt) {

        this.appointmentId = appointmentId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.serviceId = serviceId;
        this.branchId = branchId;
        this.technicianId = technicianId;
        this.prefferedDate = prefferedDate;
        this.timeSlot = timeSlot;
        this.deviceCategory = deviceCategory;
        this.deviceName = deviceName;
        this.problemDescription = problemDescription;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName(){return  customerName;}

    public void setCustomerName(String customerName){this.customerName = customerName;}

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(String technicianId) {
        this.technicianId = technicianId;
    }

    public String getAppointmentDate() {
        return prefferedDate;
    }

    public void setAppointmentDate(String prefferedDate) {
        this.prefferedDate = prefferedDate;
    }

    public String getAppointmentTime() {
        return timeSlot;
    }

    public void setAppointmentTime(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getDeviceName() {
        return deviceName;
    }
    public void setDeviceName(String deviceName) {
        this.deviceName =  deviceName;
    }

    public String getDeviceCategory() {
        return deviceCategory;
    }

    public void setDeviceCategory(String deviceCategory) {
        this.deviceCategory = deviceCategory;
    }

    public String getDeviceDescription() {
        return problemDescription;
    }

    public void setDeviceDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public FieldValue getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(FieldValue createdAt) {
        this.createdAt = createdAt;
    }
}