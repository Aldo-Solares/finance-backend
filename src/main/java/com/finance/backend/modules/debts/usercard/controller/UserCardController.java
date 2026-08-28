package com.finance.backend.modules.debts.usercard.controller;

import com.finance.backend.dto.ApiResponse;
import com.finance.backend.modules.debts.usercard.dto.CreateUserCardRequest;
import com.finance.backend.modules.debts.usercard.dto.UpdateUserCardRequest;
import com.finance.backend.modules.debts.usercard.dto.UserCardResponse;
import com.finance.backend.modules.debts.usercard.service.UserCardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-cards")
public class UserCardController {

        private final UserCardService userCardService;

        public UserCardController(
                        UserCardService userCardService) {

                this.userCardService = userCardService;
        }

        // ===================
        // FIND ALL
        // ===================

        @GetMapping
        public ApiResponse<List<UserCardResponse>> findAll(
                        Authentication authentication) {

                return ApiResponse.success(
                                userCardService.findAll(
                                                authentication.getName()));
        }

        // ===================
        // FIND ACTIVE
        // ===================

        @GetMapping("/active")
        public ApiResponse<List<UserCardResponse>> findAllActive(
                        Authentication authentication) {

                return ApiResponse.success(
                                userCardService.findAllActive(
                                                authentication.getName()));
        }

        // ===================
        // FIND BY ID
        // ===================

        @GetMapping("/{userCardId}")
        public ApiResponse<UserCardResponse> findById(
                        @PathVariable Long userCardId,
                        Authentication authentication) {

                return ApiResponse.success(
                                userCardService.findById(
                                                userCardId,
                                                authentication.getName()));
        }

        // ===================
        // CREATE
        // ===================

        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public ApiResponse<UserCardResponse> create(
                        @Valid @RequestBody CreateUserCardRequest request,
                        Authentication authentication) {

                return ApiResponse.success(
                                "Tarjeta agregada",
                                userCardService.create(
                                                request,
                                                authentication.getName()));
        }

        // ===================
        // UPDATE
        // ===================

        @PutMapping("/{userCardId}")
        public ApiResponse<UserCardResponse> update(
                        @PathVariable Long userCardId,
                        @Valid @RequestBody UpdateUserCardRequest request,
                        Authentication authentication) {

                return ApiResponse.success(
                                "Tarjeta actualizada",
                                userCardService.update(
                                                userCardId,
                                                request,
                                                authentication.getName()));
        }

        // ===================
        // DELETE
        // ===================

        @DeleteMapping("/{userCardId}")
        public ApiResponse<Void> delete(
                        @PathVariable Long userCardId,
                        Authentication authentication) {

                userCardService.delete(
                                userCardId,
                                authentication.getName());

                return ApiResponse.success(
                                "Tarjeta eliminada del usuario",
                                null);
        }
}