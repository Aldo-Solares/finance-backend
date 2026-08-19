package com.finance.backend.modules.trading.tradingmovement.controller;

import com.finance.backend.dto.ApiResponse;
import com.finance.backend.modules.trading.tradingmovement.dto.CreateTradingMovementRequest;
import com.finance.backend.modules.trading.tradingmovement.dto.TradingMovementResponse;
import com.finance.backend.modules.trading.tradingmovement.service.TradingMovementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trading-movements")
public class TradingMovementController {

    private final TradingMovementService tradingMovementService;

    public TradingMovementController(
            TradingMovementService tradingMovementService) {
        this.tradingMovementService = tradingMovementService;
    }

    @GetMapping
    public ApiResponse<List<TradingMovementResponse>> findAll() {
        return ApiResponse.success(
                tradingMovementService.findAll());
    }

    @GetMapping("/{tradingMovementId}")
    public ApiResponse<TradingMovementResponse> findById(
            @PathVariable Long tradingMovementId) {

        return ApiResponse.success(
                tradingMovementService.findById(
                        tradingMovementId));
    }

    @GetMapping("/account/{tradingAccountId}")
    public ApiResponse<List<TradingMovementResponse>> findByTradingAccountId(
            @PathVariable Long tradingAccountId) {

        return ApiResponse.success(
                tradingMovementService
                        .findByTradingAccountId(
                                tradingAccountId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TradingMovementResponse>> create(
            @Valid @RequestBody CreateTradingMovementRequest request) {

        TradingMovementResponse movement = tradingMovementService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Movimiento de trading creado",
                                movement));
    }
}