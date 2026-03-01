package com.shoppingcart.model;

import com.shoppingcart.constant.DecimalPlaces;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Cart {
    private double tax;
    private CartItem[] items;
    private double subTotal;

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public CartItem[] getItems() {
        return items;
    }

    public void setItems(com.shoppingcart.model.CartItem[] items) {
        this.items = items;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getTotal() {
        return BigDecimal.valueOf(subTotal + tax)
                .setScale(DecimalPlaces.TWO, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
