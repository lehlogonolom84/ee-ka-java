package com.shoppingcart.interfaces;

import com.shoppingcart.models.CartItem;

import java.util.Map;

public interface ProductValidator {
    String validateAdd(String productName, int quantity);
    String validateRemoval(String productName, int quantity, Map<String, CartItem> shoppingCart);
}
