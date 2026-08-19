package com.finance.backend.modules.debts.cardproduct.repository;

import com.finance.backend.modules.debts.cardproduct.model.CardProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardProductRepository
        extends JpaRepository<CardProduct, Long> {
}