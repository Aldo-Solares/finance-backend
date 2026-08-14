package com.finance.backend.modules.card.controller;

import com.finance.backend.dto.ApiResponse;
import com.finance.backend.modules.card.dto.CardResponse;
import com.finance.backend.modules.card.dto.CreateCardRequest;
import com.finance.backend.modules.card.dto.UpdateCardRequest;
import com.finance.backend.modules.card.service.CardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public ApiResponse<List<CardResponse>> findAll() {
        return ApiResponse.success(
                cardService.findAll());
    }

    @GetMapping("/{cardId}")
    public ApiResponse<CardResponse> findById(
            @PathVariable Long cardId) {
        return ApiResponse.success(
                cardService.findById(cardId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CardResponse>> create(
            @Valid @RequestBody CreateCardRequest request) {
        CardResponse card = cardService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Tarjeta creada",
                                card));
    }

    @PutMapping("/{cardId}")
    public ApiResponse<CardResponse> update(
            @PathVariable Long cardId,
            @Valid @RequestBody UpdateCardRequest request) {
        return ApiResponse.success(
                "Tarjeta actualizada",
                cardService.update(
                        cardId,
                        request));
    }

    @DeleteMapping("/{cardId}")
    public ApiResponse<Void> delete(
            @PathVariable Long cardId) {
        cardService.delete(cardId);

        return ApiResponse.success(
                "Tarjeta eliminada",
                null);
    }
}