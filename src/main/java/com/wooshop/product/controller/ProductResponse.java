package com.wooshop.product.controller;

import com.wooshop.product.domain.Product;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

public class ProductResponse {

    @Getter
    @Builder
    public static class Detail {
        private Long productId;
        private String productCode;
        private String name;
        private BigDecimal price;

        public static Detail from(Product product) {
            // Product → DTO 변환
            return Detail.builder()
                    .productId(product.getProductId())
                    .productCode(product.getProductCode())
                    .name(product.getName())
                    .price(product.getPrice())
                    .build();
        }
    }
}
