package com.finance.backend.modules.etl.controller;

import com.finance.backend.dto.ApiResponse;
import com.finance.backend.modules.debts.statemententry.dto.StatementEntryResponse;
import com.finance.backend.modules.etl.dto.EtlStatementEntriesImportRequest;
import com.finance.backend.modules.etl.service.EtlStatementImportService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/etl/statement-entries")
@PreAuthorize("hasRole('ADMIN')")
public class EtlStatementImportController {

    private final EtlStatementImportService etlStatementImportService;

    public EtlStatementImportController(
            EtlStatementImportService etlStatementImportService) {
        this.etlStatementImportService = etlStatementImportService;
    }

    @PostMapping("/import")
    public ApiResponse<List<StatementEntryResponse>> importStatementEntries(
            @Valid @RequestBody EtlStatementEntriesImportRequest request,
            Authentication authentication) {

        List<StatementEntryResponse> entries = etlStatementImportService
                .importStatementEntries(request, authentication.getName());

        return ApiResponse.success("Movimientos del ETL sincronizados", entries);
    }
}
