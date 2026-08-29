package com.example.techfix_app.models;

public class Service {

    private String id;
    private String deviceCategory;
    private String serviceName;
    private double price;

    // Required empty constructor
    // Useful later when reading Service objects from Firestore
    public Service() {
    }

    public Service(String id, String deviceCategory, String serviceName, double price) {
        this.id = id;
        this.deviceCategory = deviceCategory;
        this.serviceName = serviceName;
        this.price = price;
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
}