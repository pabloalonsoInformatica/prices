package com.inditex.technicaltest.prices.models.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "PRICES")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Price {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="price_list")
    private Long id;

    @Column(nullable = false, name = "start_date")
    private LocalDateTime startDate; // Mapping to TIMESTAMP

    @Column(nullable = false, name = "end_date")
    private LocalDateTime endDate; // Mapping to TIMESTAMP

    @Column(length = 3, nullable = false)
    private String curr; // Mapping to VARCHAR(3)

    @Column(nullable = false)
    private int priority; // int never be null

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name ="price", precision = 15, scale = 2)
    private BigDecimal value;

    @Embedded
    private Audit audit = new Audit();


}
