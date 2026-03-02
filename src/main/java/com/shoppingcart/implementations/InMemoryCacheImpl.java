package com.shoppingcart.implementations;

import com.shoppingcart.interfaces.Cache;
import com.shoppingcart.models.CacheEntry;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class InMemoryCacheImpl implements Cache {

    private final Map<String, CacheEntry> cache = new HashMap<>();
    private final Object sync = new Object();

    @Override
    public <T> void set(String key, T value, Duration expiration) {

        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Cache key cannot be null or empty.");
        }

        Instant expiresAtUtc = Instant.now().plus(expiration);

        synchronized (sync) {
            cache.put(key, new CacheEntry(value, expiresAtUtc));
        }
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        if (key == null || key.isBlank()) {
            return null;
        }

        synchronized (sync) {
            CacheEntry entry = cache.get(key);
            if (entry == null) {
                return null;
            }

            if (entry.getExpiresAtUtc().isBefore(Instant.now()) ||
                entry.getExpiresAtUtc().equals(Instant.now())) {
                cache.remove(key);
                return null;
            }

            return (T) entry.getValue();
        }
    }
}
