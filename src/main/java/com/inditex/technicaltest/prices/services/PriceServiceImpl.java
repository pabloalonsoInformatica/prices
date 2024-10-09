package com.inditex.technicaltest.prices.services;

import com.inditex.technicaltest.prices.controllers.PriceController;
import com.inditex.technicaltest.prices.dtos.PriceDTO;
import com.inditex.technicaltest.prices.models.domain.Price;
import com.inditex.technicaltest.prices.repositories.PriceRepository;
import com.inditex.technicaltest.prices.repositories.PriceSpecifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PriceServiceImpl implements PriceService {
    private static final Logger logger = LoggerFactory.getLogger(PriceController.class);
    private final PriceRepository priceRepository;

    @Autowired
    public PriceServiceImpl(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    @Override
    public Optional<Price> getProductPriceOfBrandOnDate(PriceDTO priceDTO) {
        logger.info("getProductPriceOfBrandOnDate. called");
        return priceRepository.findAll(
                Specification
                        .where(PriceSpecifications.brandIs(priceDTO.getBrandId()))
                        .and(PriceSpecifications.productIs(priceDTO.getProductId()))
                        .and(PriceSpecifications.onDate(priceDTO.getPriceDate())),
                PageRequest.of(
                        0,
                        1,
                        Sort.by(Sort.Direction.DESC, "priority")
                )
        ).stream().findFirst();
    }
}