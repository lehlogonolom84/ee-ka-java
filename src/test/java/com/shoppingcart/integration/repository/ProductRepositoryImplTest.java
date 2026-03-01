package com.shoppingcart.integration.repository;

import com.shoppingcart.caching.InMemoryCache;
import com.shoppingcart.configuration.InCodeConfigProvider;
import com.shoppingcart.constant.CachePrefix;
import com.shoppingcart.constant.ProductName;
import com.shoppingcart.interfaces.caching.Cache;
import com.shoppingcart.interfaces.configuration.ConfigProvider;
import com.shoppingcart.model.ProductInfo;
import com.shoppingcart.repository.ProductRepositoryImpl;
import com.shoppingcart.testdata.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductRepositoryImplTest {

    private ProductRepositoryImpl productRepository;
    private Cache cache;
    private ConfigProvider configProvider;

    @BeforeEach
    void setUp() {

        cache = new InMemoryCache();
        configProvider = new InCodeConfigProvider();
        productRepository = new ProductRepositoryImpl(cache, configProvider);
    }

    @Test
    void get_withKnownProducts_returnsExpectedPrices() {

        // Arrange
        ProductInfo[] knownProducts = TestData.getKnownProductInfo();

        // Act & Assert
        for (ProductInfo expectedProduct : knownProducts) {
            double actualPrice = productRepository.get(expectedProduct.getTitle());
            assertThat(actualPrice)
                    .as("Price for product '%s'", expectedProduct.getTitle())
                    .isEqualTo(expectedProduct.getPrice());
        }
    }

    @Test
    void get_withValidProduct_returnsConsistentPriceOnMultipleCalls() {

        // Arrange
        String productName = ProductName.CHEERIOS;

        // Act
        double firstPrice = productRepository.get(productName);
        double secondPrice = productRepository.get(productName);

        // Assert
        assertThat(firstPrice).isEqualTo(secondPrice);
    }

    @Test
    void get_forEachProductFromGetAllProductNames_returnsValidPrice() {
        // Arrange
        String[] allProducts = productRepository.getAllProductNames();

        // Act & Assert
        for (String productName : allProducts) {
            double price = productRepository.get(productName);
            assertThat(price)
                    .as("Price for '%s' should be positive", productName)
                    .isPositive();
        }
    }

    @Test
    void get_multipleConcurrentCalls_returnConsistentResults() {
        // Arrange
        String productName = ProductName.WEETABIX;

        // Act
        double price1 = productRepository.get(productName);
        double price2 = productRepository.get(productName);
        double price3 = productRepository.get(productName);

        // Assert
        assertThat(price1).isEqualTo(price2).isEqualTo(price3);
    }

    @Test
    void get_afterFirstCall_returnsCachedValue() {
        // Arrange
        String productName = ProductName.CHEERIOS;
        String cacheKey = CachePrefix.PRODUCT_PRICE + productName;

        // Act
        double priceFromApi = productRepository.get(productName);
        Double cachedPrice = cache.get(cacheKey, Double.class);

        // Assert
        assertThat(cachedPrice).isNotNull();
        assertThat(cachedPrice).isEqualTo(priceFromApi);
    }

    @Test
    void get_withPrePopulatedCache_returnsCachedValueWithoutApiCall() {

        // Arrange
        String productName = ProductName.CORNFLAKES;
        String cacheKey = CachePrefix.PRODUCT_PRICE + productName;
        double expectedCachedPrice = 999.99;
        cache.set(cacheKey, expectedCachedPrice, Duration.ofMinutes(5));

        // Act
        double actualPrice = productRepository.get(productName);

        // Assert
        assertThat(actualPrice).isEqualTo(expectedCachedPrice);
    }

    @Test
    void get_withDifferentProducts_cachesSeparately() {
        // Arrange
        String productName1 = ProductName.CHEERIOS;
        String productName2 = ProductName.FROSTIES;
        String cacheKey1 = CachePrefix.PRODUCT_PRICE + productName1;
        String cacheKey2 = CachePrefix.PRODUCT_PRICE + productName2;

        // Act
        double price1 = productRepository.get(productName1);
        double price2 = productRepository.get(productName2);

        // Assert
        Double cachedPrice1 = cache.get(cacheKey1, Double.class);
        Double cachedPrice2 = cache.get(cacheKey2, Double.class);
        assertThat(cachedPrice1).isEqualTo(price1);
        assertThat(cachedPrice2).isEqualTo(price2);
        assertThat(cachedPrice1).isNotNull();
        assertThat(cachedPrice2).isNotNull();
    }

    @Test
    void get_withNullProductName_throwsRuntimeException() {
        // Arrange
        String productName = null;

        // Act & Assert
        assertThatThrownBy(() -> productRepository.get(productName))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unable to fetch product price for:");
    }

    @Test
    void get_withEmptyProductName_throwsRuntimeException() {
        // Arrange
        String productName = "";

        // Act & Assert
        assertThatThrownBy(() -> productRepository.get(productName))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unable to fetch product price for:");
    }

    @Test
    void get_withBlankProductName_throwsRuntimeException() {
        // Arrange
        String productName = "   ";

        // Act & Assert
        assertThatThrownBy(() -> productRepository.get(productName))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unable to fetch product price for:");
    }

    @Test
    void get_withNonExistentProduct_throwsRuntimeException() {
        // Arrange
        String productName = "nonexistent-product-xyz";

        // Act & Assert
        assertThatThrownBy(() -> productRepository.get(productName))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unable to fetch product price for: nonexistent-product-xyz");
    }

    @Test
    void get_withSpecialCharactersInProductName_throwsRuntimeException() {
        // Arrange
        String productName = "product<>with/special?chars";

        // Act & Assert
        assertThatThrownBy(() -> productRepository.get(productName))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unable to fetch product price for:");
    }

    @Test
    void get_withProductNameContainingSpaces_throwsRuntimeException() {
        // Arrange
        String productName = "product with spaces";

        // Act & Assert
        assertThatThrownBy(() -> productRepository.get(productName))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unable to fetch product price for:");
    }

    @Test
    void get_withUppercaseProductName_throwsRuntimeException() {
        // Arrange
        String productName = "CHEERIOS";

        // Act & Assert
        assertThatThrownBy(() -> productRepository.get(productName))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unable to fetch product price for: CHEERIOS");
    }

    @Test
    void get_withMixedCaseProductName_throwsRuntimeException() {
        // Arrange
        String productName = "Cheerios";

        // Act & Assert
        assertThatThrownBy(() -> productRepository.get(productName))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unable to fetch product price for: Cheerios");
    }

    @Test
    void getAllProductNames_returnsAllKnownProducts() {
        // Arrange
        String[] expectedProducts = {
                ProductName.CHEERIOS,
                ProductName.CORNFLAKES,
                ProductName.FROSTIES,
                ProductName.SHREDDIES,
                ProductName.WEETABIX
        };
        int expectedCount = 5;

        // Act
        String[] actualProducts = productRepository.getAllProductNames();

        // Assert
        assertThat(actualProducts).containsExactly(expectedProducts);
        assertThat(actualProducts).isNotEmpty();
        assertThat(actualProducts).hasSize(expectedCount);
    }

    @Test
    void getAllProductNames_containsCheerios() {
        // Arrange - nothing to arrange

        // Act
        String[] productNames = productRepository.getAllProductNames();

        // Assert
        assertThat(productNames).contains(ProductName.CHEERIOS);
    }

    @Test
    void getAllProductNames_doesNotContainDuplicates() {
        // Arrange - nothing to arrange

        // Act
        String[] productNames = productRepository.getAllProductNames();

        // Assert
        assertThat(productNames).doesNotHaveDuplicates();
    }

    @Test
    void getAllProductNames_doesNotContainEmptyStrings() {
        // Arrange - nothing to arrange

        // Act
        String[] productNames = productRepository.getAllProductNames();

        // Assert
        assertThat(productNames).allMatch(name -> name != null && !name.isEmpty());
    }

    @Test
    void getAllProductNames_returnsNewArrayOnEachCall() {
        // Arrange - nothing to arrange

        // Act
        String[] firstCall = productRepository.getAllProductNames();
        String[] secondCall = productRepository.getAllProductNames();

        // Assert
        assertThat(firstCall).isNotSameAs(secondCall);
        assertThat(firstCall).containsExactly(secondCall);
    }

    @Test
    void getAllProductNames_returnedProductsAreAllLowercase() {
        // Arrange - nothing to arrange

        // Act
        String[] productNames = productRepository.getAllProductNames();

        // Assert
        for (String productName : productNames) {
            assertThat(productName)
                    .as("Product name '%s' should be lowercase", productName)
                    .isEqualTo(productName.toLowerCase());
        }
    }


}
