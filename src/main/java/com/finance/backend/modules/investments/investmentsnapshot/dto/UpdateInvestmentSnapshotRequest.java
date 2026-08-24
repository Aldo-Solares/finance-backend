package com.finance.backend.modules.investments.investmentsnapshot.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateInvestmentSnapshotRequest(

                @NotNull LocalDate balanceDate,

                @NotNull @DecimalMin(value = "0.00") BigDecimal balance,

                @NotNull @DecimalMin(value = "0.00") BigDecimal contribution,

                @NotNull @DecimalMin(value = "0.00") BigDecimal withdrawal) {
}