package com.example.techfix_app.models;

public class RepairHistory {

    private String repairId;
    private String appointmentId;
    private String deviceName;
    private String serviceName;
    private double finalPrice;
    private String completedDate;
    private String status;

    public RepairHistory() {}

    public RepairHistory(String repairId, String appointmentId, String deviceName,
                              String serviceName, double finalPrice, String completedDate, String status) {
        this.repairId = repairId;
        this.appointmentId = appointmentId;
        this.deviceName = deviceName;
        this.serviceName = serviceName;
        this.finalPrice = finalPrice;
        this.completedDate = completedDate;
        this.status = status;
    }

    public String getRepairId() { return repairId; }
    public void setRepairId(String repairId) { this.repairId = repairId; }

    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public double getFinalPrice() { return finalPrice; }
    public void setFinalPrice(double finalPrice) { this.finalPrice = finalPrice; }

    public String getCompletedDate() { return completedDate; }
    public void setCompletedDate(String completedDate) { this.completedDate = completedDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}