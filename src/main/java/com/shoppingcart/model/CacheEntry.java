package com.shoppingcart.model;

import java.time.Instant;

public class CacheEntry {
    private final Object value;
    private final Instant expiresAtUtc;

    public CacheEntry(Object value, Instant expiresAtUtc) {
        this.value = value;
        this.expiresAtUtc = expiresAtUtc;
    }

    public Object getValue() {
        return value;
    }

    public Instant getExpiresAtUtc() {
        return expiresAtUtc;
    }
}
