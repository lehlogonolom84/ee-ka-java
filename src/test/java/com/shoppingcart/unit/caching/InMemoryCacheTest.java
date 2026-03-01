package com.shoppingcart.unit.caching;

import com.shoppingcart.caching.InMemoryCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryCacheTest {

    private InMemoryCache cache;

    @BeforeEach
    void setUp() {
        cache = new InMemoryCache();
    }

    @Test
    void set_withValidKeyAndValue_storesValue() {
        // Arrange
        String key = "key";
        String value = "value";
        Duration expiration = Duration.ofMinutes(5);

        // Act
        cache.set(key, value, expiration);

        // Assert
        assertThat(cache.get(key, String.class)).isEqualTo(value);
    }

    @Test
    void set_withNullKey_throwsIllegalArgumentException() {

        // Arrange
        String key = null;
        String value = "value";
        Duration expiration = Duration.ofMinutes(5);

        // Act & Assert
        assertThatThrownBy(() -> cache.set(key, value, expiration))
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
        assertThatThrownBy(() -> cache.set(key, value, expiration))
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
        assertThatThrownBy(() -> cache.set(key, value, expiration))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cache key cannot be null or empty.");
    }

    @Test
    void get_withNullKey_returnsNull() {

        // Arrange
        String key = null;

        // Act
        String result = cache.get(key, String.class);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void get_withEmptyKey_returnsNull() {

        // Arrange
        String key = "";

        // Act
        String result = cache.get(key, String.class);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void get_withBlankKey_returnsNull() {

        // Arrange
        String key = "   ";

        // Act
        String result = cache.get(key, String.class);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void get_withNonExistentKey_returnsNull() {

        // Arrange
        String key = "nonexistent";

        // Act
        String result = cache.get(key, String.class);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void get_withExpiredEntry_returnsNullAndRemovesEntry() throws InterruptedException {

        // Arrange
        String key = "key";
        String value = "value";
        Duration expiration = Duration.ofMillis(50);
        cache.set(key, value, expiration);

        // Act
        Thread.sleep(100);
        String result = cache.get(key, String.class);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void get_withValidEntry_returnsValue() {

        // Arrange
        String key = "key";
        Integer value = 42;
        Duration expiration = Duration.ofMinutes(5);
        cache.set(key, value, expiration);

        // Act
        Integer result = cache.get(key, Integer.class);

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
        cache.set(key, originalValue, expiration);

        // Act
        cache.set(key, updatedValue, expiration);

        // Assert
        assertThat(cache.get(key, String.class)).isEqualTo(updatedValue);
    }

    @Test
    void get_withDifferentTypes_returnsCorrectValue() {

        // Arrange
        cache.set("string", "hello", Duration.ofMinutes(5));
        cache.set("integer", 123, Duration.ofMinutes(5));
        cache.set("double", 45.67, Duration.ofMinutes(5));

        // Act
        String stringResult = cache.get("string", String.class);
        Integer integerResult = cache.get("integer", Integer.class);
        Double doubleResult = cache.get("double", Double.class);

        // Assert
        assertThat(stringResult).isEqualTo("hello");
        assertThat(integerResult).isEqualTo(123);
        assertThat(doubleResult).isEqualTo(45.67);
    }
}
