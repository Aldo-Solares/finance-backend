package com.finance.backend.modules.cardproduct.repository;

import com.finance.backend.modules.cardproduct.model.CardProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardProductRepository
                extends JpaRepository<CardProduct, Long> {
}