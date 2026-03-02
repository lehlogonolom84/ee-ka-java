package com.shoppingcart.unit;

import com.shoppingcart.implementations.InMemoryCacheImpl;
import com.shoppingcart.implementations.InCodeConfigProviderImpl;
import com.shoppingcart.constant.ProductName;
import com.shoppingcart.models.CartItem;
import com.shoppingcart.models.ProductInfo;
import com.shoppingcart.implementations.ProductCatalogImpl;
import com.shoppingcart.testdata.TestData;
import com.shoppingcart.implementations.ShoppingCartValidatorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ShoppingCartValidatorTest {

    private ShoppingCartValidatorImpl sut;
    private ProductCatalogImpl productRepository;
    private ProductInfo[] knownProducts;

    @BeforeEach
    void setUp() {

        productRepository = new ProductCatalogImpl(new InMemoryCacheImpl(), new InCodeConfigProviderImpl());
        knownProducts = TestData.getKnownProductInfo();
        sut = new ShoppingCartValidatorImpl(productRepository);
    }

    @Test
    void validateAdd_withNullProductName_returnsError() {

        // Arrange
        String productName = null;
        var quantity = 1;

        // Act
        String result = sut.validateAdd(productName, quantity);

        // Assert
        assertThat(result).isEqualTo("Product name cannot be empty.");
    }

    @Test
    void validateAdd_withEmptyProductName_returnsError() {

        // Arrange
        var productName = "";
        var quantity = 1;

        // Act
        String result = sut.validateAdd(productName, quantity);

        // Assert
        assertThat(result).isEqualTo("Product name cannot be empty.");
    }

    @Test
    void validateAdd_withInvalidProductName_returnsError() {

        // Arrange
        var productName = "InvalidProduct";
        var quantity = 1;

        // Act
        String result = sut.validateAdd(productName, quantity);

        // Assert
        assertThat(result).isEqualTo("Product name 'InvalidProduct' is not valid.");
    }

    @Test
    void validateAdd_withZeroQuantity_returnsError() {

        // Arrange
        var productName = knownProducts[0].getTitle();
        var quantity = 0;

        // Act
        String result = sut.validateAdd(productName, quantity);

        // Assert
        assertThat(result).isEqualTo("Quantity must be greater than zero.");
    }

    @Test
    void validateAdd_withNegativeQuantity_returnsError() {

        // Arrange
        var productName = knownProducts[0].getTitle();
        var quantity = -1;

        // Act
        String result = sut.validateAdd(productName, quantity);

        // Assert
        assertThat(result).isEqualTo("Quantity must be greater than zero.");

    }

    @Test
    void validateAdd_withValidProductAndQuantity_returnsEmptyString() {

        // Arrange
        var productName = knownProducts[0].getTitle();
        var quantity = 5;

        // Act
        String result = sut.validateAdd(productName, quantity);

        // Assert
        assertThat(result).isEmpty();

    }

    @Test
    void validateRemoval_withNullProductName_returnsError() {

        // Arrange
        String productName = null;
        var quantity = 1;
        Map<String, CartItem> shoppingCart = new HashMap<>();

        // Act
        String result = sut.validateRemoval(productName, quantity, shoppingCart);

        // Assert
        assertThat(result).isEqualTo("Product name cannot be empty.");
    }

    @Test
    void validateRemoval_withEmptyProductName_returnsError() {

        // Arrange
        var productName = "";
        var quantity = 1;
        Map<String, CartItem> shoppingCart = new HashMap<>();

        // Act
        String result = sut.validateRemoval(productName, quantity, shoppingCart);

        // Assert
        assertThat(result).isEqualTo("Product name cannot be empty.");
    }

    @Test
    void validateRemoval_withInvalidProductName_returnsError() {

        // Arrange
        var productName = "InvalidProduct";
        var quantity = 1;
        Map<String, CartItem> shoppingCart = new HashMap<>();

        // Act
        String result = sut.validateRemoval(productName, quantity, shoppingCart);

        // Assert
        assertThat(result).isEqualTo("Product name 'InvalidProduct' is not valid.");
    }

    @Test
    void validateRemoval_withProductNotInCart_returnsError() {

        // Arrange
        var productName = ProductName.CHEERIOS;
        var quantity = 1;
        Map<String, CartItem> shoppingCart = new HashMap<>();

        // Act
        String result = sut.validateRemoval(productName, quantity, shoppingCart);

        // Assert
        assertThat(result).isEqualTo("Product 'cheerios' is not in the shopping cart.");
    }

    @Test
    void validateRemoval_withZeroQuantity_returnsError() {

        // Arrange
        var productName = ProductName.CHEERIOS;
        var quantity = 0;
        Map<String, CartItem> shoppingCart = createCartWithItem(productName, 5);

        // Act
        String result = sut.validateRemoval(productName, quantity, shoppingCart);

        // Assert
        assertThat(result).isEqualTo("Quantity must be greater than zero.");
    }

    @Test
    void validateRemoval_withNegativeQuantity_returnsError() {

        // Arrange
        var productName = ProductName.CHEERIOS;
        var quantity = -1;
        Map<String, CartItem> shoppingCart = createCartWithItem(productName, 5);

        // Act
        String result = sut.validateRemoval(productName, quantity, shoppingCart);

        // Assert
        assertThat(result).isEqualTo("Quantity must be greater than zero.");
    }

    @Test
    void validateRemoval_withQuantityExceedingCartQuantity_returnsError() {

        // Arrange
        var productName = ProductName.CHEERIOS;
        var quantity = 10;
        Map<String, CartItem> shoppingCart = createCartWithItem(productName, 5);

        // Act
        String result = sut.validateRemoval(productName, quantity, shoppingCart);

        // Assert
        assertThat(result).isEqualTo("Cannot remove 10 of 'cheerios' as only 5 are in the cart.");
    }

    @Test
    void validateRemoval_withValidProductAndQuantity_returnsEmptyString() {

        // Arrange
        var productName = ProductName.CHEERIOS;
        var quantity = 3;
        var shoppingCart = createCartWithItem(productName, 5);

        // Act
        String result = sut.validateRemoval(productName, quantity, shoppingCart);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void validateRemoval_withExactCartQuantity_returnsEmptyString() {

        // Arrange
        var productName = ProductName.CHEERIOS;
        var quantity = 5;
        var shoppingCart = createCartWithItem(productName, 5);

        // Act
        String result = sut.validateRemoval(productName, quantity, shoppingCart);

        // Assert
        assertThat(result).isEmpty();
    }

    private Map<String, CartItem> createCartWithItem(String productName, int quantity) {

        Map<String, CartItem> cart = new HashMap<>();

        var item = new CartItem();

        item.setProductName(productName);
        item.setQuantity(quantity);
        item.setPrice(1.0);
        cart.put(productName, item);

        return cart;
    }
}
