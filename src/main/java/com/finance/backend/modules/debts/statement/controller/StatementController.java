// src/main/java/com/finance/backend/modules/debts/statement/controller/StatementController.java

package com.finance.backend.modules.debts.statement.controller;

import com.finance.backend.dto.ApiResponse;
import com.finance.backend.modules.debts.statement.dto.CreateStatementRequest;
import com.finance.backend.modules.debts.statement.dto.StatementDateSuggestionResponse;
import com.finance.backend.modules.debts.statement.dto.StatementResponse;
import com.finance.backend.modules.debts.statement.dto.UpdateStatementRequest;
import com.finance.backend.modules.debts.statement.service.StatementService;
import com.finance.backend.modules.debts.statement.service.StatementExcelExportService;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statements")
public class StatementController {

        private final StatementService statementService;
        private final StatementExcelExportService statementExcelExportService;

        public StatementController(
                        StatementService statementService,
                        StatementExcelExportService statementExcelExportService) {

                this.statementService = statementService;
                this.statementExcelExportService = statementExcelExportService;
        }

        @GetMapping
        public ApiResponse<List<StatementResponse>> findAll(
                        Authentication authentication) {

                return ApiResponse.success(
                                statementService.findAll(
                                                authentication.getName()));
        }

        @GetMapping("/{statementId}")
        public ApiResponse<StatementResponse> findById(
                        @PathVariable Long statementId,
                        Authentication authentication) {

                return ApiResponse.success(
                                statementService.findById(
                                                statementId,
                                                authentication.getName()));
        }

        @GetMapping("/{statementId}/export")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<byte[]> exportExcel(
                        @PathVariable Long statementId,
                        Authentication authentication) {

                var file = statementExcelExportService.export(
                                statementId,
                                authentication.getName());

                return ResponseEntity.ok()
                                .contentType(MediaType.parseMediaType(
                                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                                .header(
                                                HttpHeaders.CONTENT_DISPOSITION,
                                                ContentDisposition.attachment()
                                                                .filename(file.filename())
                                                                .build()
                                                                .toString())
                                .contentLength(file.content().length)
                                .body(file.content());
        }

        @GetMapping("/user-card/{userCardId}")
        public ApiResponse<List<StatementResponse>> findByUserCardId(
                        @PathVariable Long userCardId,
                        Authentication authentication) {

                return ApiResponse.success(
                                statementService.findByUserCardId(
                                                userCardId,
                                                authentication.getName()));
        }

        @PostMapping
        public ResponseEntity<ApiResponse<StatementResponse>> create(
                        Authentication authentication,
                        @Valid @RequestBody CreateStatementRequest request) {

                StatementResponse statement = statementService.create(
                                request,
                                authentication.getName());

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(
                                                ApiResponse.success(
                                                                "Estado de cuenta creado",
                                                                statement));
        }

        @PutMapping("/{statementId}")
        public ApiResponse<StatementResponse> update(
                        @PathVariable Long statementId,
                        Authentication authentication,
                        @Valid @RequestBody UpdateStatementRequest request) {

                return ApiResponse.success(
                                "Estado de cuenta actualizado",
                                statementService.update(
                                                statementId,
                                                request,
                                                authentication.getName()));
        }

        @PatchMapping("/user-card/{userCardId}/pay-all")
        public ApiResponse<List<StatementResponse>> payAll(
                        @PathVariable Long userCardId,
                        Authentication authentication) {

                return ApiResponse.success(
                                "Todos los periodos fueron marcados como pagados",
                                statementService.payAll(
                                                userCardId,
                                                authentication.getName()));
        }

        @DeleteMapping("/{statementId}")
        public ApiResponse<Void> delete(
                        @PathVariable Long statementId,
                        Authentication authentication) {

                statementService.delete(
                                statementId,
                                authentication.getName());

                return ApiResponse.success(
                                "Estado de cuenta eliminado",
                                null);
        }

        @GetMapping("/suggestion")
        public ApiResponse<StatementDateSuggestionResponse> getDateSuggestion(
                        @RequestParam Long userCardId,
                        Authentication authentication) {

                return ApiResponse.success(
                                statementService.getDateSuggestion(
                                                userCardId,
                                                authentication.getName()));
        }
}
