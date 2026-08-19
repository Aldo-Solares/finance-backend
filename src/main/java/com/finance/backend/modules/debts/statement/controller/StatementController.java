package com.finance.backend.modules.debts.statement.controller;

import com.finance.backend.dto.ApiResponse;
import com.finance.backend.modules.debts.statement.dto.CreateStatementRequest;
import com.finance.backend.modules.debts.statement.dto.PayAheadRequest;
import com.finance.backend.modules.debts.statement.dto.StatementResponse;
import com.finance.backend.modules.debts.statement.dto.UpdateStatementPaidRequest;
import com.finance.backend.modules.debts.statement.dto.UpdateStatementRequest;
import com.finance.backend.modules.debts.statement.service.StatementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statements")
public class StatementController {

        private final StatementService statementService;

        public StatementController(
                        StatementService statementService) {
                this.statementService = statementService;
        }

        @GetMapping
        public ApiResponse<List<StatementResponse>> findAll() {
                return ApiResponse.success(
                                statementService.findAll());
        }

        @GetMapping("/{statementId}")
        public ApiResponse<StatementResponse> findById(
                        @PathVariable Long statementId) {
                return ApiResponse.success(
                                statementService.findById(statementId));
        }

        @GetMapping("/card/{cardId}")
        public ApiResponse<List<StatementResponse>> findByCardId(
                        @PathVariable Long cardId) {
                return ApiResponse.success(
                                statementService.findByCardId(cardId));
        }

        @PostMapping
        public ResponseEntity<ApiResponse<StatementResponse>> create(
                        @Valid @RequestBody CreateStatementRequest request) {
                StatementResponse statement = statementService.create(request);

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
                        @Valid @RequestBody UpdateStatementRequest request) {
                return ApiResponse.success(
                                "Estado de cuenta actualizado",
                                statementService.update(
                                                statementId,
                                                request));
        }

        @PatchMapping("/{statementId}/paid")
        public ApiResponse<StatementResponse> updatePaid(
                        @PathVariable Long statementId,
                        @Valid @RequestBody UpdateStatementPaidRequest request) {
                return ApiResponse.success(
                                "Estado de pago actualizado",
                                statementService.updatePaid(
                                                statementId,
                                                request.paid()));
        }

        @PatchMapping("/{statementId}/pay-ahead")
        public ApiResponse<List<StatementResponse>> payAhead(
                        @PathVariable Long statementId,
                        @Valid @RequestBody PayAheadRequest request) {
                return ApiResponse.success(
                                "Periodos pagados correctamente",
                                statementService.payAhead(
                                                statementId,
                                                request.months()));
        }

        @PatchMapping("/card/{cardId}/pay-all")
        public ApiResponse<List<StatementResponse>> payAll(
                        @PathVariable Long cardId) {
                return ApiResponse.success(
                                "Todos los periodos fueron marcados como pagados",
                                statementService.payAll(cardId));
        }

        @DeleteMapping("/{statementId}")
        public ApiResponse<Void> delete(
                        @PathVariable Long statementId) {
                statementService.delete(statementId);

                return ApiResponse.success(
                                "Estado de cuenta eliminado",
                                null);
        }
}