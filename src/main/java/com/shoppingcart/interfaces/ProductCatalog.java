package com.shoppingcart.interfaces;

public interface ProductCatalog {
    double get(String productName);
    String[] getAllProductNames();
}
