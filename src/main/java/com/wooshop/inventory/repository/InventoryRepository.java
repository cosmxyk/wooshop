package com.wooshop.inventory.repository;

import com.wooshop.inventory.domain.Inventory;
import com.wooshop.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProduct(Product product);
}
