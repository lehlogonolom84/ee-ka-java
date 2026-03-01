package com.shoppingcart.configuration;

import com.shoppingcart.interfaces.configuration.ConfigProvider;

import java.time.Duration;

public class InCodeConfigProvider implements ConfigProvider {

    private Duration productCacheDuration;
    private String productBaseUrl;
    private double taxPercentage;

    public InCodeConfigProvider() {

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
