package com.finance.backend.modules.concept.repository;

import com.finance.backend.modules.concept.model.Concept;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConceptRepository
        extends JpaRepository<Concept, Long> {

    boolean existsByNameIgnoreCase(String name);
}