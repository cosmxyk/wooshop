package com.wooshop.product.controller;

import com.wooshop.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Long> register (@RequestBody ProductRequest.Register request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.register(
                request.getProductCode(),
                request.getName(),
                request.getPrice(),
                request.getStockQuantity()
        ));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse.Detail> getProduct (@PathVariable Long productId) {
        return ResponseEntity.ok().body(productService.getProduct(productId));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse.Detail>> getProducts () {
        return ResponseEntity.ok().body(productService.getProducts());
    }
}
