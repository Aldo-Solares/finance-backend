package com.finance.backend.modules.debts.usercard.dto;

public record UserCardResponse(
        Long userCardId,
        Long userId,
        Long cardId,
        String bank,
        String cardName,
        Boolean active) {
}