package com.inditex.technicaltest.prices.services;

import com.inditex.technicaltest.prices.dtos.PriceDTO;
import com.inditex.technicaltest.prices.models.domain.Price;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface PriceService {
    Optional<Price> getProductPriceOfBrandOnDate(PriceDTO priceDTO);
}
