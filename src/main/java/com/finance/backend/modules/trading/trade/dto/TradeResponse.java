package com.finance.backend.modules.trading.trade.dto;

import com.finance.backend.modules.trading.trade.model.TradeStatus;
import com.finance.backend.modules.trading.tradesale.dto.TradeSaleResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record TradeResponse(

                Long tradeId,

                Long tradingAccountId,

                String tradingAccountName,

                Long instrumentId,

                String instrumentSymbol,

                String instrumentName,

                String currency,

                BigDecimal quantity,

                BigDecimal purchasePrice,

                BigDecimal purchaseCommission,

                BigDecimal purchaseCommissionRate,

                BigDecimal expectedPurchaseCommission,

                boolean purchaseCommissionValid,

                LocalDate purchaseDate,

                BigDecimal purchaseGrossAmount,

                BigDecimal purchaseTotalCost,

                BigDecimal soldQuantity,

                BigDecimal remainingQuantity,

                BigDecimal remainingCost,

                BigDecimal totalSaleAmount,

                BigDecimal totalSaleCommissions,

                BigDecimal realizedProfit,

                TradeStatus status,

                List<TradeSaleResponse> sales

) {
}