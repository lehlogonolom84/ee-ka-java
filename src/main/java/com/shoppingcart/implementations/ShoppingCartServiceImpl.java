package com.shoppingcart.implementations;

import com.shoppingcart.constant.DecimalPlaces;
import com.shoppingcart.interfaces.ConfigProvider;
import com.shoppingcart.interfaces.ProductCatalog;
import com.shoppingcart.interfaces.ShoppingCartService;
import com.shoppingcart.interfaces.ProductValidator;
import com.shoppingcart.models.Cart;
import com.shoppingcart.models.CartActionResult;
import com.shoppingcart.models.CartItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCartServiceImpl implements ShoppingCartService {

    Map<String, Map<String, CartItem>> carts;

    ProductCatalog productCatalog;

    ProductValidator productValidator;

    double taxRate;

    public ShoppingCartServiceImpl(ConfigProvider configProvider, ProductCatalog productCatalog, ProductValidator productValidator) {
        this.productCatalog = productCatalog;
        this.productValidator = productValidator;
        this.carts = new HashMap<>();
        this.taxRate = configProvider.getTaxPercentage() / 100;
    }

    private Map<String, CartItem> getOrCreateCart(String cartId) {
        return carts.computeIfAbsent(cartId, k -> new HashMap<>());
    }

    @Override
    public CartActionResult addItem(String cartId, String productName, int quantity) {
        var addActionResult = new CartActionResult();

        var validationResults = productValidator.validateAdd(productName, quantity);

        if (validationResults != null && !validationResults.isEmpty()) {
            addActionResult.setMessage(validationResults);
            return addActionResult;
        }

        var shoppingCart = getOrCreateCart(cartId);
        var price = productCatalog.get(productName);

        if (!shoppingCart.containsKey(productName)) {
            var cartItem = new CartItem();
            cartItem.setPrice(price);
            cartItem.setQuantity(quantity);
            cartItem.setProductName(productName);
            shoppingCart.put(productName, cartItem);
        } else {
            var shoppingCartItem = shoppingCart.get(productName);
            shoppingCartItem.setQuantity(shoppingCartItem.getQuantity() + quantity);
        }
        return addActionResult;
    }

    @Override
    public CartActionResult removeItem(String cartId, String productName, int quantity) {
        var removeActionResult = new CartActionResult();

        var shoppingCart = getOrCreateCart(cartId);
        var validationResults = productValidator.validateRemoval(productName, quantity, shoppingCart);

        if (validationResults != null && !validationResults.isEmpty()) {
            removeActionResult.setMessage(validationResults);
            return removeActionResult;
        }

        if (shoppingCart.containsKey(productName)) {
            var shoppingCartItem = shoppingCart.get(productName);
            int newQuantity = shoppingCartItem.getQuantity() - quantity;
            if (newQuantity <= 0) {
                shoppingCart.remove(productName);
            } else {
                shoppingCartItem.setQuantity(newQuantity);
            }
        }
        return removeActionResult;
    }

    @Override
    public Cart getCart(String cartId) {
        var shoppingCart = getOrCreateCart(cartId);

        Cart cart = new Cart();
        cart.setCartId(cartId);
        cart.setItems(shoppingCart.values().toArray(new CartItem[0]));

        double subTotal = shoppingCart.values().stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
        cart.setSubTotal(subTotal);

        BigDecimal tax = BigDecimal.valueOf(subTotal * taxRate)
                .setScale(DecimalPlaces.TWO, RoundingMode.HALF_UP);
        cart.setTax(tax.doubleValue());

        return cart;
    }
}
