package com.wooshop.product.service;

import com.wooshop.common.exception.DuplicateProductCodeException;
import com.wooshop.common.exception.ProductNotFoundException;
import com.wooshop.inventory.domain.Inventory;
import com.wooshop.inventory.repository.InventoryRepository;
import com.wooshop.product.domain.Product;
import com.wooshop.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    // 상품 등록 (재고도 함께 생성)
    @Transactional
    public Long register(String productCode, String name, BigDecimal price, int stockQuantity) {
        // 1. 상품코드 중복 체크
        if (productRepository.findByProductCode(productCode).isPresent()) {
            throw new DuplicateProductCodeException(productCode);
        }

        // 2. Product 저장
        Product product = productRepository.save(Product.builder()
                .productCode(productCode)
                .name(name)
                .price(price)
                .build());

        // 3. Inventory 저장
        inventoryRepository.save(Inventory.builder()
                .product(product)
                .stockQuantity(stockQuantity)
                .build());

        // 4. productId 반환
        return product.getProductId();
    }

    // 상품 단건 조회
    @Transactional(readOnly = true)
    public Product getProduct(Long productId) {
        // productId로 조회, 없으면 예외
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    // 상품 목록 조회
    @Transactional(readOnly = true)
    public List<Product> getProducts() {
        // 전체 조회
        return productRepository.findAll();
    }
}
