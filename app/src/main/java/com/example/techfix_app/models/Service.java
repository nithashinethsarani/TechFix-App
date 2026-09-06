package com.example.techfix_app.models;

import java.util.ArrayList;
import java.util.List;

public class Service {

    private String id;
    private String name;
    private String description;
    private double price;
    private String deviceCategory;
    private Boolean isAvailable;
    private List<String> inventoryItemIds;

    public Service() {
        inventoryItemIds = new ArrayList<>();
    }

    public Service(String id,
                   String name,
                   String description,
                   double price,
                   String deviceCategory,
                   Boolean isAvailable,
                   List<String> inventoryItemIds) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.deviceCategory = deviceCategory;
        this.isAvailable = isAvailable;

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

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean availability) {
        this.isAvailable = availability;
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