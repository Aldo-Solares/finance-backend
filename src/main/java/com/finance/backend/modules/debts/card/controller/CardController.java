package com.finance.backend.modules.debts.card.controller;

import com.finance.backend.dto.ApiResponse;
import com.finance.backend.modules.debts.card.dto.CardResponse;
import com.finance.backend.modules.debts.card.dto.CreateCardRequest;
import com.finance.backend.modules.debts.card.dto.UpdateCardRequest;
import com.finance.backend.modules.debts.card.service.CardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardController {

        private final CardService cardService;

        public CardController(
                        CardService cardService) {
                this.cardService = cardService;
        }

        // ===================
        // FIND ALL
        // ===================

        @GetMapping
        public ApiResponse<List<CardResponse>> findAll(
                        Authentication authentication) {

                return ApiResponse.success(
                                cardService.findAll(
                                                authentication.getName()));
        }

        // ===================
        // FIND BY ID
        // ===================

        @GetMapping("/{cardId}")
        public ApiResponse<CardResponse> findById(
                        @PathVariable Long cardId,
                        Authentication authentication) {

                return ApiResponse.success(
                                cardService.findById(
                                                cardId,
                                                authentication.getName()));
        }

        // ===================
        // CREATE
        // ===================

        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public ApiResponse<CardResponse> create(
                        @Valid @RequestBody CreateCardRequest request,
                        Authentication authentication) {

                return ApiResponse.success(
                                "Tarjeta creada",
                                cardService.create(
                                                request,
                                                authentication.getName()));
        }

        // ===================
        // UPDATE
        // ===================

        @PutMapping("/{cardId}")
        public ApiResponse<CardResponse> update(
                        @PathVariable Long cardId,
                        @Valid @RequestBody UpdateCardRequest request,
                        Authentication authentication) {

                return ApiResponse.success(
                                "Tarjeta actualizada",
                                cardService.update(
                                                cardId,
                                                request,
                                                authentication.getName()));
        }

        // ===================
        // DELETE
        // ===================

        @DeleteMapping("/{cardId}")
        public ApiResponse<Void> delete(
                        @PathVariable Long cardId,
                        Authentication authentication) {

                cardService.delete(
                                cardId,
                                authentication.getName());

                return ApiResponse.success(
                                "Tarjeta eliminada",
                                null);
        }

}

        