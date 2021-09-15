package com.ekspeace.buddystaff.Model;

public class Category {
    private String name;
    private String price;

    public Category() {
    }

    public Category(String price) {
        this.price = price;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String  getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
