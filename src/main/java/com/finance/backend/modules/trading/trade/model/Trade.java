package com.finance.backend.modules.trading.trade.model;

import com.finance.backend.modules.trading.instrument.model.Instrument;
import com.finance.backend.modules.trading.tradingaccount.model.TradingAccount;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "trades")
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trade_id")
    private Long tradeId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trading_account_id", nullable = false)
    private TradingAccount tradingAccount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instrument_id", nullable = false)
    private Instrument instrument;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TradeSide side;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal price;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal commission;

    @Column(name = "commission_rate", nullable = false, precision = 8, scale = 4)
    private BigDecimal commissionRate;

    @Column(nullable = false)
    private LocalDate date;

    public Trade() {
    }

    public Long getTradeId() {
        return tradeId;
    }

    public void setTradeId(Long tradeId) {
        this.tradeId = tradeId;
    }

    public TradingAccount getTradingAccount() {
        return tradingAccount;
    }

    public void setTradingAccount(
            TradingAccount tradingAccount) {
        this.tradingAccount = tradingAccount;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(
            Instrument instrument) {
        this.instrument = instrument;
    }

    public TradeSide getSide() {
        return side;
    }

    public void setSide(
            TradeSide side) {
        this.side = side;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(
            BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(
            BigDecimal price) {
        this.price = price;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(
            LocalDate date) {
        this.date = date;
    }
}