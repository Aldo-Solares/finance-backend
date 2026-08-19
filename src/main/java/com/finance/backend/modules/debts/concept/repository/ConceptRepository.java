package com.finance.backend.modules.debts.concept.repository;

import com.finance.backend.modules.debts.concept.model.Concept;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConceptRepository
        extends JpaRepository<Concept, Long> {

    boolean existsByNameIgnoreCase(String name);
}