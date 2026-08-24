package com.finance.backend.modules.investments.investmentsnapshot.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvestmentSnapshotResponse(

        Long investmentSnapshotId,

        LocalDate balanceDate,

        BigDecimal balance,

        BigDecimal contribution,

        BigDecimal withdrawal,

        BigDecimal generatedAmount) {
}