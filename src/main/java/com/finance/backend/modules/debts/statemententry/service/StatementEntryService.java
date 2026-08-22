package com.finance.backend.modules.debts.statemententry.service;

import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.modules.debts.concept.model.Concept;
import com.finance.backend.modules.debts.concept.repository.ConceptRepository;
import com.finance.backend.modules.debts.statement.model.Statement;
import com.finance.backend.modules.debts.statement.repository.StatementRepository;
import com.finance.backend.modules.debts.statemententry.dto.CreateStatementEntryRequest;
import com.finance.backend.modules.debts.statemententry.dto.StatementEntryResponse;
import com.finance.backend.modules.debts.statemententry.dto.UpdateStatementEntryRequest;
import com.finance.backend.modules.debts.statemententry.mapper.StatementEntryMapper;
import com.finance.backend.modules.debts.statemententry.model.StatementEntry;
import com.finance.backend.modules.debts.statemententry.repository.StatementEntryRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StatementEntryService {

        private final StatementEntryRepository statementEntryRepository;
        private final StatementRepository statementRepository;
        private final ConceptRepository conceptRepository;

        public StatementEntryService(
                        StatementEntryRepository statementEntryRepository,
                        StatementRepository statementRepository,
                        ConceptRepository conceptRepository) {

                this.statementEntryRepository = statementEntryRepository;
                this.statementRepository = statementRepository;
                this.conceptRepository = conceptRepository;
        }

        @Transactional(readOnly = true)
        public List<StatementEntryResponse> findAll(
                        String email) {

                return statementEntryRepository
                                .findByStatementCardUserEmailIgnoreCase(
                                                email)
                                .stream()
                                .map(StatementEntryMapper::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public StatementEntryResponse findById(
                        Long entryId,
                        String email) {

                return StatementEntryMapper.toResponse(
                                getOwnedEntry(
                                                entryId,
                                                email));
        }

        @Transactional(readOnly = true)
        public List<StatementEntryResponse> findByStatementId(
                        Long statementId,
                        String email) {

                getOwnedStatement(
                                statementId,
                                email);

                return statementEntryRepository
                                .findByStatementStatementIdAndStatementCardUserEmailIgnoreCase(
                                                statementId,
                                                email)
                                .stream()
                                .map(StatementEntryMapper::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public List<StatementEntryResponse> findByDebtor(
                        String debtor,
                        String email) {

                return statementEntryRepository
                                .findByDebtorAndStatementCardUserEmailIgnoreCase(
                                                debtor,
                                                email)
                                .stream()
                                .map(StatementEntryMapper::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public List<StatementEntryResponse> findByStatementIdAndDebtor(
                        Long statementId,
                        String debtor,
                        String email) {

                getOwnedStatement(
                                statementId,
                                email);

                return statementEntryRepository
                                .findByStatementStatementIdAndDebtorAndStatementCardUserEmailIgnoreCase(
                                                statementId,
                                                debtor,
                                                email)
                                .stream()
                                .map(StatementEntryMapper::toResponse)
                                .toList();
        }

        @Transactional
        public StatementEntryResponse create(
                        CreateStatementEntryRequest request,
                        String email) {

                Statement statement = getOwnedStatement(
                                request.statementId(),
                                email);

                Concept concept = getConcept(
                                request.conceptId());

                StatementEntry entry = StatementEntryMapper.toEntity(
                                request,
                                statement,
                                concept);

                StatementEntry savedEntry = statementEntryRepository.save(
                                entry);

                return StatementEntryMapper.toResponse(
                                savedEntry);
        }

        @Transactional
        public StatementEntryResponse update(
                        Long entryId,
                        UpdateStatementEntryRequest request,
                        String email) {

                StatementEntry entry = getOwnedEntry(
                                entryId,
                                email);

                Statement statement = getOwnedStatement(
                                request.statementId(),
                                email);

                Concept concept = getConcept(
                                request.conceptId());

                StatementEntryMapper.updateEntity(
                                entry,
                                request,
                                statement,
                                concept);

                StatementEntry updatedEntry = statementEntryRepository.save(
                                entry);

                return StatementEntryMapper.toResponse(
                                updatedEntry);
        }

        @Transactional
        public void delete(
                        Long entryId,
                        String email) {

                StatementEntry entry = getOwnedEntry(
                                entryId,
                                email);

                statementEntryRepository.delete(
                                entry);
        }

        private StatementEntry getOwnedEntry(
                        Long entryId,
                        String email) {

                return statementEntryRepository
                                .findByEntryIdAndStatementCardUserEmailIgnoreCase(
                                                entryId,
                                                email)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Movimiento no encontrado"));
        }

        private Statement getOwnedStatement(
                        Long statementId,
                        String email) {

                return statementRepository
                                .findByStatementIdAndCardUserEmailIgnoreCase(
                                                statementId,
                                                email)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Estado de cuenta no encontrado"));
        }

        private Concept getConcept(
                        Long conceptId) {

                return conceptRepository
                                .findById(conceptId)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Concepto no encontrado"));
        }
}