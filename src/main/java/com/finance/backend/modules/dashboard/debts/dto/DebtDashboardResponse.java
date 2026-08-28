package com.finance.backend.modules.dashboard.debts.dto;

import java.math.BigDecimal;
import java.util.List;

public record DebtDashboardResponse(

                Integer year,

                Integer month,

                BigDecimal totalExpenses,

                BigDecimal totalPaid,

                BigDecimal totalPending,

                long totalEntries,

                BigDecimal averageExpense,

                List<DebtDashboardCardResponse> cards,

                List<DebtDashboardConceptResponse> concepts,

                List<DebtDashboardStatementResponse> statements

) {
}