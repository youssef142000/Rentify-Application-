package com.example.rentifyapp;

public class Item {
private String id;
private String itemName;
private String itemDescription;
private double price;
private String lessorId;
private String categoryId;
private String categoryName;
private String timePeriod;
private boolean pending;
private String rentStatus;
    public Item() {
}
    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }

public Item(String lessorId, String ID, String name, String description, double Price, String categoryId, String categoryName, String timePeriod, boolean pending, String rentStatus) {

    this.id = ID;
    this.lessorId = lessorId;
    this.itemName = name;
    this.itemDescription = description;
    this.price = Price;
    this.categoryId = categoryId;
    this.categoryName = categoryName;
    this.timePeriod = timePeriod;
    this.pending = pending;
    this.rentStatus =rentStatus;

}

    public String getRentStatus() {
        return this.rentStatus;
    }
    public void setRentStatus(String rentStatus) {
       this.rentStatus = rentStatus;
    }
public String getLessorId() { return lessorId; }
public void setLessorId(String userId) { this.lessorId = userId; }
public void setId(String ID) {
    id = ID;
}
public String getId() {

    return id;
}
public void setItemName(String itemName) {
    this.itemName = itemName;
}

public String getItemName() {
    return itemName;
}
public String getItemDescription() {
    return itemDescription;
}
public void setPrice(double Price) {
    price = Price;
}
public double getPrice() {

    return price;
}

public String getTimePeriod(){
        return this.timePeriod;
}

public void setTimePeriod(String timePeriod){
        this.timePeriod = timePeriod;
}

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

}
