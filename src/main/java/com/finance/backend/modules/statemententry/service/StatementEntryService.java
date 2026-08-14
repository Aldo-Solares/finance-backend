package com.finance.backend.modules.statemententry.service;

import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.modules.concept.model.Concept;
import com.finance.backend.modules.concept.repository.ConceptRepository;
import com.finance.backend.modules.statemententry.dto.CreateStatementEntryRequest;
import com.finance.backend.modules.statemententry.dto.StatementEntryResponse;
import com.finance.backend.modules.statemententry.dto.UpdateStatementEntryRequest;
import com.finance.backend.modules.statemententry.mapper.StatementEntryMapper;
import com.finance.backend.modules.statemententry.model.StatementEntry;
import com.finance.backend.modules.statemententry.repository.StatementEntryRepository;
import com.finance.backend.modules.statement.model.Statement;
import com.finance.backend.modules.statement.repository.StatementRepository;
import com.finance.backend.modules.user.model.User;
import com.finance.backend.modules.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatementEntryService {

        private final StatementEntryRepository statementEntryRepository;
        private final StatementRepository statementRepository;
        private final ConceptRepository conceptRepository;
        private final UserRepository userRepository;

        public StatementEntryService(
                        StatementEntryRepository statementEntryRepository,
                        StatementRepository statementRepository,
                        ConceptRepository conceptRepository,
                        UserRepository userRepository) {
                this.statementEntryRepository = statementEntryRepository;
                this.statementRepository = statementRepository;
                this.conceptRepository = conceptRepository;
                this.userRepository = userRepository;
        }

        public List<StatementEntryResponse> findAll() {
                return statementEntryRepository
                                .findAll()
                                .stream()
                                .map(StatementEntryMapper::toResponse)
                                .toList();
        }

        public StatementEntryResponse findById(
                        Long entryId) {
                return StatementEntryMapper.toResponse(
                                getEntry(entryId));
        }

        public List<StatementEntryResponse> findByStatementId(
                        Long statementId) {
                getStatement(statementId);

                return statementEntryRepository
                                .findByStatementStatementId(statementId)
                                .stream()
                                .map(StatementEntryMapper::toResponse)
                                .toList();
        }

        public List<StatementEntryResponse> findByUserId(
                        Long userId) {
                getUser(userId);

                return statementEntryRepository
                                .findByUserUserId(userId)
                                .stream()
                                .map(StatementEntryMapper::toResponse)
                                .toList();
        }

        public StatementEntryResponse create(
                        CreateStatementEntryRequest request) {
                Statement statement = getStatement(request.statementId());

                Concept concept = getConcept(request.conceptId());

                User user = getUser(request.userId());

                StatementEntry entry = StatementEntryMapper.toEntity(
                                request,
                                statement,
                                concept,
                                user);

                StatementEntry savedEntry = statementEntryRepository.save(entry);

                return StatementEntryMapper.toResponse(
                                savedEntry);
        }

        public StatementEntryResponse update(
                        Long entryId,
                        UpdateStatementEntryRequest request) {
                StatementEntry entry = getEntry(entryId);

                Statement statement = getStatement(request.statementId());

                Concept concept = getConcept(request.conceptId());

                User user = getUser(request.userId());

                StatementEntryMapper.updateEntity(
                                entry,
                                request,
                                statement,
                                concept,
                                user);

                StatementEntry updatedEntry = statementEntryRepository.save(entry);

                return StatementEntryMapper.toResponse(
                                updatedEntry);
        }

        public void delete(
                        Long entryId) {
                StatementEntry entry = getEntry(entryId);

                statementEntryRepository.delete(entry);
        }

        private StatementEntry getEntry(
                        Long entryId) {
                return statementEntryRepository
                                .findById(entryId)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Movimiento no encontrado"));
        }

        private Statement getStatement(
                        Long statementId) {
                return statementRepository
                                .findById(statementId)
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

        private User getUser(
                        Long userId) {
                return userRepository
                                .findById(userId)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Usuario no encontrado"));
        }
}