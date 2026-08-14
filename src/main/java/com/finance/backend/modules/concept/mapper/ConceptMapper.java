package com.finance.backend.modules.concept.mapper;

import com.finance.backend.modules.concept.dto.ConceptResponse;
import com.finance.backend.modules.concept.dto.CreateConceptRequest;
import com.finance.backend.modules.concept.dto.UpdateConceptRequest;
import com.finance.backend.modules.concept.model.Concept;

public final class ConceptMapper {

    private ConceptMapper() {
    }

    public static Concept toEntity(
            CreateConceptRequest request) {
        Concept concept = new Concept();

        concept.setName(
                request.name().trim());

        return concept;
    }

    public static void updateEntity(
            Concept concept,
            UpdateConceptRequest request) {
        concept.setName(
                request.name().trim());
    }

    public static ConceptResponse toResponse(
            Concept concept) {
        return new ConceptResponse(
                concept.getConceptId(),
                concept.getName());
    }
}