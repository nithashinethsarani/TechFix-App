package com.example.techfix_app.models;

public class Branch {
    private int branchId;
    private String name;
    private String address;
    private String city;
    private String phone;
    private double latitude;
    private double longitude;

    public Branch() { }

    // Location member ge constructor eka (id String widihata gannawa, int ekata convert karanawa)
    public Branch(String branchId, String name, double latitude, double longitude) {
        this.branchId = Integer.parseInt(branchId);
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getBranchId() { return branchId; }
    public void setBranchId(int branchId) { this.branchId = branchId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}