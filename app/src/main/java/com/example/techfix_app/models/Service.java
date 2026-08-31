package com.example.techfix_app.models;

public class Service {

    private String id;
    private String deviceCategory;   // "Computer" or "Mobile"
    private String serviceName;
    private double price;
    private String description;
    private boolean available;       // false = spare parts unavailable

    // Required empty constructor for Firestore
    public Service() {
    }

    public Service(String id, String deviceCategory, String serviceName,
                   double price, String description, boolean available) {
        this.id = id;
        this.deviceCategory = deviceCategory;
        this.serviceName = serviceName;
        this.price = price;
        this.description = description;
        this.available = available;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceCategory() {
        return deviceCategory;
    }

    public void setDeviceCategory(String deviceCategory) {
        this.deviceCategory = deviceCategory;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}