package com.finance.backend.modules.dashboard.debts.dto;

import com.finance.backend.modules.debts.statement.model.StatementStatus;

public record DebtDashboardFilter(

                Integer year,

                Integer month,

                Long userCardId,

                Long conceptId,

                Boolean paid,

                StatementStatus status,

                String debtor

) {
}