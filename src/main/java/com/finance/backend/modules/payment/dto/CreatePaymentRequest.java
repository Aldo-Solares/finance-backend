package com.finance.backend.modules.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreatePaymentRequest(

        @NotNull Long statementId,

        @NotNull Long userId,

        @NotNull @Positive BigDecimal amount,

        @NotBlank String paymentType) {
}