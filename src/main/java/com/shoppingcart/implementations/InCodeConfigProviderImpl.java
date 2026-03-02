package com.shoppingcart.implementations;

import com.shoppingcart.interfaces.ConfigProvider;

import java.time.Duration;

public class InCodeConfigProviderImpl implements ConfigProvider {


    @Override
    public Duration getProductCacheDuration() {
        return Duration.ofMinutes(5);
    }

    @Override
    public String getProductBaseUrl() {
        return "https://equalexperts.github.io";
    }

    @Override
    public double getTaxPercentage() {
        return 12.5;
    }

}
