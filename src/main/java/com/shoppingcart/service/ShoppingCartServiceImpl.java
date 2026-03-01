package com.shoppingcart.service;

import com.shoppingcart.constant.DecimalPlaces;
import com.shoppingcart.interfaces.configuration.ConfigProvider;
import com.shoppingcart.interfaces.repository.ProductRepository;
import com.shoppingcart.interfaces.service.ShoppingCartService;
import com.shoppingcart.interfaces.validator.ProductValidator;
import com.shoppingcart.model.Cart;
import com.shoppingcart.model.CartActionResult;
import com.shoppingcart.model.CartItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCartServiceImpl implements ShoppingCartService {

    Map<String, CartItem> shoppingCart;

    ProductRepository productRepository;

    ProductValidator productValidator;

    double taxRate;

    public ShoppingCartServiceImpl(ConfigProvider configProvider, ProductRepository productRepository,ProductValidator productValidator) {

        this.productRepository=productRepository;
        this.productValidator=productValidator;

        shoppingCart = new HashMap<>();
        taxRate = configProvider.getTaxPercentage()/100;
    }

    @Override
    public CartActionResult addItem(String productName, int quantity) {

        var addActionResult = new CartActionResult();

        var validationResults = productValidator.validateAdd(productName, quantity);

        if (validationResults != null && !validationResults.isEmpty()) {
            addActionResult.setMessage(validationResults);
            return addActionResult;
        }

        var price = productRepository.get(productName);

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
    public CartActionResult removeItem(String productName, int quantity) {

        var removeActionResult = new CartActionResult();

        var validationResults = productValidator.validateRemoval(productName, quantity, shoppingCart);

        if (validationResults != null && !validationResults.isEmpty()) {
            removeActionResult.setMessage(validationResults);
            return removeActionResult;
        }

        if (shoppingCart.containsKey(productName)) {
            var shoppingCartItem = this.shoppingCart.get(productName);
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
    public Cart getCart() {

        Cart cart = new Cart();
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
