package com.finance.backend.modules.payment.dto;

import java.math.BigDecimal;

public record PaymentResponse(
        Long paymentId,

        Long statementId,

        Long userId,
        String userName,

        BigDecimal amount,
        String paymentType) {
}