package com.finance.backend.modules.trading.tradesale.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TradeSaleResponse(

                Long tradeSaleId,

                Long tradeId,

                BigDecimal quantity,

                BigDecimal salePrice,

                BigDecimal commission,

                BigDecimal commissionRate,

                BigDecimal expectedCommission,

                boolean commissionValid,

                LocalDate saleDate,

                BigDecimal grossAmount,

                BigDecimal netAmount,

                BigDecimal costBasis,

                BigDecimal realizedProfit

) {
}