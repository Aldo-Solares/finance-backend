package com.finance.backend.modules.trading.tradesale.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTradeSaleRequest(

                @NotNull Long tradeId,

                @NotNull @DecimalMin("0.00000001") BigDecimal quantity,

                @NotNull @DecimalMin("0.00000001") BigDecimal salePrice,

                @NotNull @DecimalMin("0.0") BigDecimal commission,

                @NotNull @DecimalMin("0.0") BigDecimal commissionRate,

                @NotNull LocalDate saleDate

) {
}