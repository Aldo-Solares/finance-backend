package com.finance.backend.modules.debts.card.dto;

public record CardResponse(
        Long cardId,
        String cardCode,
        Boolean active,
        Long productId,
        String bank,
        String cardName,
        Long userId,
        String userName

) {
}