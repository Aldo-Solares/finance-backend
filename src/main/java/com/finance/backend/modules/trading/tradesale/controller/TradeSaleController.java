package com.finance.backend.modules.trading.tradesale.controller;

import com.finance.backend.dto.ApiResponse;
import com.finance.backend.modules.trading.tradesale.dto.CreateTradeSaleRequest;
import com.finance.backend.modules.trading.tradesale.dto.TradeSaleResponse;
import com.finance.backend.modules.trading.tradesale.dto.UpdateTradeSaleRequest;
import com.finance.backend.modules.trading.tradesale.service.TradeSaleService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trade-sales")
public class TradeSaleController {

        private final TradeSaleService service;

        public TradeSaleController(
                        TradeSaleService service) {
                this.service = service;
        }

        @GetMapping("/{tradeSaleId}")
        public ApiResponse<TradeSaleResponse> findById(
                        @PathVariable Long tradeSaleId,
                        Authentication authentication) {
                return ApiResponse.success(
                                service.findById(
                                                tradeSaleId,
                                                authentication.getName()));
        }

        @GetMapping("/trade/{tradeId}")
        public ApiResponse<List<TradeSaleResponse>> findByTradeId(
                        @PathVariable Long tradeId,
                        Authentication authentication) {
                return ApiResponse.success(
                                service.findByTradeId(
                                                tradeId,
                                                authentication.getName()));
        }

        @PostMapping
        public ApiResponse<TradeSaleResponse> create(
                        @Valid @RequestBody CreateTradeSaleRequest request,
                        Authentication authentication) {
                return ApiResponse.success(
                                "Trade sale created successfully",
                                service.create(
                                                request,
                                                authentication.getName()));
        }

        @PutMapping("/{tradeSaleId}")
        public ApiResponse<TradeSaleResponse> update(
                        @PathVariable Long tradeSaleId,
                        @Valid @RequestBody UpdateTradeSaleRequest request,
                        Authentication authentication) {
                return ApiResponse.success(
                                "Trade sale updated successfully",
                                service.update(
                                                tradeSaleId,
                                                request,
                                                authentication.getName()));
        }

        @DeleteMapping("/{tradeSaleId}")
        public ApiResponse<Void> delete(
                        @PathVariable Long tradeSaleId,
                        Authentication authentication) {
                service.delete(
                                tradeSaleId,
                                authentication.getName());

                return ApiResponse.success(
                                "Trade sale deleted successfully",
                                null);
        }
}