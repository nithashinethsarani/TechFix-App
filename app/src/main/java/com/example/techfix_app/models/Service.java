package com.example.techfix_app.models;

import java.util.ArrayList;
import java.util.List;

public class Service {

    private String id;
    private String name;
    private String description;
    private double price;
    private String deviceCategory;
    private String availability;
    private List<String> inventoryItemIds;

    public Service() {
        inventoryItemIds = new ArrayList<>();
    }

    public Service(String id,
                   String name,
                   String description,
                   double price,
                   String deviceCategory,
                   String availability,
                   List<String> inventoryItemIds) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.deviceCategory = deviceCategory;
        this.availability = availability;

        this.inventoryItemIds = inventoryItemIds == null
                ? new ArrayList<>()
                : inventoryItemIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDeviceCategory() {
        return deviceCategory;
    }

    public void setDeviceCategory(String deviceCategory) {
        this.deviceCategory = deviceCategory;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public List<String> getInventoryItemIds() {
        return inventoryItemIds;
    }

    public void setInventoryItemIds(List<String> inventoryItemIds) {
        this.inventoryItemIds = inventoryItemIds == null
                ? new ArrayList<>()
                : inventoryItemIds;
    }
}