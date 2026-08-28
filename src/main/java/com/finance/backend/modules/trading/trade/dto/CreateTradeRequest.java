package com.finance.backend.modules.trading.trade.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTradeRequest(

                @NotNull Long tradingAccountId,

                @NotNull Long instrumentId,

                @NotNull @DecimalMin("0.00000001") BigDecimal quantity,

                @NotNull @DecimalMin("0.00000001") BigDecimal purchasePrice,

                @NotNull @DecimalMin("0.0") BigDecimal purchaseCommission,

                @NotNull @DecimalMin("0.0") BigDecimal purchaseCommissionRate,

                @NotNull LocalDate purchaseDate

) {
}