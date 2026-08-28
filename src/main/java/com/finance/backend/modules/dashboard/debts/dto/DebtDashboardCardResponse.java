package com.finance.backend.modules.dashboard.debts.dto;

import java.math.BigDecimal;

public record DebtDashboardCardResponse(

                Long userCardId,

                Long cardId,

                String bank,

                String cardName,

                BigDecimal totalExpenses,

                BigDecimal totalPaid,

                BigDecimal totalPending,

                long totalEntries,

                BigDecimal percentage

) {
}