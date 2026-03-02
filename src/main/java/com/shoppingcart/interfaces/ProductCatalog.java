package com.shoppingcart.interfaces;

public interface ProductCatalog {
    double getPrice(String productName);
    String[] getAllProductNames();
}
