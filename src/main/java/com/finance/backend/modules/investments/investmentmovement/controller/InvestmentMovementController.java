package com.finance.backend.modules.investments.investmentmovement.controller;

import com.finance.backend.dto.ApiResponse;
import com.finance.backend.modules.investments.investmentmovement.dto.CreateInvestmentMovementRequest;
import com.finance.backend.modules.investments.investmentmovement.dto.InvestmentMovementResponse;
import com.finance.backend.modules.investments.investmentmovement.service.InvestmentMovementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/investment-movements")
public class InvestmentMovementController {

    private final InvestmentMovementService investmentMovementService;

    public InvestmentMovementController(
            InvestmentMovementService investmentMovementService) {
        this.investmentMovementService = investmentMovementService;
    }

    @GetMapping
    public ApiResponse<List<InvestmentMovementResponse>> findAll() {
        return ApiResponse.success(
                investmentMovementService.findAll());
    }

    @GetMapping("/{investmentMovementId}")
    public ApiResponse<InvestmentMovementResponse> findById(
            @PathVariable Long investmentMovementId) {

        return ApiResponse.success(
                investmentMovementService.findById(
                        investmentMovementId));
    }

    @GetMapping("/account/{investmentAccountId}")
    public ApiResponse<List<InvestmentMovementResponse>> findByInvestmentAccountId(
            @PathVariable Long investmentAccountId) {

        return ApiResponse.success(
                investmentMovementService
                        .findByInvestmentAccountId(
                                investmentAccountId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InvestmentMovementResponse>> create(
            @Valid @RequestBody CreateInvestmentMovementRequest request) {

        InvestmentMovementResponse movement = investmentMovementService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Movimiento de inversión creado",
                                movement));
    }
}