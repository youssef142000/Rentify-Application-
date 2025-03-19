package com.example.rentifyapp;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private String id;
    private String categoryName;
    private String categoryDescription;
    private List<Item> items;


    public Category() {
        items = new ArrayList<>();
    }
    public Category( String ID, String name, String description) {
        id = ID;
        categoryName = name;
        categoryDescription = description;
        this.items = new ArrayList<>();
    }
    public void setId(String ID) {
        id = ID;
    }
    public String getId() {
        return id;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }
    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    };

    public String getCategoryDescription() {
        return categoryDescription;
    }
    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }    @Override
    public String toString() {
        return categoryName;  // Display category name in Spinner
    }

}


