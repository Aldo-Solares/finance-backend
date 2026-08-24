package com.finance.backend.modules.investments.investmentsnapshot.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvestmentPerformanceResponse(

        BigDecimal currentBalance,

        BigDecimal generatedLastPeriod,

        BigDecimal generatedTotal,

        BigDecimal totalContributions,

        BigDecimal totalWithdrawals,

        LocalDate lastBalanceDate) {
}