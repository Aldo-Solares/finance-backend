package com.finance.backend.modules.trading.trade.controller;

import com.finance.backend.dto.ApiResponse;
import com.finance.backend.modules.trading.trade.dto.CreateTradeRequest;
import com.finance.backend.modules.trading.trade.dto.TradeResponse;
import com.finance.backend.modules.trading.trade.dto.UpdateTradeRequest;
import com.finance.backend.modules.trading.trade.service.TradeService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trades")
public class TradeController {

        private final TradeService service;

        public TradeController(
                        TradeService service) {

                this.service = service;
        }

        // ===================
        // QUERIES
        // ===================

        @GetMapping
        public ApiResponse<List<TradeResponse>> findAll(
                        Authentication authentication) {

                return ApiResponse.success(
                                service.findAll(
                                                authentication.getName()));
        }

        @GetMapping("/{tradeId}")
        public ApiResponse<TradeResponse> findById(
                        @PathVariable Long tradeId,
                        Authentication authentication) {

                return ApiResponse.success(
                                service.findById(
                                                tradeId,
                                                authentication.getName()));
        }

        @GetMapping("/account/{userTradingAccountId}")
        public ApiResponse<List<TradeResponse>> findByAccountId(
                        @PathVariable Long userTradingAccountId,
                        Authentication authentication) {

                return ApiResponse.success(
                                service.findByAccountId(
                                                userTradingAccountId,
                                                authentication.getName()));
        }

        // ===================
        // CREATE
        // ===================

        @PostMapping
        public ApiResponse<TradeResponse> create(
                        @Valid @RequestBody CreateTradeRequest request,
                        Authentication authentication) {

                return ApiResponse.success(
                                "Trade created successfully",
                                service.create(
                                                request,
                                                authentication.getName()));
        }

        // ===================
        // UPDATE
        // ===================

        @PutMapping("/{tradeId}")
        public ApiResponse<TradeResponse> update(
                        @PathVariable Long tradeId,
                        @Valid @RequestBody UpdateTradeRequest request,
                        Authentication authentication) {

                return ApiResponse.success(
                                "Trade updated successfully",
                                service.update(
                                                tradeId,
                                                request,
                                                authentication.getName()));
        }

        // ===================
        // DELETE
        // ===================

        @DeleteMapping("/{tradeId}")
        public ApiResponse<Void> delete(
                        @PathVariable Long tradeId,
                        Authentication authentication) {

                service.delete(
                                tradeId,
                                authentication.getName());

                return ApiResponse.success(
                                "Trade deleted successfully",
                                null);
        }
}