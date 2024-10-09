package com.inditex.technicaltest.prices.controllers;

import com.inditex.technicaltest.prices.dtos.PriceDTO;
import com.inditex.technicaltest.prices.dtos.PriceRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.inditex.technicaltest.prices.dtos.PriceResponse;
import com.inditex.technicaltest.prices.services.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prices")
public class PriceController {
    private static final Logger logger = LoggerFactory.getLogger(PriceController.class);
    private final PriceService priceService;

    @Autowired
    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }

    @GetMapping
    public ResponseEntity<PriceResponse> getProductPriceOfBrandOnDated(@Valid @ModelAttribute PriceRequest priceRequest) {
        logger.info("getProductPriceOfBrandOnDate. new get request.");
        logger.debug("getProductPriceOfBrandOnDate. priceRequest: {}", priceRequest);
        PriceResponse response = this.priceService.getProductPriceOfBrandOnDate(
                        PriceDTO.from(priceRequest).build() // Pojo
                )
                .map(p -> PriceResponse.from(p).build()) // mapping to PriceBuilder
                .orElse(null);

        if (response == null) {
            logger.warn("getProductPriceOfBrandOnDate. No price found for the given request. Returning 404.");
            return ResponseEntity.notFound().build(); // 404 Not Found
        }

        logger.debug("getProductPriceOfBrandOnDate. priceResponse: {}", response);
        logger.info("getProductPriceOfBrandOnDate. response OK");
        return ResponseEntity.ok(response);
    }
}
