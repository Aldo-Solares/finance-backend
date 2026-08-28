package com.finance.backend.modules.trading.usertradingaccount.controller;

import com.finance.backend.dto.ApiResponse;
import com.finance.backend.modules.trading.usertradingaccount.dto.CreateUserTradingAccountRequest;
import com.finance.backend.modules.trading.usertradingaccount.dto.UpdateUserTradingAccountRequest;
import com.finance.backend.modules.trading.usertradingaccount.dto.UserTradingAccountResponse;
import com.finance.backend.modules.trading.usertradingaccount.service.UserTradingAccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-trading-accounts")
public class UserTradingAccountController {

    private final UserTradingAccountService userTradingAccountService;

    public UserTradingAccountController(
            UserTradingAccountService userTradingAccountService) {

        this.userTradingAccountService = userTradingAccountService;
    }

    // ===================
    // QUERIES
    // ===================

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserTradingAccountResponse>>> findAll(
            Authentication authentication) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        userTradingAccountService.findAll(
                                authentication.getName())));
    }

    @GetMapping("/{userTradingAccountId}")
    public ResponseEntity<ApiResponse<UserTradingAccountResponse>> findById(
            @PathVariable Long userTradingAccountId,
            Authentication authentication) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        userTradingAccountService.findById(
                                userTradingAccountId,
                                authentication.getName())));
    }

    // ===================
    // CREATE
    // ===================

    @PostMapping
    public ResponseEntity<ApiResponse<UserTradingAccountResponse>> create(
            @Valid @RequestBody CreateUserTradingAccountRequest request,
            Authentication authentication) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Cuenta de trading agregada al usuario correctamente",
                                userTradingAccountService.create(
                                        request,
                                        authentication.getName())));
    }

    // ===================
    // UPDATE
    // ===================

    @PutMapping("/{userTradingAccountId}")
    public ResponseEntity<ApiResponse<UserTradingAccountResponse>> update(
            @PathVariable Long userTradingAccountId,
            @Valid @RequestBody UpdateUserTradingAccountRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cuenta de trading del usuario actualizada correctamente",
                        userTradingAccountService.update(
                                userTradingAccountId,
                                request,
                                authentication.getName())));
    }

    // ===================
    // DELETE
    // ===================

    @DeleteMapping("/{userTradingAccountId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long userTradingAccountId,
            Authentication authentication) {

        userTradingAccountService.delete(
                userTradingAccountId,
                authentication.getName());

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cuenta de trading eliminada del usuario correctamente",
                        null));
    }
}