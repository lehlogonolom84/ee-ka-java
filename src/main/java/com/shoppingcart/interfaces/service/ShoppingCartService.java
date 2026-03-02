package com.shoppingcart.interfaces.service;

import com.shoppingcart.valueobjects.Cart;
import com.shoppingcart.valueobjects.CartActionResult;

public interface ShoppingCartService {
    CartActionResult addItem(String cartId, String productName, int quantity);
    CartActionResult removeItem(String cartId, String productName, int quantity);
    Cart getCart(String cartId);
}
