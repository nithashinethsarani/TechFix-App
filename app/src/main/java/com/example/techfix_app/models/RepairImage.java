package com.example.techfix_app.models;

public class RepairImage {
    private int imageId;
    private int branchId;
    private String deviceCategory;
    private String imagePath;
    private String caption;
    private long timestamp;

    public RepairImage() { }

    public RepairImage(int branchId, String deviceCategory, String imagePath,
                       String caption, long timestamp) {
        this.branchId = branchId;
        this.deviceCategory = deviceCategory;
        this.imagePath = imagePath;
        this.caption = caption;
        this.timestamp = timestamp;
    }

    public int getImageId() { return imageId; }
    public void setImageId(int imageId) { this.imageId = imageId; }
    public int getBranchId() { return branchId; }
    public void setBranchId(int branchId) { this.branchId = branchId; }
    public String getDeviceCategory() { return deviceCategory; }
    public void setDeviceCategory(String deviceCategory) { this.deviceCategory = deviceCategory; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}