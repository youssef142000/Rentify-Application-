package com.example.rentifyapp;

public class RentalRequest {

    private String renterId;
    private String itemId;
    private String ownerId;

    public RentalRequest(String renterId, String itemId, String ownerId) {
        this.renterId = renterId;
        this.itemId = itemId;
        this.ownerId = ownerId;
    }

    public String getRenterId() {
        return renterId;
    }

    public void setRenterId(String renterId) {
        this.renterId = renterId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
