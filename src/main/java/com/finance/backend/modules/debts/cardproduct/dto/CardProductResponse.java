package com.finance.backend.modules.debts.cardproduct.dto;

public record CardProductResponse(
                Long productId,
                String bank,
                String cardName) {
}