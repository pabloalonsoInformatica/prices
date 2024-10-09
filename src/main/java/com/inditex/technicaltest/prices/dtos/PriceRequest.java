package com.inditex.technicaltest.prices.dtos;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PriceRequest {
    @NotNull(message = "product id cannot be void")
    @Positive(message = "product id must be positive")
    private Long productId;

    @NotNull(message = "brand id cannot be void")
    @Positive(message = "brand id must be positive")
    private Long brandId;

    @NotNull(message = "the application date cannot be void")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime priceDate; // ISO-8601
}
