package com.example.techfix_app.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Repair {

    private String repairId;
    private String appointmentId;
    private String customerId;
    private String serviceId;
    private String branchId;
    private String technicianId;

    private String deviceCategory;
    private String deviceName;
    private String deviceDescription;

    private String diagnosis;
    private String repairNotes;

    private String status;
    private String paymentStatus;

    private double finalPrice;

    @ServerTimestamp
    private Date createdAt;
    private Date updatedAt;
    private Date completedAt;

    public Repair() {
    }

    public Repair(
            String repairId,
            String appointmentId,
            String customerId,
            String serviceId,
            String branchId,
            String technicianId,
            String deviceCategory,
            String deviceName,
            String deviceDescription,
            String diagnosis,
            String repairNotes,
            String status,
            String paymentStatus,
            double finalPrice,
            Date createdAt,
            Date updatedAt,
            Date completedAt) {

        this.repairId = repairId;
        this.appointmentId = appointmentId;
        this.customerId = customerId;
        this.serviceId = serviceId;
        this.branchId = branchId;
        this.technicianId = technicianId;
        this.deviceCategory = deviceCategory;
        this.deviceName = deviceName;
        this.deviceDescription = deviceDescription;
        this.diagnosis = diagnosis;
        this.repairNotes = repairNotes;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.finalPrice = finalPrice;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.completedAt = completedAt;
    }

    public String getRepairId() {
        return repairId;
    }

    public void setRepairId(String repairId) {
        this.repairId = repairId;
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

    public String getDeviceCategory() {
        return deviceCategory;
    }

    public void setDeviceCategory(String deviceCategory) {
        this.deviceCategory = deviceCategory;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceDescription() {
        return deviceDescription;
    }

    public void setDeviceDescription(String problemDescription) {
        this.deviceDescription = problemDescription;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getRepairNotes() {
        return repairNotes;
    }

    public void setRepairNotes(String repairNotes) {
        this.repairNotes = repairNotes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Date completedAt) {
        this.completedAt = completedAt;
    }
}