package com.inditex.technicaltest.prices.repositories;

import com.inditex.technicaltest.prices.models.domain.Price;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class PriceSpecifications {

    public static Specification<Price> onDate (LocalDateTime applicationDate){
        return (root, query, criteriaBuilder) -> {
            if (applicationDate == null) {
                return criteriaBuilder.conjunction(); // no filter applies
            }
            return criteriaBuilder.between(criteriaBuilder.literal(applicationDate), root.get("startDate"), root.get("endDate"));
        };
    }

    public static Specification<Price> brandIs(Long brand) {
        return (root, query, criteriaBuilder) -> {
            if (brand == null) {
                return criteriaBuilder.conjunction(); //  no filter applies
            }
            return criteriaBuilder.equal(root.get("brand").get("id"), brand);
        };
    }

    public static Specification<Price> productIs(Long product) {
        return (root, query, criteriaBuilder) -> {
            if (product == null) {
                return criteriaBuilder.conjunction(); //  no filter applies
            }
            return criteriaBuilder.equal(root.get("product").get("id"), product);
        };
    }
}