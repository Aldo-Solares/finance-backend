package com.finance.backend.modules.debts.statemententry.dto;

import com.finance.backend.modules.debts.statemententry.model.StatementEntryType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StatementEntryResponse(
                Long entryId,
                Long statementId,
                Long conceptId,
                String conceptName,
                String debtor,
                String specification,
                String notes,
                StatementEntryType entryType,
                LocalDate date,
                BigDecimal amount,
                Boolean paid,
                Integer msiCurrent,
                Integer msiTotal,
                BigDecimal purchaseAmount,
                Integer remainingMsi,
                BigDecimal remainingMsiAmount) {
}