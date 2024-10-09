package com.inditex.technicaltest.prices.dtos;


import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PriceDTO {
    private Long productId;
    private Long brandId;
    private LocalDateTime priceDate; // ISO-8601

    public static PriceDTO.PriceDTOBuilder from(PriceRequest priceRequest) {
        return PriceDTO.builder()
                .productId(priceRequest.getProductId())
                .brandId(priceRequest.getBrandId())
                .priceDate(priceRequest.getPriceDate());
    }
}
