// src/main/java/com/finance/backend/modules/dashboard/debts/specification/StatementEntrySpecification.java

package com.finance.backend.modules.dashboard.debts.specification;

import com.finance.backend.modules.debts.statement.model.StatementStatus;
import com.finance.backend.modules.debts.statemententry.model.StatementEntry;
import org.springframework.data.jpa.domain.Specification;

public final class StatementEntrySpecification {

    private StatementEntrySpecification() {
    }

    public static Specification<StatementEntry> belongsToUser(
            String email) {

        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(
                criteriaBuilder.lower(
                        root
                                .get("statement")
                                .get("userCard")
                                .get("user")
                                .get("email")),
                email.toLowerCase());
    }

    public static Specification<StatementEntry> hasYear(
            Integer year) {

        return (root, query, criteriaBuilder) -> {

            if (year == null || year == 0) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root
                            .get("statement")
                            .get("year"),
                    year);
        };
    }

    public static Specification<StatementEntry> hasMonth(
            Integer month) {

        return (root, query, criteriaBuilder) -> {

            if (month == null || month == 0) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root
                            .get("statement")
                            .get("month"),
                    month);
        };
    }

    public static Specification<StatementEntry> hasUserCardId(
            Long userCardId) {

        return (root, query, criteriaBuilder) -> {

            if (userCardId == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root
                            .get("statement")
                            .get("userCard")
                            .get("userCardId"),
                    userCardId);
        };
    }

    public static Specification<StatementEntry> hasConceptId(
            Long conceptId) {

        return (root, query, criteriaBuilder) -> {

            if (conceptId == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root
                            .get("concept")
                            .get("conceptId"),
                    conceptId);
        };
    }

    public static Specification<StatementEntry> hasPaid(
            Boolean paid) {

        return (root, query, criteriaBuilder) -> {

            if (paid == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("paid"),
                    paid);
        };
    }

    public static Specification<StatementEntry> hasStatementStatus(
            StatementStatus status) {

        return (root, query, criteriaBuilder) -> {

            if (status == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root
                            .get("statement")
                            .get("status"),
                    status);
        };
    }

    public static Specification<StatementEntry> hasDebtor(
            String debtor) {

        return (root, query, criteriaBuilder) -> {

            if (debtor == null || debtor.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    criteriaBuilder.lower(
                            root.get("debtor")),
                    debtor
                            .trim()
                            .toLowerCase());
        };
    }
}