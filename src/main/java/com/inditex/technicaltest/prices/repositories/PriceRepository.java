package com.inditex.technicaltest.prices.repositories;

import com.inditex.technicaltest.prices.models.domain.Price;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceRepository extends JpaRepository<Price, Long>, JpaSpecificationExecutor<Price> {
}
