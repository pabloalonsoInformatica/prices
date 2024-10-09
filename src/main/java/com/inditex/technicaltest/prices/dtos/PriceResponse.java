package com.inditex.technicaltest.prices.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.inditex.technicaltest.prices.models.domain.Price;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PriceResponse {
    private Long priceList;
    private Long productId;
    private Long brandId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;
    private BigDecimal value;

    public static PriceResponseBuilder from (Price price) {
        return PriceResponse.builder()
                .priceList(price.getId())
                .productId(price.getProduct().getId())
                .brandId(price.getBrand().getId())
                .startDate(price.getStartDate())
                .endDate(price.getEndDate())
                .value(price.getValue());

    }
}
