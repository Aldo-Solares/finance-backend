package com.finance.backend.modules.trading.tradingaccount.controller;

import com.finance.backend.dto.ApiResponse;
import com.finance.backend.modules.trading.tradingaccount.dto.CreateTradingAccountRequest;
import com.finance.backend.modules.trading.tradingaccount.dto.TradingAccountResponse;
import com.finance.backend.modules.trading.tradingaccount.dto.UpdateTradingAccountRequest;
import com.finance.backend.modules.trading.tradingaccount.service.TradingAccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trading-accounts")
public class TradingAccountController {

    private final TradingAccountService tradingAccountService;

    public TradingAccountController(
            TradingAccountService tradingAccountService) {
        this.tradingAccountService = tradingAccountService;
    }

    @GetMapping
    public ApiResponse<List<TradingAccountResponse>> findAll() {
        return ApiResponse.success(
                tradingAccountService.findAll());
    }

    @GetMapping("/{tradingAccountId}")
    public ApiResponse<TradingAccountResponse> findById(
            @PathVariable Long tradingAccountId) {

        return ApiResponse.success(
                tradingAccountService.findById(
                        tradingAccountId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TradingAccountResponse>> create(
            @Valid @RequestBody CreateTradingAccountRequest request) {

        TradingAccountResponse account = tradingAccountService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Cuenta de trading creada",
                                account));
    }

    @PutMapping("/{tradingAccountId}")
    public ApiResponse<TradingAccountResponse> update(
            @PathVariable Long tradingAccountId,
            @Valid @RequestBody UpdateTradingAccountRequest request) {

        return ApiResponse.success(
                "Cuenta de trading actualizada",
                tradingAccountService.update(
                        tradingAccountId,
                        request));
    }
}