package com.shoppingcart.interfaces;

import java.time.Duration;

public interface Cache {
    <T> T get(String key, Class<T> type);
    <T> void set(String key, T value, Duration expiration);
}
