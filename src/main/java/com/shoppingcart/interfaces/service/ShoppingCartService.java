package com.shoppingcart.interfaces.service;

import com.shoppingcart.model.Cart;
import com.shoppingcart.model.CartActionResult;

public interface ShoppingCartService {
    CartActionResult addItem(String productName, int quantity);
    CartActionResult removeItem(String productName, int quantity);
    Cart getCart();
}
