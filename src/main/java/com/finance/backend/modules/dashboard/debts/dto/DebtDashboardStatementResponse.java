package com.finance.backend.modules.dashboard.debts.dto;

import com.finance.backend.modules.debts.statement.model.StatementStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DebtDashboardStatementResponse(

        Long statementId,

        Long userCardId,

        Long cardId,

        String bank,

        String cardName,

        Integer year,

        Integer month,

        LocalDate paymentDate,

        Boolean paid,

        StatementStatus status,

        BigDecimal totalExpenses,

        BigDecimal totalPaid,

        BigDecimal totalPending,

        long totalEntries

) {
}