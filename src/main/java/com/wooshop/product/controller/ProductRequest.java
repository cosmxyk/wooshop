package com.wooshop.product.controller;

import lombok.Getter;

import java.math.BigDecimal;

public class ProductRequest {

    @Getter
    public static class Register {
        private String productCode;
        private String name;
        private BigDecimal price;
        private int stockQuantity;
    }
}
