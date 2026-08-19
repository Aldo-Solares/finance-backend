package com.finance.backend.modules.trading.trade.controller;

import com.finance.backend.dto.ApiResponse;
import com.finance.backend.modules.trading.trade.dto.CreateTradeRequest;
import com.finance.backend.modules.trading.trade.dto.TradeResponse;
import com.finance.backend.modules.trading.trade.service.TradeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trades")
public class TradeController {

    private final TradeService tradeService;

    public TradeController(
            TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @GetMapping
    public ApiResponse<List<TradeResponse>> findAll() {
        return ApiResponse.success(
                tradeService.findAll());
    }

    @GetMapping("/{tradeId}")
    public ApiResponse<TradeResponse> findById(
            @PathVariable Long tradeId) {

        return ApiResponse.success(
                tradeService.findById(
                        tradeId));
    }

    @GetMapping("/account/{tradingAccountId}")
    public ApiResponse<List<TradeResponse>> findByTradingAccountId(
            @PathVariable Long tradingAccountId) {

        return ApiResponse.success(
                tradeService.findByTradingAccountId(
                        tradingAccountId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TradeResponse>> create(
            @Valid @RequestBody CreateTradeRequest request) {

        TradeResponse trade = tradeService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Trade creado",
                                trade));
    }
}