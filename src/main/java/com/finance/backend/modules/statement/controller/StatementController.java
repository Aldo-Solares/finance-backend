package com.finance.backend.modules.statement.controller;

import com.finance.backend.dto.ApiResponse;
import com.finance.backend.modules.statement.dto.CreateStatementRequest;
import com.finance.backend.modules.statement.dto.StatementResponse;
import com.finance.backend.modules.statement.dto.UpdateStatementRequest;
import com.finance.backend.modules.statement.service.StatementService;
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

    @DeleteMapping("/{statementId}")
    public ApiResponse<Void> delete(
            @PathVariable Long statementId) {
        statementService.delete(statementId);

        return ApiResponse.success(
                "Estado de cuenta eliminado",
                null);
    }
}