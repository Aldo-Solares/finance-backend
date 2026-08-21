// src/main/java/com/finance/backend/modules/debts/statemententry/controller/StatementEntryController.java

package com.finance.backend.modules.debts.statemententry.controller;

import com.finance.backend.dto.ApiResponse;
import com.finance.backend.modules.debts.statemententry.dto.CreateStatementEntryRequest;
import com.finance.backend.modules.debts.statemententry.dto.StatementEntryResponse;
import com.finance.backend.modules.debts.statemententry.dto.UpdateStatementEntryRequest;
import com.finance.backend.modules.debts.statemententry.service.StatementEntryService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
        public ApiResponse<List<StatementEntryResponse>> findAll(
                        Authentication authentication) {

                return ApiResponse.success(
                                statementEntryService.findAll(
                                                authentication.getName()));
        }

        @GetMapping("/{entryId}")
        public ApiResponse<StatementEntryResponse> findById(
                        @PathVariable Long entryId,
                        Authentication authentication) {

                return ApiResponse.success(
                                statementEntryService.findById(
                                                entryId,
                                                authentication.getName()));
        }

        @GetMapping("/statement/{statementId}")
        public ApiResponse<List<StatementEntryResponse>> findByStatementId(
                        @PathVariable Long statementId,
                        Authentication authentication) {

                return ApiResponse.success(
                                statementEntryService.findByStatementId(
                                                statementId,
                                                authentication.getName()));
        }

        @GetMapping("/debtor/{debtor}")
        public ApiResponse<List<StatementEntryResponse>> findByDebtor(
                        @PathVariable String debtor,
                        Authentication authentication) {

                return ApiResponse.success(
                                statementEntryService.findByDebtor(
                                                debtor,
                                                authentication.getName()));
        }

        @GetMapping("/statement/{statementId}/debtor/{debtor}")
        public ApiResponse<List<StatementEntryResponse>> findByStatementIdAndDebtor(
                        @PathVariable Long statementId,
                        @PathVariable String debtor,
                        Authentication authentication) {

                return ApiResponse.success(
                                statementEntryService.findByStatementIdAndDebtor(
                                                statementId,
                                                debtor,
                                                authentication.getName()));
        }

        @PostMapping
        public ResponseEntity<ApiResponse<StatementEntryResponse>> create(
                        Authentication authentication,
                        @Valid @RequestBody CreateStatementEntryRequest request) {

                StatementEntryResponse entry = statementEntryService.create(
                                request,
                                authentication.getName());

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
                        Authentication authentication,
                        @Valid @RequestBody UpdateStatementEntryRequest request) {

                return ApiResponse.success(
                                "Movimiento actualizado",
                                statementEntryService.update(
                                                entryId,
                                                request,
                                                authentication.getName()));
        }

        @DeleteMapping("/{entryId}")
        public ApiResponse<Void> delete(
                        @PathVariable Long entryId,
                        Authentication authentication) {

                statementEntryService.delete(
                                entryId,
                                authentication.getName());

                return ApiResponse.success(
                                "Movimiento eliminado",
                                null);
        }
}