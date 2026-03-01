package com.shoppingcart.interfaces.configuration;

import java.time.Duration;

public interface ConfigProvider {
    Duration getProductCacheDuration();

    String getProductBaseUrl();

    double getTaxPercentage();
}
