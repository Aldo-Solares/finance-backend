package com.finance.backend.modules.investments.investmentaccount.controller;

import com.finance.backend.dto.ApiResponse;
import com.finance.backend.modules.investments.investmentaccount.dto.CreateInvestmentAccountRequest;
import com.finance.backend.modules.investments.investmentaccount.dto.InvestmentAccountResponse;
import com.finance.backend.modules.investments.investmentaccount.dto.UpdateInvestmentAccountRequest;
import com.finance.backend.modules.investments.investmentaccount.service.InvestmentAccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/investment-accounts")
public class InvestmentAccountController {

    private final InvestmentAccountService investmentAccountService;

    public InvestmentAccountController(
            InvestmentAccountService investmentAccountService) {
        this.investmentAccountService = investmentAccountService;
    }

    @GetMapping
    public ApiResponse<List<InvestmentAccountResponse>> findAll() {
        return ApiResponse.success(
                investmentAccountService.findAll());
    }

    @GetMapping("/{investmentAccountId}")
    public ApiResponse<InvestmentAccountResponse> findById(
            @PathVariable Long investmentAccountId) {
        return ApiResponse.success(
                investmentAccountService.findById(
                        investmentAccountId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InvestmentAccountResponse>> create(
            @Valid @RequestBody CreateInvestmentAccountRequest request) {

        InvestmentAccountResponse account = investmentAccountService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Cuenta de inversión creada",
                                account));
    }

    @PutMapping("/{investmentAccountId}")
    public ApiResponse<InvestmentAccountResponse> update(
            @PathVariable Long investmentAccountId,
            @Valid @RequestBody UpdateInvestmentAccountRequest request) {

        return ApiResponse.success(
                "Cuenta de inversión actualizada",
                investmentAccountService.update(
                        investmentAccountId,
                        request));
    }
}