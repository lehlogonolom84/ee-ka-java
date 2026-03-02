package com.shoppingcart.valueobjects;

public class ProductInfo {
    private String title;
    private double price;

    public ProductInfo() {
    }

    public ProductInfo(String title, double price) {
        this.price=price;
        this.title=title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
