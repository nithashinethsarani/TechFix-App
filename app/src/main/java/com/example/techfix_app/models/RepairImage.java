
package com.example.techfix_app.models;

public class RepairImage {

    private int imageId;
    private String repairId;
    private String branchId;
    private String deviceCategory;
    private String imagePath;
    private String caption;
    private long timestamp;

    public RepairImage() {
    }

    public RepairImage(
            String repairId,
            String branchId,
            String deviceCategory,
            String imagePath,
            String caption,
            long timestamp
    ) {
        this.repairId = repairId;
        this.branchId = branchId;
        this.deviceCategory = deviceCategory;
        this.imagePath = imagePath;
        this.caption = caption;
        this.timestamp = timestamp;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getRepairId() {
        return repairId;
    }

    public void setRepairId(String repairId) {
        this.repairId = repairId;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getDeviceCategory() {
        return deviceCategory;
    }

    public void setDeviceCategory(String deviceCategory) {
        this.deviceCategory = deviceCategory;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}