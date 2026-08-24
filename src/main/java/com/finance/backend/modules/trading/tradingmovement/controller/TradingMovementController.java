package com.finance.backend.modules.trading.tradingmovement.controller;

import com.finance.backend.dto.ApiResponse;
import com.finance.backend.modules.trading.tradingmovement.dto.CreateTradingMovementRequest;
import com.finance.backend.modules.trading.tradingmovement.dto.TradingMovementResponse;
import com.finance.backend.modules.trading.tradingmovement.dto.UpdateTradingMovementRequest;
import com.finance.backend.modules.trading.tradingmovement.service.TradingMovementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

        // ===================
        // FIND ALL
        // ===================

        @GetMapping
        public ResponseEntity<ApiResponse<List<TradingMovementResponse>>> findAll(
                        Authentication authentication) {

                List<TradingMovementResponse> movements = tradingMovementService.findAll(
                                authentication.getName());

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                movements));
        }

        // ===================
        // FIND BY ID
        // ===================

        @GetMapping("/{tradingMovementId}")
        public ResponseEntity<ApiResponse<TradingMovementResponse>> findById(
                        @PathVariable Long tradingMovementId,
                        Authentication authentication) {

                TradingMovementResponse movement = tradingMovementService.findById(
                                tradingMovementId,
                                authentication.getName());

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                movement));
        }

        // ===================
        // FIND BY ACCOUNT
        // ===================

        @GetMapping("/account/{tradingAccountId}")
        public ResponseEntity<ApiResponse<List<TradingMovementResponse>>> findByTradingAccountId(
                        @PathVariable Long tradingAccountId,
                        Authentication authentication) {

                List<TradingMovementResponse> movements = tradingMovementService.findByTradingAccountId(
                                tradingAccountId,
                                authentication.getName());

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                movements));
        }

        // ===================
        // CREATE
        // ===================

        @PostMapping
        public ResponseEntity<ApiResponse<TradingMovementResponse>> create(
                        @Valid @RequestBody CreateTradingMovementRequest request,
                        Authentication authentication) {

                TradingMovementResponse movement = tradingMovementService.create(
                                request,
                                authentication.getName());

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(
                                                ApiResponse.success(
                                                                "Movimiento de trading creado correctamente",
                                                                movement));
        }

        // ===================
        // UPDATE
        // ===================

        @PutMapping("/{tradingMovementId}")
        public ResponseEntity<ApiResponse<TradingMovementResponse>> update(
                        @PathVariable Long tradingMovementId,
                        @Valid @RequestBody UpdateTradingMovementRequest request,
                        Authentication authentication) {

                TradingMovementResponse movement = tradingMovementService.update(
                                tradingMovementId,
                                request,
                                authentication.getName());

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Movimiento de trading actualizado correctamente",
                                                movement));
        }

        // ===================
        // DELETE
        // ===================

        @DeleteMapping("/{tradingMovementId}")
        public ResponseEntity<ApiResponse<Void>> delete(
                        @PathVariable Long tradingMovementId,
                        Authentication authentication) {

                tradingMovementService.delete(
                                tradingMovementId,
                                authentication.getName());

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Movimiento de trading eliminado correctamente",
                                                null));
        }
}