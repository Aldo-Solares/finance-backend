package com.finance.backend.modules.debts.statement.service;

import com.finance.backend.modules.debts.statement.dto.StatementResponse;
import com.finance.backend.modules.debts.statement.export.StatementExcelExportFile;
import com.finance.backend.modules.debts.statement.export.StatementExcelWorkbookGenerator;
import com.finance.backend.modules.debts.statemententry.dto.StatementEntryResponse;
import com.finance.backend.modules.debts.statemententry.service.StatementEntryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class StatementExcelExportService {

    private final StatementService statementService;
    private final StatementEntryService statementEntryService;
    private final StatementExcelWorkbookGenerator workbookGenerator;

    public StatementExcelExportService(
            StatementService statementService,
            StatementEntryService statementEntryService,
            StatementExcelWorkbookGenerator workbookGenerator) {

        this.statementService = statementService;
        this.statementEntryService = statementEntryService;
        this.workbookGenerator = workbookGenerator;
    }

    public StatementExcelExportFile export(Long statementId, String email) {
        StatementResponse anchor = statementService.findById(statementId, email);
        List<StatementResponse> statements = statementService
                .findByUserCardId(anchor.userCardId(), email)
                .stream()
                .filter(statement -> anchor.year().equals(statement.year()))
                .toList();

        List<StatementEntryResponse> entries = new ArrayList<>();
        for (StatementResponse statement : statements) {
            entries.addAll(statementEntryService.findByStatementId(statement.statementId(), email));
        }

        byte[] content = workbookGenerator.generate(statements, entries);
        return new StatementExcelExportFile(
                filename(anchor.cardName(), anchor.year()),
                content);
    }

    private static String filename(String cardName, Integer year) {
        int fileYear = year;
        String prefix = fileYear < LocalDate.now().getYear() ? "PAGADA_" : "Deudas_";
        String cardPart = sanitizeCardName(cardName);
        return prefix + cardPart + "_" + fileYear + ".xlsx";
    }

    private static String sanitizeCardName(String cardName) {
        String normalized = Normalizer.normalize(
                cardName == null || cardName.isBlank() ? "Tarjeta" : cardName,
                Normalizer.Form.NFD);
        String safeName = normalized
                .replaceAll("\\p{M}+", "")
                .replaceAll("[^\\p{Alnum}]+", "_")
                .replaceAll("^_+|_+$", "");
        return safeName.isBlank() ? "Tarjeta" : safeName;
    }
}
