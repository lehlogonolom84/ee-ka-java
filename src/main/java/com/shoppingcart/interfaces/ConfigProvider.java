package com.shoppingcart.interfaces;

import java.time.Duration;

public interface ConfigProvider {
    Duration getProductCacheDuration();

    String getProductBaseUrl();

    double getTaxPercentage();
}
