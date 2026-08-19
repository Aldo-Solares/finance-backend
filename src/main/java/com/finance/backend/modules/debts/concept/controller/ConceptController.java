package com.finance.backend.modules.debts.concept.controller;

import com.finance.backend.dto.ApiResponse;
import com.finance.backend.modules.debts.concept.dto.ConceptResponse;
import com.finance.backend.modules.debts.concept.dto.CreateConceptRequest;
import com.finance.backend.modules.debts.concept.dto.UpdateConceptRequest;
import com.finance.backend.modules.debts.concept.service.ConceptService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/concepts")
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

        @GetMapping("/{conceptId}")
        public ApiResponse<ConceptResponse> findById(
                        @PathVariable Long conceptId) {
                return ApiResponse.success(
                                conceptService.findById(conceptId));
        }

        @PostMapping
        public ResponseEntity<ApiResponse<ConceptResponse>> create(
                        @Valid @RequestBody CreateConceptRequest request) {
                ConceptResponse concept = conceptService.create(request);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(
                                                ApiResponse.success(
                                                                "Concepto creado",
                                                                concept));
        }

        @PutMapping("/{conceptId}")
        public ApiResponse<ConceptResponse> update(
                        @PathVariable Long conceptId,
                        @Valid @RequestBody UpdateConceptRequest request) {
                return ApiResponse.success(
                                "Concepto actualizado",
                                conceptService.update(
                                                conceptId,
                                                request));
        }

        @DeleteMapping("/{conceptId}")
        public ApiResponse<Void> delete(
                        @PathVariable Long conceptId) {
                conceptService.delete(conceptId);

                return ApiResponse.success(
                                "Concepto eliminado",
                                null);
        }
}