package com.inditex.technicaltest.prices.controllers;

import com.inditex.technicaltest.prices.dtos.PriceDTO;
import com.inditex.technicaltest.prices.dtos.PriceRequest;
import com.inditex.technicaltest.prices.models.domain.Audit;
import com.inditex.technicaltest.prices.models.domain.Brand;
import com.inditex.technicaltest.prices.models.domain.Price;
import com.inditex.technicaltest.prices.models.domain.Product;
import com.inditex.technicaltest.prices.services.PriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PriceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PriceService priceService;

    @InjectMocks
    private PriceController priceController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(priceController).build();
    }

    @Test
    public void testGetProductPriceOfBrandOnDated_Success() throws Exception {
        // Arrange
        PriceRequest priceRequest = new PriceRequest(1L, 1L, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        Audit audit = new Audit();
        Brand brand = new Brand(1L, "BrandName", new HashSet<>(), audit);
        Product product = new Product(1L, "ProductName", new HashSet<>(), audit);
        Price price = new Price(1L, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS), "EUR", 1, brand, product, BigDecimal.valueOf(100), audit);

        // Simulation of service behavior
        when(priceService.getProductPriceOfBrandOnDate(any(PriceDTO.class))).thenReturn(Optional.of(price));


        System.out.println("{\"priceList\":" + price.getId() + "," +
                "\"productId\":" + price.getProduct().getId() + "," +
                "\"brandId\":" + price.getBrand().getId() + "," +
                "\"startDate\":\"" + price.getStartDate() + "\"," +
                "\"endDate\":\"" + price.getEndDate() + "\"," +
                "\"value\":" + price.getValue() + "}");
        // Act & Assert
        mockMvc.perform(get("/api/prices")
                        .param("productId", String.valueOf(priceRequest.getProductId()))
                        .param("brandId", String.valueOf(priceRequest.getBrandId()))
                        .param("priceDate", priceRequest.getPriceDate().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> {

                    System.out.println(result.getResponse().getContentAsString());
                })
                .andExpect(content().json("{\"priceList\":" + price.getId() + "," +
                        "\"productId\":" + price.getProduct().getId() + "," +
                        "\"brandId\":" + price.getBrand().getId() + "," +
                        "\"startDate\":\"" + price.getStartDate() + "\"," +
                        "\"endDate\":\"" + price.getEndDate() + "\"," +
                        "\"value\":" + price.getValue() + "}"));
    }

    @Test
    public void testGetProductPriceOfBrandOnDated_NotFound() throws Exception {
        // Arrange
        PriceRequest priceRequest = new PriceRequest(1L, 1L, LocalDateTime.now());

        when(priceService.getProductPriceOfBrandOnDate(any(PriceDTO.class))).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/prices")
                        .param("productId", String.valueOf(priceRequest.getProductId()))
                        .param("brandId", String.valueOf(priceRequest.getBrandId()))
                        .param("priceDate", priceRequest.getPriceDate().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
