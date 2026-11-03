package com.finance.backend.modules.dashboard.debts.dto;

import java.math.BigDecimal;

public record DebtDashboardConceptResponse(

        Long categoryId,

        String categoryName,

        BigDecimal totalExpenses,

        long totalEntries,

        BigDecimal percentage

) {
}