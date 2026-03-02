package com.shoppingcart.models;

public class CartActionResult {
    private String message;

    public String getMessage() {

        return message;
    }

    public void setMessage(String message) {

        this.message = message;
    }

    public boolean isSuccess() {

        return message == null || message.isEmpty();
    }
}
