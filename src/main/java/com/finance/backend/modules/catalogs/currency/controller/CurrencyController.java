package com.finance.backend.modules.catalogs.currency.controller;

import com.finance.backend.dto.ApiResponse;
import com.finance.backend.modules.catalogs.currency.dto.CreateCurrencyRequest;
import com.finance.backend.modules.catalogs.currency.dto.CurrencyResponse;
import com.finance.backend.modules.catalogs.currency.dto.UpdateCurrencyRequest;
import com.finance.backend.modules.catalogs.currency.service.CurrencyService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogs/currencies")
public class CurrencyController {

        private final CurrencyService service;

        public CurrencyController(
                        CurrencyService service) {
                this.service = service;
        }

        // ===================
        // GET ALL
        // ===================

        @GetMapping
        public ApiResponse<List<CurrencyResponse>> getCurrencies() {
                return ApiResponse.success(
                                service.getCurrencies());
        }

        // ===================
        // GET BY ID
        // ===================

        @GetMapping("/{currencyId}")
        public ApiResponse<CurrencyResponse> getCurrencyById(
                        @PathVariable Long currencyId) {

                return ApiResponse.success(
                                service.getCurrencyById(currencyId));
        }

        // ===================
        // CREATE
        // ===================

        @PreAuthorize("hasRole('ADMIN')")
        @PostMapping
        public ApiResponse<CurrencyResponse> createCurrency(
                        @Valid @RequestBody CreateCurrencyRequest request) {

                return ApiResponse.success(
                                service.createCurrency(request));
        }

        // ===================
        // UPDATE
        // ===================

        @PreAuthorize("hasRole('ADMIN')")
        @PutMapping("/{currencyId}")
        public ApiResponse<CurrencyResponse> updateCurrency(
                        @PathVariable Long currencyId,
                        @Valid @RequestBody UpdateCurrencyRequest request) {

                return ApiResponse.success(
                                service.updateCurrency(
                                                currencyId,
                                                request));
        }

        // ===================
        // DELETE
        // ===================

        @PreAuthorize("hasRole('ADMIN')")
        @DeleteMapping("/{currencyId}")
        public ApiResponse<Void> deleteCurrency(
                        @PathVariable Long currencyId) {

                service.deleteCurrency(currencyId);

                return ApiResponse.success(null);
        }
}