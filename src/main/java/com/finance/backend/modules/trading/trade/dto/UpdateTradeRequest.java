package com.finance.backend.modules.trading.trade.dto;

import com.finance.backend.modules.trading.trade.model.TradeSide;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateTradeRequest(

        @NotNull Long tradingAccountId,

        @NotNull Long instrumentId,

        @NotNull TradeSide side,

        @NotNull @DecimalMin(value = "0.00000001") BigDecimal quantity,

        @NotNull @DecimalMin(value = "0.00000001") BigDecimal price,

        @NotNull @DecimalMin(value = "0.00") BigDecimal commission,

        @NotNull @DecimalMin(value = "0.00") BigDecimal commissionRate,

        @NotNull LocalDate date

) {
}