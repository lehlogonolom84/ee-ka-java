package com.shoppingcart.interfaces.repository;

public interface ProductRepository {
    double get(String productName);
    String[] getAllProductNames();
}
