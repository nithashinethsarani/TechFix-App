package com.example.techfix_app.models;

public class Technician {

    private String technicianId;
    private String name;
    private String specialization;
    private String phone;
    private String branchId;

    public Technician() {
    }

    public String getTechnicianId() {return technicianId;}

    public void setTechnicianId(String technicianId) {this.technicianId = technicianId;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public String getSpecialization() {return specialization;}

    public void setSpecialization(String specialization) {this.specialization = specialization;}

    public String getPhone() {return phone;}

    public void setPhone(String phone) {this.phone = phone;}

    public String getBranchId() {return branchId;}

    public void setBranchId(String branchId) {this.branchId = branchId;}
}