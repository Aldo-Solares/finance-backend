package com.finance.backend.modules.trading.tradesale.model;

import com.finance.backend.modules.trading.trade.model.Trade;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "trade_sales")
public class TradeSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trade_sale_id")
    private Long tradeSaleId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trade_id", nullable = false)
    private Trade trade;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal quantity;

    @Column(name = "sale_price", nullable = false, precision = 19, scale = 8)
    private BigDecimal salePrice;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal commission;

    @Column(name = "commission_rate", nullable = false, precision = 10, scale = 6)
    private BigDecimal commissionRate;

    @Column(name = "sale_date", nullable = false)
    private LocalDate saleDate;

    public TradeSale() {
    }

    public Long getTradeSaleId() {
        return tradeSaleId;
    }

    public void setTradeSaleId(
            Long tradeSaleId) {
        this.tradeSaleId = tradeSaleId;
    }

    public Trade getTrade() {
        return trade;
    }

    public void setTrade(
            Trade trade) {
        this.trade = trade;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(
            BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(
            BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(
            BigDecimal commission) {
        this.commission = commission;
    }

    public BigDecimal getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(
            BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(
            LocalDate saleDate) {
        this.saleDate = saleDate;
    }
}