package com.finance.backend.modules.debts.card.dto;

public record CardResponse(
                Long cardId,
                String bank,
                String cardName,
                Boolean active) {
}