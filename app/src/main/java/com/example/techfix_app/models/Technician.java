package com.example.techfix_app.models;

public class Technician {
    private int technicianId;
    private String name;
    private String specialization;
    private String phone;
    private int branchId;

    public Technician() { }

    public int getTechnicianId() { return technicianId; }
    public String getName() { return name; }
    public String getSpecialization() { return specialization; }
    public String getPhone() { return phone; }
    public int getBranchId() { return branchId; }

    public void setTechnicianId(int technicianId) { this.technicianId = technicianId; }
    public void setName(String name) { this.name = name; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setBranchId(int branchId) { this.branchId = branchId; }
}