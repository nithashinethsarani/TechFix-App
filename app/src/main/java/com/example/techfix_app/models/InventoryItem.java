package com.example.techfix_app.models;

public class InventoryItem {

    private String itemId;
    private String itemName;
    private int quantity;
    private double price;
    private String branchId;

    // Required by Firestore
    public InventoryItem() {}

    public String getItemId(){return itemId;}
    public void setItemId(String itemId) {this.itemId = itemId;}
    public String getItemName() {return itemName;}
    public void setItemName(String itemName) {this.itemName = itemName;}
    public int getQuantity() {return quantity;}
    public void setQuantity(int quantity) {this.quantity = quantity;}
    public double getPrice() {return price;}
    public void setPrice(double price) {this.price = price;}
    public String getBranchId() {return branchId;}
    public void setBranchId(String branchId) {this.branchId = branchId;}
}