package com.shoppingcart.unit.service;

import com.shoppingcart.caching.InMemoryCache;
import com.shoppingcart.configuration.InCodeConfigProvider;
import com.shoppingcart.constant.DecimalPlaces;
import com.shoppingcart.constant.ProductName;
import com.shoppingcart.valueobjects.Cart;
import com.shoppingcart.valueobjects.CartActionResult;
import com.shoppingcart.valueobjects.CartItem;
import com.shoppingcart.repositories.ProductRepositoryImpl;
import com.shoppingcart.services.ShoppingCartServiceImpl;
import com.shoppingcart.validator.ProductValidatorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class ShoppingCartServiceImplTest {

    private static final String TEST_CART_ID = "test-user-123";

    private ShoppingCartServiceImpl sut;

    @BeforeEach
    void setUp() {
        var configProvider = new InCodeConfigProvider();
        var productRepository = new ProductRepositoryImpl(new InMemoryCache(), configProvider);
        var productValidator = new ProductValidatorImpl(productRepository);
        sut = new ShoppingCartServiceImpl(configProvider, productRepository, productValidator);
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
        CartActionResult firstAddResult = sut.addItem(TEST_CART_ID, productOne, productOneQuantity);
        CartActionResult secondAddResult = sut.addItem(TEST_CART_ID, productOne, productOneQuantityOne);
        CartActionResult thirdAddResult = sut.addItem(TEST_CART_ID, productTwo, productTwoQuantity);
        Cart shoppingCart = sut.getCart(TEST_CART_ID);

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
        CartActionResult removeResult = sut.removeItem(TEST_CART_ID, productOne, 1);

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
        CartActionResult addResult = sut.addItem(TEST_CART_ID, productOne, productOneQuantity);
        CartActionResult removeResult = sut.removeItem(TEST_CART_ID, productOne, 1);
        Cart shoppingCart = sut.getCart(TEST_CART_ID);

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
        CartActionResult addResult = sut.addItem(TEST_CART_ID, productOne, productOneQuantity);
        CartActionResult removeResult = sut.removeItem(TEST_CART_ID, productOne, removeQuantity);
        Cart shoppingCart = sut.getCart(TEST_CART_ID);

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
        CartActionResult addResult = sut.addItem(TEST_CART_ID, invalidProductName, quantity);

        // Assert
        assertFalse(addResult.isSuccess());
        assertNotNull(addResult);
        assertEquals("Product name '" + invalidProductName + "' is not valid.", addResult.getMessage());
    }

    @Test
    void carts_withDifferentCartIds_areIsolated() {
        // Arrange
        String userOneCartId = "user-1";
        String userTwoCartId = "user-2";
        String sessionCartId = "session-abc123";

        // Act - add different products to each cart
        sut.addItem(userOneCartId, ProductName.CORNFLAKES, 2);
        sut.addItem(userTwoCartId, ProductName.WEETABIX, 3);
        sut.addItem(sessionCartId, ProductName.CHEERIOS, 1);

        Cart userOneCart = sut.getCart(userOneCartId);
        Cart userTwoCart = sut.getCart(userTwoCartId);
        Cart sessionCart = sut.getCart(sessionCartId);

        // Assert - each cart should only contain its own items
        assertEquals(1, userOneCart.getItems().length);
        assertEquals(ProductName.CORNFLAKES, userOneCart.getItems()[0].getProductName());
        assertEquals(2, userOneCart.getItems()[0].getQuantity());
        assertEquals(userOneCartId, userOneCart.getCartId());

        assertEquals(1, userTwoCart.getItems().length);
        assertEquals(ProductName.WEETABIX, userTwoCart.getItems()[0].getProductName());
        assertEquals(3, userTwoCart.getItems()[0].getQuantity());
        assertEquals(userTwoCartId, userTwoCart.getCartId());

        assertEquals(1, sessionCart.getItems().length);
        assertEquals(ProductName.CHEERIOS, sessionCart.getItems()[0].getProductName());
        assertEquals(1, sessionCart.getItems()[0].getQuantity());
        assertEquals(sessionCartId, sessionCart.getCartId());
    }

    @Test
    void getCart_returnsCartIdInResponse() {
        // Arrange
        String cartId = "my-unique-cart-id";
        sut.addItem(cartId, ProductName.CORNFLAKES, 1);

        // Act
        Cart cart = sut.getCart(cartId);

        // Assert
        assertEquals(cartId, cart.getCartId());
    }

    private CartItem findItemByProductName(Cart cart, String productName) {
        return Arrays.stream(cart.getItems())
                .filter(item -> item.getProductName().equals(productName))
                .findFirst()
                .orElse(null);
    }
}
