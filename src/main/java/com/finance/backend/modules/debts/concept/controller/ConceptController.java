package com.finance.backend.modules.debts.concept.controller;

import com.finance.backend.dto.ApiResponse;
import com.finance.backend.modules.debts.concept.dto.ConceptResponse;
import com.finance.backend.modules.debts.concept.dto.CreateConceptRequest;
import com.finance.backend.modules.debts.concept.dto.UpdateConceptRequest;
import com.finance.backend.modules.debts.concept.service.ConceptService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class ConceptController {

        private final ConceptService conceptService;

        public ConceptController(
                        ConceptService conceptService) {
                this.conceptService = conceptService;
        }

        @GetMapping
        public ApiResponse<List<ConceptResponse>> findAll() {
                return ApiResponse.success(
                                conceptService.findAll());
        }

        @GetMapping("/{categoryId}")
        public ApiResponse<ConceptResponse> findById(
                        @PathVariable Long categoryId) {
                return ApiResponse.success(
                                conceptService.findById(categoryId));
        }

        @PostMapping
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ApiResponse<ConceptResponse>> create(
                        @Valid @RequestBody CreateConceptRequest request) {

                ConceptResponse concept = conceptService.create(request);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(
                                                ApiResponse.success(
                                                                "Categoría creada",
                                                                concept));
        }

        @PutMapping("/{categoryId}")
        @PreAuthorize("hasRole('ADMIN')")
        public ApiResponse<ConceptResponse> update(
                        @PathVariable Long categoryId,
                        @Valid @RequestBody UpdateConceptRequest request) {

                return ApiResponse.success(
                                "Categoría actualizada",
                                conceptService.update(
                                                categoryId,
                                                request));
        }

        @DeleteMapping("/{categoryId}")
        @PreAuthorize("hasRole('ADMIN')")
        public ApiResponse<Void> delete(
                        @PathVariable Long categoryId) {

                conceptService.delete(categoryId);

                return ApiResponse.success(
                                "Categoría eliminada",
                                null);
        }

}