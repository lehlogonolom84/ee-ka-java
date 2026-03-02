package com.shoppingcart.unit;

import com.shoppingcart.implementations.InMemoryCacheImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryCacheImplTest {

    private InMemoryCacheImpl sut;

    @BeforeEach
    void setUp() {
        sut = new InMemoryCacheImpl();
    }

    @Test
    void set_withValidKeyAndValue_storesValue() {
        // Arrange
        String key = "key";
        String value = "value";
        Duration expiration = Duration.ofMinutes(5);

        // Act
        sut.set(key, value, expiration);

        // Assert
        assertThat(sut.get(key, String.class)).isEqualTo(value);
    }

    @Test
    void set_withNullKey_throwsIllegalArgumentException() {

        // Arrange
        String key = null;
        String value = "value";
        Duration expiration = Duration.ofMinutes(5);

        // Act & Assert
        assertThatThrownBy(() -> sut.set(key, value, expiration))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cache key cannot be null or empty.");
    }

    @Test
    void set_withEmptyKey_throwsIllegalArgumentException() {

        // Arrange
        String key = "";
        String value = "value";
        Duration expiration = Duration.ofMinutes(5);

        // Act & Assert
        assertThatThrownBy(() -> sut.set(key, value, expiration))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cache key cannot be null or empty.");
    }

    @Test
    void set_withBlankKey_throwsIllegalArgumentException() {

        // Arrange
        String key = "   ";
        String value = "value";
        Duration expiration = Duration.ofMinutes(5);

        // Act & Assert
        assertThatThrownBy(() -> sut.set(key, value, expiration))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cache key cannot be null or empty.");
    }

    @Test
    void get_withNullKey_returnsNull() {

        // Arrange
        String key = null;

        // Act
        String result = sut.get(key, String.class);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void get_withEmptyKey_returnsNull() {

        // Arrange
        String key = "";

        // Act
        String result = sut.get(key, String.class);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void get_withBlankKey_returnsNull() {

        // Arrange
        String key = "   ";

        // Act
        String result = sut.get(key, String.class);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void get_withNonExistentKey_returnsNull() {

        // Arrange
        String key = "nonexistent";

        // Act
        String result = sut.get(key, String.class);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void get_withExpiredEntry_returnsNullAndRemovesEntry() throws InterruptedException {

        // Arrange
        String key = "key";
        String value = "value";
        Duration expiration = Duration.ofMillis(50);
        sut.set(key, value, expiration);

        // Act
        Thread.sleep(100);
        String result = sut.get(key, String.class);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void get_withValidEntry_returnsValue() {

        // Arrange
        String key = "key";
        Integer value = 42;
        Duration expiration = Duration.ofMinutes(5);
        sut.set(key, value, expiration);

        // Act
        Integer result = sut.get(key, Integer.class);

        // Assert
        assertThat(result).isEqualTo(value);
    }

    @Test
    void set_overwritesExistingEntry() {

        // Arrange
        String key = "key";
        String originalValue = "original";
        String updatedValue = "updated";
        Duration expiration = Duration.ofMinutes(5);
        sut.set(key, originalValue, expiration);

        // Act
        sut.set(key, updatedValue, expiration);

        // Assert
        assertThat(sut.get(key, String.class)).isEqualTo(updatedValue);
    }

    @Test
    void get_withDifferentTypes_returnsCorrectValue() {

        // Arrange
        sut.set("string", "hello", Duration.ofMinutes(5));
        sut.set("integer", 123, Duration.ofMinutes(5));
        sut.set("double", 45.67, Duration.ofMinutes(5));

        // Act
        String stringResult = sut.get("string", String.class);
        Integer integerResult = sut.get("integer", Integer.class);
        Double doubleResult = sut.get("double", Double.class);

        // Assert
        assertThat(stringResult).isEqualTo("hello");
        assertThat(integerResult).isEqualTo(123);
        assertThat(doubleResult).isEqualTo(45.67);
    }
}
