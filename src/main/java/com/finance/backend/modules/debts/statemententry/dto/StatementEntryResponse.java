package com.finance.backend.modules.debts.statemententry.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StatementEntryResponse(
                Long entryId,
                Long statementId,
                Long conceptId,
                String debtor,
                String description,
                LocalDate purchaseDate,
                BigDecimal installmentAmount,
                Boolean paid,
                Integer msiCurrent,
                Integer msiTotal,
                BigDecimal purchaseTotal,
                Integer remainingMonths,
                BigDecimal remainingTotal) {
}