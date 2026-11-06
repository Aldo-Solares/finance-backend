package com.finance.backend.modules.etl.service;

import com.finance.backend.exception.BadRequestException;
import com.finance.backend.modules.debts.statemententry.dto.CreateStatementEntryRequest;
import com.finance.backend.modules.debts.statemententry.dto.UpdateStatementEntryRequest;
import com.finance.backend.modules.debts.statemententry.dto.StatementEntryResponse;
import com.finance.backend.modules.debts.statemententry.model.StatementEntry;
import com.finance.backend.modules.debts.statemententry.repository.StatementEntryRepository;
import com.finance.backend.modules.debts.statemententry.service.StatementEntryService;
import com.finance.backend.modules.etl.dto.EtlStatementEntriesImportRequest;
import com.finance.backend.modules.etl.dto.EtlStatementEntryImportRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class EtlStatementImportService {

    private final StatementEntryService statementEntryService;
    private final StatementEntryRepository statementEntryRepository;

    public EtlStatementImportService(
            StatementEntryService statementEntryService,
            StatementEntryRepository statementEntryRepository) {
        this.statementEntryService = statementEntryService;
        this.statementEntryRepository = statementEntryRepository;
    }

    @Transactional
    public List<StatementEntryResponse> importStatementEntries(
            EtlStatementEntriesImportRequest request,
            String email) {

        Set<String> importKeys = new HashSet<>();
        Long statementId = request.entries().get(0).statementId();

        for (EtlStatementEntryImportRequest entry : request.entries()) {
            if (!statementId.equals(entry.statementId())) {
                throw new BadRequestException(
                        "Una importación debe contener movimientos de un solo estado de cuenta");
            }

            if (!importKeys.add(entry.importKey())) {
                throw new BadRequestException(
                        "La importación contiene import_key duplicados");
            }
        }

        return request.entries().stream()
                .map(entry -> importEntry(entry, email))
                .toList();
    }

    private StatementEntryResponse importEntry(
            EtlStatementEntryImportRequest entry,
            String email) {

        StatementEntry existing = statementEntryRepository
                .findByImportKey(entry.importKey())
                .orElse(null);

        StatementEntryResponse response;
        if (existing == null) {
            response = statementEntryService.create(toCreateRequest(entry), email);
        } else {
            response = statementEntryService.update(
                    existing.getEntryId(),
                    toUpdateRequest(entry),
                    email);
        }

        StatementEntry savedEntry = statementEntryRepository.findById(response.entryId())
                .orElseThrow(() -> new IllegalStateException(
                        "El movimiento importado no se encontró después de guardarlo"));
        savedEntry.setImportKey(entry.importKey());
        statementEntryRepository.save(savedEntry);
        return response;
    }

    private CreateStatementEntryRequest toCreateRequest(
            EtlStatementEntryImportRequest entry) {

        return new CreateStatementEntryRequest(
                entry.statementId(),
                entry.categoryId(),
                entry.debtor(),
                entry.description(),
                entry.merchant(),
                entry.notes(),
                entry.date(),
                entry.amount(),
                entry.paid(),
                entry.monthlyPaymentNumber(),
                entry.totalMonthlyPayments());
    }

    private UpdateStatementEntryRequest toUpdateRequest(
            EtlStatementEntryImportRequest entry) {

        return new UpdateStatementEntryRequest(
                entry.statementId(),
                entry.categoryId(),
                entry.debtor(),
                entry.description(),
                entry.merchant(),
                entry.notes(),
                entry.date(),
                entry.amount(),
                entry.paid(),
                entry.monthlyPaymentNumber(),
                entry.totalMonthlyPayments());
    }
}
