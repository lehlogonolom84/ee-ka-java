package com.shoppingcart.implementations;

import com.shoppingcart.interfaces.ConfigProvider;

import java.time.Duration;

public class InCodeConfigProviderImpl implements ConfigProvider {

    private Duration productCacheDuration;
    private String productBaseUrl;
    private double taxPercentage;

    public InCodeConfigProviderImpl() {

        this.productCacheDuration = Duration.ofMinutes(5);
        this.taxPercentage=12.5;
        this.productBaseUrl="https://equalexperts.github.io";
    }

    @Override
    public Duration getProductCacheDuration() {
        return productCacheDuration;
    }


    @Override
    public String getProductBaseUrl() {
        return productBaseUrl;
    }


    @Override
    public double getTaxPercentage() {
        return taxPercentage;
    }

}
