package com.finance.backend.modules.debts.concept.service;

import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.modules.debts.concept.dto.ConceptResponse;
import com.finance.backend.modules.debts.concept.dto.CreateConceptRequest;
import com.finance.backend.modules.debts.concept.dto.UpdateConceptRequest;
import com.finance.backend.modules.debts.concept.mapper.ConceptMapper;
import com.finance.backend.modules.debts.concept.model.Concept;
import com.finance.backend.modules.debts.concept.repository.ConceptRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConceptService {

        private final ConceptRepository conceptRepository;

        public ConceptService(
                        ConceptRepository conceptRepository) {
                this.conceptRepository = conceptRepository;
        }

        public List<ConceptResponse> findAll() {
                return conceptRepository
                                .findAll()
                                .stream()
                                .map(ConceptMapper::toResponse)
                                .toList();
        }

        public ConceptResponse findById(
                        Long conceptId) {
                return ConceptMapper.toResponse(
                                getConcept(conceptId));
        }

        public ConceptResponse create(
                        CreateConceptRequest request) {
                Concept concept = ConceptMapper.toEntity(request);

                Concept savedConcept = conceptRepository.save(concept);

                return ConceptMapper.toResponse(
                                savedConcept);
        }

        public ConceptResponse update(
                        Long conceptId,
                        UpdateConceptRequest request) {
                Concept concept = getConcept(conceptId);

                ConceptMapper.updateEntity(
                                concept,
                                request);

                Concept updatedConcept = conceptRepository.save(concept);

                return ConceptMapper.toResponse(
                                updatedConcept);
        }

        public void delete(
                        Long conceptId) {
                Concept concept = getConcept(conceptId);

                conceptRepository.delete(concept);
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