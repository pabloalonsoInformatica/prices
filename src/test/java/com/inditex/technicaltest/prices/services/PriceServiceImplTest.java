package com.inditex.technicaltest.prices.services;

import com.inditex.technicaltest.prices.dtos.PriceDTO;
import com.inditex.technicaltest.prices.models.domain.Price;
import com.inditex.technicaltest.prices.repositories.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class PriceServiceImplTest {

    @Mock
    private PriceRepository priceRepository;

    @InjectMocks
    private PriceServiceImpl priceService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetProductPriceOfBrandOnDate_Found() {
        // Arrange
        PriceDTO priceDTO = new PriceDTO(1L, 1L, LocalDateTime.now());
        Price expectedPrice = new Price(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), "EUR", 1, null, null, BigDecimal.valueOf(100), null);
        PageImpl<Price> pricePage = new PageImpl<>(Collections.singletonList(expectedPrice), PageRequest.of(0, 1), 1);

        when(priceRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(pricePage);

        // Act
        Optional<Price> result = priceService.getProductPriceOfBrandOnDate(priceDTO);

        // Assert
        assertEquals(expectedPrice, result.orElse(null));
    }

    @Test
    public void testGetProductPriceOfBrandOnDate_NotFound() {
        // Arrange
        PriceDTO priceDTO = new PriceDTO(1L, 1L, LocalDateTime.now());

        when(priceRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

        // Act
        Optional<Price> result = priceService.getProductPriceOfBrandOnDate(priceDTO);

        // Assert
        assertEquals(Optional.empty(), result);
    }
}
