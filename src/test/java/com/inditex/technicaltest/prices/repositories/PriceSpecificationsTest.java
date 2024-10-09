package com.inditex.technicaltest.prices.repositories;

import com.inditex.technicaltest.prices.models.domain.Price;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PriceSpecificationsTest {

    @Test
    public void testOnDate() {
        LocalDateTime applicationDate = LocalDateTime.now();
        Specification<Price> spec = PriceSpecifications.onDate(applicationDate);

        assertNotNull(spec);
    }

    @Test
    public void testBrandIs() {
        Long brandId = 1L;
        Specification<Price> spec = PriceSpecifications.brandIs(brandId);

        assertNotNull(spec);
    }

    @Test
    public void testProductIs() {
        Long productId = 1L;
        Specification<Price> spec = PriceSpecifications.productIs(productId);

        assertNotNull(spec);
    }
}
