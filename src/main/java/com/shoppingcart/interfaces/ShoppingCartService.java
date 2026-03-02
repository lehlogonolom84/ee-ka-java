package com.shoppingcart.interfaces;

import com.shoppingcart.models.Cart;
import com.shoppingcart.models.CartActionResult;

public interface ShoppingCartService {
    CartActionResult addItem(String cartId, String productName, int quantity);
    CartActionResult removeItem(String cartId, String productName, int quantity);
    Cart getCart(String cartId);
}
