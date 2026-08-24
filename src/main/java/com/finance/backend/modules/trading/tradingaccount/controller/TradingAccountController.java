package com.finance.backend.modules.trading.tradingaccount.controller;

import com.finance.backend.dto.ApiResponse;
import com.finance.backend.modules.trading.tradingaccount.dto.CreateTradingAccountRequest;
import com.finance.backend.modules.trading.tradingaccount.dto.TradingAccountResponse;
import com.finance.backend.modules.trading.tradingaccount.dto.UpdateTradingAccountRequest;
import com.finance.backend.modules.trading.tradingaccount.service.TradingAccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
        public ResponseEntity<ApiResponse<List<TradingAccountResponse>>> findAll(
                        Authentication authentication) {

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                tradingAccountService.findAll(
                                                                authentication.getName())));
        }

        @GetMapping("/{tradingAccountId}")
        public ResponseEntity<ApiResponse<TradingAccountResponse>> findById(
                        @PathVariable Long tradingAccountId,
                        Authentication authentication) {

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                tradingAccountService.findById(
                                                                tradingAccountId,
                                                                authentication.getName())));
        }

        @PostMapping
        public ResponseEntity<ApiResponse<TradingAccountResponse>> create(
                        @Valid @RequestBody CreateTradingAccountRequest request,
                        Authentication authentication) {

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(
                                                ApiResponse.success(
                                                                "Cuenta de trading creada correctamente",
                                                                tradingAccountService.create(
                                                                                request,
                                                                                authentication.getName())));
        }

        @PutMapping("/{tradingAccountId}")
        public ResponseEntity<ApiResponse<TradingAccountResponse>> update(
                        @PathVariable Long tradingAccountId,
                        @Valid @RequestBody UpdateTradingAccountRequest request,
                        Authentication authentication) {

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Cuenta de trading actualizada correctamente",
                                                tradingAccountService.update(
                                                                tradingAccountId,
                                                                request,
                                                                authentication.getName())));
        }

        @DeleteMapping("/{tradingAccountId}")
        public ResponseEntity<ApiResponse<Void>> delete(
                        @PathVariable Long tradingAccountId,
                        Authentication authentication) {

                tradingAccountService.delete(
                                tradingAccountId,
                                authentication.getName());

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Cuenta de trading eliminada correctamente",
                                                null));
        }
}