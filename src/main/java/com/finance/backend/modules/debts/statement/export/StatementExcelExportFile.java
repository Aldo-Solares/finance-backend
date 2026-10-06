package com.finance.backend.modules.debts.statement.export;

public record StatementExcelExportFile(String filename, byte[] content) {
}
