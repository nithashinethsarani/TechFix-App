package com.example.techfix_app.models;

public class Branch {
    private String  branchId;
    private String name;
    private String address;
    private String phone;
    private double latitude;
    private double longitude;

    public Branch() { }

    // Location member ge constructor eka (id String widihata gannawa, int ekata convert karanawa)
    public Branch(String branchId, String name,String address,String phone, double latitude, double longitude) {
        this.branchId = branchId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getBranchId() { return branchId; }
    public String getName() { return name; }
    public String getAddress() { return address; }

    public String getPhone() { return phone; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }



    public void setBranchId(String branchId) { this.branchId = branchId; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }

    public void setPhone(String phone) { this.phone = phone; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}