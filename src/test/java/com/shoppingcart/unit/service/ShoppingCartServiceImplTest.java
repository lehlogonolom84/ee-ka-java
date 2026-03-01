package com.shoppingcart.unit.service;

import com.shoppingcart.caching.InMemoryCache;
import com.shoppingcart.configuration.InCodeConfigProvider;
import com.shoppingcart.constant.DecimalPlaces;
import com.shoppingcart.constant.ProductName;
import com.shoppingcart.interfaces.caching.Cache;
import com.shoppingcart.interfaces.configuration.ConfigProvider;
import com.shoppingcart.interfaces.repository.ProductRepository;
import com.shoppingcart.interfaces.validator.ProductValidator;
import com.shoppingcart.model.Cart;
import com.shoppingcart.model.CartActionResult;
import com.shoppingcart.model.CartItem;
import com.shoppingcart.repository.ProductRepositoryImpl;
import com.shoppingcart.service.ShoppingCartServiceImpl;
import com.shoppingcart.validator.ProductValidatorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class ShoppingCartServiceImplTest {

    private ShoppingCartServiceImpl sut;

    @BeforeEach
    void setUp() {

        var configProvider = new InCodeConfigProvider();
        var productRepository = new ProductRepositoryImpl(new InMemoryCache(), configProvider);
        var productValidator = new ProductValidatorImpl(productRepository);
        sut = new ShoppingCartServiceImpl(configProvider,productRepository,productValidator);
    }

    @Test
    void addItem_withValidProductsAndQuantities_returnsCartDetails() {

        // Arrange
        String productOne = ProductName.CORNFLAKES;
        int productOneQuantity = 1;
        int productOneQuantityOne = 1;
        String productTwo = ProductName.WEETABIX;
        int productTwoQuantity = 1;
        int expectedCartItemsCount = 2;

        int expectedProductOneQuantity = productOneQuantity + productOneQuantityOne;
        int expectedProductTwoQuantity = productTwoQuantity;

        double expectedTax = 1.88;
        double expectedSubTotal = 15.02;

        // Act
        CartActionResult firstAddResult = sut.addItem(productOne, productOneQuantity);
        CartActionResult secondAddResult = sut.addItem(productOne, productOneQuantityOne);
        CartActionResult thirdAddResult = sut.addItem(productTwo, productTwoQuantity);
        Cart shoppingCart = sut.getCart();

        // Assert
        // Check if all add operations were successful
        assertTrue(firstAddResult.isSuccess());
        assertTrue(secondAddResult.isSuccess());
        assertTrue(thirdAddResult.isSuccess());

        // Check if the shopping cart details are correct
        assertNotNull(shoppingCart);
        assertEquals(expectedCartItemsCount, shoppingCart.getItems().length);
        assertEquals(expectedProductOneQuantity, findItemByProductName(shoppingCart, productOne).getQuantity());
        assertEquals(expectedProductTwoQuantity, findItemByProductName(shoppingCart, productTwo).getQuantity());
        assertEquals(expectedSubTotal, shoppingCart.getSubTotal());
        assertEquals(expectedTax, shoppingCart.getTax());
        assertEquals(BigDecimal.valueOf(expectedSubTotal + expectedTax)
                        .setScale(DecimalPlaces.TWO, RoundingMode.HALF_UP)
                        .doubleValue(),shoppingCart.getTotal()
        );
    }

    @Test
    void removeItem_withNonExistingProduct_returnsErrorMessage() {

        // Arrange
        String productOne = ProductName.CORNFLAKES + "nothere";

        // Act
        CartActionResult removeResult = sut.removeItem(productOne, 1);

        // Assert
        assertFalse(removeResult.isSuccess());
        assertNotNull(removeResult);
        assertEquals("Product name '" + productOne + "' is not valid.", removeResult.getMessage());
    }

    @Test
    void removeItem_withExistingProduct_reducesQuantity() {

        // Arrange
        String productOne = ProductName.CORNFLAKES;
        int productOneQuantity = 2;
        int expectedProductOneQuantity = 1;

        // Act
        CartActionResult addResult = sut.addItem(productOne, productOneQuantity);
        CartActionResult removeResult = sut.removeItem(productOne, 1);
        Cart shoppingCart = sut.getCart();

        // Assert
        assertTrue(addResult.isSuccess());
        assertTrue(removeResult.isSuccess());
        assertNotNull(shoppingCart);
        assertEquals(expectedProductOneQuantity, findItemByProductName(shoppingCart, productOne).getQuantity());
    }

    @Test
    void removeItem_withEntireQuantity_removesItemFromCart() {

        // Arrange
        String productOne = ProductName.CORNFLAKES;
        int productOneQuantity = 1;
        int removeQuantity = 1;
        int expectedCartItemsCount = 0;

        // Act
        CartActionResult addResult = sut.addItem(productOne, productOneQuantity);
        CartActionResult removeResult = sut.removeItem(productOne, removeQuantity);
        Cart shoppingCart = sut.getCart();

        // Assert
        assertTrue(addResult.isSuccess());
        assertTrue(removeResult.isSuccess());
        assertNotNull(shoppingCart);
        assertEquals(expectedCartItemsCount, shoppingCart.getItems().length);
        assertNull(findItemByProductName(shoppingCart, productOne));
    }

    @Test
    void addItem_withInvalidProduct_returnsErrorMessage() {

        // Arrange
        String invalidProductName = "InvalidProduct";
        int quantity = 1;

        // Act
        CartActionResult addResult = sut.addItem(invalidProductName, quantity);

        // Assert
        assertFalse(addResult.isSuccess());
        assertNotNull(addResult);
        assertEquals("Product name '" + invalidProductName + "' is not valid.", addResult.getMessage());
    }

    private CartItem findItemByProductName(Cart cart, String productName) {

        return Arrays.stream(cart.getItems())
                .filter(item -> item.getProductName().equals(productName))
                .findFirst()
                .orElse(null);
    }
}
