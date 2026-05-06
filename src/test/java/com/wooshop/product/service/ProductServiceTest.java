package com.wooshop.product.service;

import com.wooshop.common.exception.DuplicateProductCodeException;
import com.wooshop.common.exception.ProductNotFoundException;
import com.wooshop.inventory.domain.Inventory;
import com.wooshop.inventory.repository.InventoryRepository;
import com.wooshop.product.controller.ProductResponse;
import com.wooshop.product.domain.Product;
import com.wooshop.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("상품코드 중복 시 예외 발생")
    void register_duplicateProductCode_throwsException() {
        // given
        given(productRepository.findByProductCode("testCode"))
                .willReturn(Optional.of(mock(Product.class)));

        // when & then
        assertThatThrownBy(() -> productService.register("testCode", "테스트상품", BigDecimal.valueOf(12000), 100))
                .isInstanceOf(DuplicateProductCodeException.class);
    }

    @Test
    @DisplayName("정상 등록 시 상품 ID 반환")
    void register_success_returnsProductId() {
        // given 1: 상품코드 중복 없음
        given(productRepository.findByProductCode("testCode"))
                .willReturn(Optional.empty());

        // given 2: save() 호출 시 productId = 1L인 Product 반환
        Product savedProduct = Product.builder()
                .productId(1L)
                .productCode("testCode")
                .name("테스트상품")
                .price(BigDecimal.valueOf(12000))
                .build();

        given(productRepository.save(any(Product.class)))
                .willReturn(savedProduct);

        // when
        Long productId = productService.register("testCode", "테스트상품", BigDecimal.valueOf(12000), 100);

        // then
        assertThat(productId).isEqualTo(1L);
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    @DisplayName("존재하는 상품 조회 성공")
    void getProduct_success_returnsProduct() {
        // given
        Product savedProduct = Product.builder()
                .productId(1L)
                .productCode("testCode")
                .name("테스트상품")
                .price(BigDecimal.valueOf(12000))
                .build();

        given(productRepository.findById(1L))
                .willReturn(Optional.of(savedProduct));

        // when
        ProductResponse.Detail product = productService.getProduct(1L);

        // then
        assertThat(product.getProductId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("존재하지 않는 상품 조회 시 예외 발생")
    void getProduct_notFound_throwsException() {
        // given
        given(productRepository.findById(1L))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productService.getProduct(1L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    @DisplayName("상품 목록 반환")
    void getProducts_returnsProductList() {
        // given
        given(productRepository.findAll())
                .willReturn(List.of(
                        Product.builder().productId(1L).productCode("A").name("상품A").price(BigDecimal.valueOf(1000)).build(),
                        Product.builder().productId(2L).productCode("B").name("상품B").price(BigDecimal.valueOf(2000)).build()
                ));

        // when
        List<ProductResponse.Detail> list = productService.getProducts();

        // then
        assertThat(list).hasSize(2);
    }
}
