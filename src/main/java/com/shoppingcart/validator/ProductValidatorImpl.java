package com.shoppingcart.validator;

import com.shoppingcart.interfaces.repository.ProductRepository;
import com.shoppingcart.model.CartItem;

import java.util.Arrays;
import java.util.Map;

public class ProductValidatorImpl implements com.shoppingcart.interfaces.validator.ProductValidator {

    private final ProductRepository productRepository;

    public ProductValidatorImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public String validateAdd(String productName, int quantity) {
        String productNameError = validateProductName(productName);
        if (!productNameError.isEmpty()) {
            return productNameError;
        }

        return validateQuantity(quantity);
    }

    @Override
    public String validateRemoval(String productName, int quantity, Map<String, CartItem> shoppingCart) {

        String error="";

        error = validateProductName(productName);
        if (!error.isEmpty()) {
            return error;
        }

        if (!shoppingCart.containsKey(productName)) {
            return "Product '" + productName + "' is not in the shopping cart.";
        }

        error = validateQuantity(quantity);
        if (!error.isEmpty()) {
            return error;
        }

        int cartQuantity = shoppingCart.get(productName).getQuantity();
        if (quantity > cartQuantity) {
            return "Cannot remove " + quantity + " of '" + productName + "' as only " + cartQuantity + " are in the cart.";
        }

        return error;
    }

    private String validateProductName(String productName) {

        if (productName == null || productName.isEmpty()) {
            return "Product name cannot be empty.";
        }

        if (!isValidProduct(productName)) {
            return "Product name '" + productName + "' is not valid.";
        }

        return "";
    }

    private String validateQuantity(int quantity) {
        if (quantity <= 0) {
            return "Quantity must be greater than zero.";
        }
        return "";
    }

    private boolean isValidProduct(String productName) {
        String[] validProducts = productRepository.getAllProductNames();
        return Arrays.asList(validProducts).contains(productName);
    }
}
