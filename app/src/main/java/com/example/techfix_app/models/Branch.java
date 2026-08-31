
package com.example.techfix_app.models;

public class Branch {

    private String branchId;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private boolean active;

    // Required empty constructor for Firebase Firestore
    public Branch() {
    }

    // Constructor
    public Branch(String branchId, String name, double latitude, double longitude) {
        this.branchId = branchId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.active = true;
    }

    // Full constructor
    public Branch(String branchId, String name, String address,
                  double latitude, double longitude, boolean active) {
        this.branchId = branchId;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.active = active;
    }

    // Getters
    public String getBranchId() {
        return branchId;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean isActive() {
        return active;
    }

    // Setters
    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

