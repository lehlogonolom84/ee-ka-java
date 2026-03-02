package com.shoppingcart.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoppingcart.constant.CachePrefix;
import com.shoppingcart.constant.ProductName;
import com.shoppingcart.interfaces.Cache;
import com.shoppingcart.interfaces.ConfigProvider;
import com.shoppingcart.interfaces.ProductCatalog;
import com.shoppingcart.models.ProductInfo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ProductCatalogImpl implements ProductCatalog {

    private final Cache cache;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Duration cacheDuration;
    private final String productBaseUrl;

    public ProductCatalogImpl(Cache cache, ConfigProvider configProvider) {
        this.cache = cache;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.cacheDuration = configProvider.getProductCacheDuration();
        this.productBaseUrl = configProvider.getProductBaseUrl();
    }

    @Override
    public double getPrice(String productName) {

        var cacheKey = CachePrefix.PRODUCT_PRICE + productName;
        var cachedPrice = cache.get(cacheKey, Double.class);

        if (cachedPrice != null) {
            return cachedPrice;
        }

        try {

            var url = productBaseUrl + "/backend-take-home-test-data/" + productName + ".json";
            var request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed to fetch product: " + productName);
            }

            var product = objectMapper.readValue(response.body(), ProductInfo.class);

            cache.set(cacheKey, product.getPrice(), cacheDuration);

            return product.getPrice();

        } catch (Exception e) {

            throw new RuntimeException("Unable to fetch product price for: " + productName, e);

        }
    }

    @Override
    public String[] getAllProductNames() {

        return new String[]{
                ProductName.CHEERIOS,
                ProductName.CORNFLAKES,
                ProductName.FROSTIES,
                ProductName.SHREDDIES,
                ProductName.WEETABIX
        };

    }
}
