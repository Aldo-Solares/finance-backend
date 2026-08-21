package com.finance.backend.modules.debts.cardproduct.controller;

import com.finance.backend.dto.ApiResponse;
import com.finance.backend.modules.debts.cardproduct.dto.CreateCardProductRequest;
import com.finance.backend.modules.debts.cardproduct.dto.UpdateCardProductRequest;
import com.finance.backend.modules.debts.cardproduct.dto.CardProductResponse;
import com.finance.backend.modules.debts.cardproduct.service.CardProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/card-products")
public class CardProductController {

        private final CardProductService cardProductService;

        public CardProductController(
                        CardProductService cardProductService) {
                this.cardProductService = cardProductService;
        }

        @GetMapping
        public ApiResponse<List<CardProductResponse>> findAll() {
                return ApiResponse.success(
                                cardProductService.findAll());
        }

        @GetMapping("/{productId}")
        public ApiResponse<CardProductResponse> findById(
                        @PathVariable Long productId) {
                return ApiResponse.success(
                                cardProductService.findById(productId));
        }

        @PostMapping
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ApiResponse<CardProductResponse>> create(
                        @Valid @RequestBody CreateCardProductRequest request) {
                CardProductResponse product = cardProductService.create(request);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(
                                                ApiResponse.success(
                                                                "Producto de tarjeta creado",
                                                                product));
        }

        @PutMapping("/{productId}")
        @PreAuthorize("hasRole('ADMIN')")
        public ApiResponse<CardProductResponse> update(
                        @PathVariable Long productId,
                        @Valid @RequestBody UpdateCardProductRequest request) {

                return ApiResponse.success(
                                "Producto de tarjeta actualizado",
                                cardProductService.update(
                                                productId,
                                                request));
        }

        @DeleteMapping("/{productId}")
        @PreAuthorize("hasRole('ADMIN')")
        public ApiResponse<Void> delete(
                        @PathVariable Long productId) {

                cardProductService.delete(productId);

                return ApiResponse.success(
                                "Producto de tarjeta eliminado",
                                null);
        }
}