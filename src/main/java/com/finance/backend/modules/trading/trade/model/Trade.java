package com.finance.backend.modules.trading.trade.model;

import com.finance.backend.modules.trading.instrument.model.Instrument;
import com.finance.backend.modules.trading.tradesale.model.TradeSale;
import com.finance.backend.modules.trading.usertradingaccount.model.UserTradingAccount;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trades")
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trade_id")
    private Long tradeId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_trading_account_id", nullable = false)
    private UserTradingAccount userTradingAccount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instrument_id", nullable = false)
    private Instrument instrument;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal quantity;

    @Column(name = "purchase_price", nullable = false, precision = 19, scale = 8)
    private BigDecimal purchasePrice;

    @Column(name = "purchase_commission", nullable = false, precision = 19, scale = 8)
    private BigDecimal purchaseCommission;

    @Column(name = "purchase_commission_rate", nullable = false, precision = 10, scale = 6)
    private BigDecimal purchaseCommissionRate;

    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    @OneToMany(mappedBy = "trade", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("saleDate ASC, tradeSaleId ASC")
    private List<TradeSale> sales = new ArrayList<>();

    public Trade() {
    }

    public Long getTradeId() {
        return tradeId;
    }

    public void setTradeId(Long tradeId) {
        this.tradeId = tradeId;
    }

    public UserTradingAccount getUserTradingAccount() {
        return userTradingAccount;
    }

    public void setUserTradingAccount(UserTradingAccount userTradingAccount) {
        this.userTradingAccount = userTradingAccount;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public BigDecimal getPurchaseCommission() {
        return purchaseCommission;
    }

    public void setPurchaseCommission(BigDecimal purchaseCommission) {
        this.purchaseCommission = purchaseCommission;
    }

    public BigDecimal getPurchaseCommissionRate() {
        return purchaseCommissionRate;
    }

    public void setPurchaseCommissionRate(BigDecimal purchaseCommissionRate) {
        this.purchaseCommissionRate = purchaseCommissionRate;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public List<TradeSale> getSales() {
        return sales;
    }

    public void setSales(List<TradeSale> sales) {
        this.sales = sales;
    }
}