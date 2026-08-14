package com.finance.backend.modules.cardproduct.dto;

public record CardProductResponse(
        Long productId,
        String bank,
        String cardName) {
}