package com.shoppingcart.unit.validator;

import com.shoppingcart.caching.InMemoryCache;
import com.shoppingcart.configuration.InCodeConfigProvider;
import com.shoppingcart.constant.ProductName;
import com.shoppingcart.valueobjects.CartItem;
import com.shoppingcart.valueobjects.ProductInfo;
import com.shoppingcart.repositories.ProductRepositoryImpl;
import com.shoppingcart.testdata.TestData;
import com.shoppingcart.validator.ProductValidatorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ProductValidatorTest {

    private ProductValidatorImpl sut;
    private ProductRepositoryImpl productRepository;
    private ProductInfo[] knownProducts;

    @BeforeEach
    void setUp() {

        productRepository = new ProductRepositoryImpl(new InMemoryCache(), new InCodeConfigProvider());
        knownProducts = TestData.getKnownProductInfo();
        sut = new ProductValidatorImpl(productRepository);
    }

    @Test
    void validateAdd_withNullProductName_returnsError() {

        // Arrange
        String productName = null;
        int quantity = 1;

        // Act
        String result = sut.validateAdd(productName, quantity);

        // Assert
        assertThat(result).isEqualTo("Product name cannot be empty.");
    }

    @Test
    void validateAdd_withEmptyProductName_returnsError() {

        // Arrange
        String productName = "";
        int quantity = 1;

        // Act
        String result = sut.validateAdd(productName, quantity);

        // Assert
        assertThat(result).isEqualTo("Product name cannot be empty.");
    }

    @Test
    void validateAdd_withInvalidProductName_returnsError() {

        // Arrange
        String productName = "InvalidProduct";
        int quantity = 1;

        // Act
        String result = sut.validateAdd(productName, quantity);

        // Assert
        assertThat(result).isEqualTo("Product name 'InvalidProduct' is not valid.");
    }

    @Test
    void validateAdd_withZeroQuantity_returnsError() {

        // Arrange
        String productName = knownProducts[0].getTitle();
        int quantity = 0;

        // Act
        String result = sut.validateAdd(productName, quantity);

        // Assert
        assertThat(result).isEqualTo("Quantity must be greater than zero.");
    }

    @Test
    void validateAdd_withNegativeQuantity_returnsError() {

        // Arrange
        String productName = knownProducts[0].getTitle();
        int quantity = -1;

        // Act
        String result = sut.validateAdd(productName, quantity);

        // Assert
        assertThat(result).isEqualTo("Quantity must be greater than zero.");

    }

    @Test
    void validateAdd_withValidProductAndQuantity_returnsEmptyString() {

        // Arrange
        String productName = knownProducts[0].getTitle();
        int quantity = 5;

        // Act
        String result = sut.validateAdd(productName, quantity);

        // Assert
        assertThat(result).isEmpty();

    }

    @Test
    void validateRemoval_withNullProductName_returnsError() {

        // Arrange
        String productName = null;
        int quantity = 1;
        Map<String, CartItem> shoppingCart = new HashMap<>();

        // Act
        String result = sut.validateRemoval(productName, quantity, shoppingCart);

        // Assert
        assertThat(result).isEqualTo("Product name cannot be empty.");
    }

    @Test
    void validateRemoval_withEmptyProductName_returnsError() {

        // Arrange
        String productName = "";
        int quantity = 1;
        Map<String, CartItem> shoppingCart = new HashMap<>();

        // Act
        String result = sut.validateRemoval(productName, quantity, shoppingCart);

        // Assert
        assertThat(result).isEqualTo("Product name cannot be empty.");
    }

    @Test
    void validateRemoval_withInvalidProductName_returnsError() {

        // Arrange
        String productName = "InvalidProduct";
        int quantity = 1;
        Map<String, CartItem> shoppingCart = new HashMap<>();

        // Act
        String result = sut.validateRemoval(productName, quantity, shoppingCart);

        // Assert
        assertThat(result).isEqualTo("Product name 'InvalidProduct' is not valid.");
    }

    @Test
    void validateRemoval_withProductNotInCart_returnsError() {

        // Arrange
        String productName = ProductName.CHEERIOS;
        int quantity = 1;
        Map<String, CartItem> shoppingCart = new HashMap<>();

        // Act
        String result = sut.validateRemoval(productName, quantity, shoppingCart);

        // Assert
        assertThat(result).isEqualTo("Product 'cheerios' is not in the shopping cart.");
    }

    @Test
    void validateRemoval_withZeroQuantity_returnsError() {

        // Arrange
        String productName = ProductName.CHEERIOS;
        int quantity = 0;
        Map<String, CartItem> shoppingCart = createCartWithItem(productName, 5);

        // Act
        String result = sut.validateRemoval(productName, quantity, shoppingCart);

        // Assert
        assertThat(result).isEqualTo("Quantity must be greater than zero.");
    }

    @Test
    void validateRemoval_withNegativeQuantity_returnsError() {

        // Arrange
        String productName = ProductName.CHEERIOS;
        int quantity = -1;
        Map<String, CartItem> shoppingCart = createCartWithItem(productName, 5);

        // Act
        String result = sut.validateRemoval(productName, quantity, shoppingCart);

        // Assert
        assertThat(result).isEqualTo("Quantity must be greater than zero.");
    }

    @Test
    void validateRemoval_withQuantityExceedingCartQuantity_returnsError() {

        // Arrange
        String productName = ProductName.CHEERIOS;
        int quantity = 10;
        Map<String, CartItem> shoppingCart = createCartWithItem(productName, 5);

        // Act
        String result = sut.validateRemoval(productName, quantity, shoppingCart);

        // Assert
        assertThat(result).isEqualTo("Cannot remove 10 of 'cheerios' as only 5 are in the cart.");
    }

    @Test
    void validateRemoval_withValidProductAndQuantity_returnsEmptyString() {

        // Arrange
        String productName = ProductName.CHEERIOS;
        int quantity = 3;
        Map<String, CartItem> shoppingCart = createCartWithItem(productName, 5);

        // Act
        String result = sut.validateRemoval(productName, quantity, shoppingCart);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void validateRemoval_withExactCartQuantity_returnsEmptyString() {

        // Arrange
        String productName = ProductName.CHEERIOS;
        int quantity = 5;
        Map<String, CartItem> shoppingCart = createCartWithItem(productName, 5);

        // Act
        String result = sut.validateRemoval(productName, quantity, shoppingCart);

        // Assert
        assertThat(result).isEmpty();
    }

    private Map<String, CartItem> createCartWithItem(String productName, int quantity) {
        Map<String, CartItem> cart = new HashMap<>();
        CartItem item = new CartItem();
        item.setProductName(productName);
        item.setQuantity(quantity);
        item.setPrice(1.0);
        cart.put(productName, item);
        return cart;
    }
}
