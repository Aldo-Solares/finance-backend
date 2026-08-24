package com.finance.backend.modules.trading.trade.controller;

import com.finance.backend.dto.ApiResponse;
import com.finance.backend.modules.trading.trade.dto.CreateTradeRequest;
import com.finance.backend.modules.trading.trade.dto.TradeResponse;
import com.finance.backend.modules.trading.trade.dto.UpdateTradeRequest;
import com.finance.backend.modules.trading.trade.service.TradeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

        // ===================
        // FIND ALL
        // ===================

        @GetMapping
        public ResponseEntity<ApiResponse<List<TradeResponse>>> findAll(
                        Authentication authentication) {

                List<TradeResponse> trades = tradeService.findAll(
                                authentication.getName());

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                trades));
        }

        // ===================
        // FIND BY ID
        // ===================

        @GetMapping("/{tradeId}")
        public ResponseEntity<ApiResponse<TradeResponse>> findById(
                        @PathVariable Long tradeId,
                        Authentication authentication) {

                TradeResponse trade = tradeService.findById(
                                tradeId,
                                authentication.getName());

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                trade));
        }

        // ===================
        // FIND BY ACCOUNT
        // ===================

        @GetMapping("/account/{tradingAccountId}")
        public ResponseEntity<ApiResponse<List<TradeResponse>>> findByTradingAccountId(
                        @PathVariable Long tradingAccountId,
                        Authentication authentication) {

                List<TradeResponse> trades = tradeService.findByTradingAccountId(
                                tradingAccountId,
                                authentication.getName());

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                trades));
        }

        // ===================
        // CREATE
        // ===================

        @PostMapping
        public ResponseEntity<ApiResponse<TradeResponse>> create(
                        @Valid @RequestBody CreateTradeRequest request,
                        Authentication authentication) {

                TradeResponse trade = tradeService.create(
                                request,
                                authentication.getName());

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(
                                                ApiResponse.success(
                                                                "Trade creado correctamente",
                                                                trade));
        }

        // ===================
        // UPDATE
        // ===================

        @PutMapping("/{tradeId}")
        public ResponseEntity<ApiResponse<TradeResponse>> update(
                        @PathVariable Long tradeId,
                        @Valid @RequestBody UpdateTradeRequest request,
                        Authentication authentication) {

                TradeResponse trade = tradeService.update(
                                tradeId,
                                request,
                                authentication.getName());

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Trade actualizado correctamente",
                                                trade));
        }

        // ===================
        // DELETE
        // ===================

        @DeleteMapping("/{tradeId}")
        public ResponseEntity<ApiResponse<Void>> delete(
                        @PathVariable Long tradeId,
                        Authentication authentication) {

                tradeService.delete(
                                tradeId,
                                authentication.getName());

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Trade eliminado correctamente",
                                                null));
        }
}