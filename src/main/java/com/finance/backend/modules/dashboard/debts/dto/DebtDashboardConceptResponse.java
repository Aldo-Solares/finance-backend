package com.finance.backend.modules.dashboard.debts.dto;

import java.math.BigDecimal;

public record DebtDashboardConceptResponse(

                Long conceptId,

                String conceptName,

                BigDecimal totalExpenses,

                long totalEntries,

                BigDecimal percentage

) {
}