package com.finance.backend.modules.statemententry.controller;

import com.finance.backend.dto.ApiResponse;
import com.finance.backend.modules.statemententry.dto.CreateStatementEntryRequest;
import com.finance.backend.modules.statemententry.dto.StatementEntryResponse;
import com.finance.backend.modules.statemententry.dto.UpdateStatementEntryRequest;
import com.finance.backend.modules.statemententry.service.StatementEntryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statement-entries")
public class StatementEntryController {

        private final StatementEntryService statementEntryService;

        public StatementEntryController(
                        StatementEntryService statementEntryService) {
                this.statementEntryService = statementEntryService;
        }

        @GetMapping
        public ApiResponse<List<StatementEntryResponse>> findAll() {
                return ApiResponse.success(
                                statementEntryService.findAll());
        }

        @GetMapping("/{entryId}")
        public ApiResponse<StatementEntryResponse> findById(
                        @PathVariable Long entryId) {
                return ApiResponse.success(
                                statementEntryService.findById(entryId));
        }

        @GetMapping("/statement/{statementId}")
        public ApiResponse<List<StatementEntryResponse>> findByStatementId(
                        @PathVariable Long statementId) {
                return ApiResponse.success(
                                statementEntryService.findByStatementId(statementId));
        }

        @GetMapping("/user/{userId}")
        public ApiResponse<List<StatementEntryResponse>> findByUserId(
                        @PathVariable Long userId) {
                return ApiResponse.success(
                                statementEntryService.findByUserId(userId));
        }

        @PostMapping
        public ResponseEntity<ApiResponse<StatementEntryResponse>> create(
                        @Valid @RequestBody CreateStatementEntryRequest request) {
                StatementEntryResponse entry = statementEntryService.create(request);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(
                                                ApiResponse.success(
                                                                "Movimiento creado",
                                                                entry));
        }

        @PutMapping("/{entryId}")
        public ApiResponse<StatementEntryResponse> update(
                        @PathVariable Long entryId,
                        @Valid @RequestBody UpdateStatementEntryRequest request) {
                return ApiResponse.success(
                                "Movimiento actualizado",
                                statementEntryService.update(
                                                entryId,
                                                request));
        }

        @DeleteMapping("/{entryId}")
        public ApiResponse<Void> delete(
                        @PathVariable Long entryId) {
                statementEntryService.delete(entryId);

                return ApiResponse.success(
                                "Movimiento eliminado",
                                null);
        }
}