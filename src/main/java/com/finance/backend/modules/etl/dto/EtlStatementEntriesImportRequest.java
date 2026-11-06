package com.finance.backend.modules.etl.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record EtlStatementEntriesImportRequest(
        @NotEmpty List<@Valid EtlStatementEntryImportRequest> entries) {
}
