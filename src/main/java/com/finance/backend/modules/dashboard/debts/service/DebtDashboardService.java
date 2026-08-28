package com.finance.backend.modules.dashboard.debts.service;

import com.finance.backend.modules.dashboard.debts.dto.DebtDashboardCardResponse;
import com.finance.backend.modules.dashboard.debts.dto.DebtDashboardConceptResponse;
import com.finance.backend.modules.dashboard.debts.dto.DebtDashboardFilter;
import com.finance.backend.modules.dashboard.debts.dto.DebtDashboardResponse;
import com.finance.backend.modules.dashboard.debts.dto.DebtDashboardStatementResponse;
import com.finance.backend.modules.dashboard.debts.specification.StatementEntrySpecification;
import com.finance.backend.modules.debts.card.model.Card;
import com.finance.backend.modules.debts.concept.model.Concept;
import com.finance.backend.modules.debts.statement.model.Statement;
import com.finance.backend.modules.debts.statemententry.model.StatementEntry;
import com.finance.backend.modules.debts.statemententry.repository.StatementEntryRepository;
import com.finance.backend.modules.debts.usercard.model.UserCard;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DebtDashboardService {

        private static final BigDecimal ZERO = BigDecimal.ZERO;

        private static final BigDecimal HUNDRED = new BigDecimal("100");

        private static final int MONEY_SCALE = 2;

        private static final int PERCENTAGE_SCALE = 2;

        private final StatementEntryRepository statementEntryRepository;

        public DebtDashboardService(
                        StatementEntryRepository statementEntryRepository) {

                this.statementEntryRepository = statementEntryRepository;
        }

        @Transactional(readOnly = true)
        public DebtDashboardResponse getDashboard(
                        DebtDashboardFilter filter,
                        String email) {

                DebtDashboardFilter resolvedFilter = resolveFilter(filter);

                List<StatementEntry> entries = findFilteredEntries(
                                resolvedFilter,
                                email);

                BigDecimal totalExpenses = calculateTotalExpenses(
                                entries);

                BigDecimal totalPaid = calculateTotalByPaid(
                                entries,
                                true);

                BigDecimal totalPending = calculateTotalByPaid(
                                entries,
                                false);

                long totalEntries = entries.size();

                BigDecimal averageExpense = calculateAverageExpense(
                                totalExpenses,
                                totalEntries);

                List<DebtDashboardCardResponse> cards = buildCards(
                                entries,
                                totalExpenses);

                List<DebtDashboardConceptResponse> concepts = buildConcepts(
                                entries,
                                totalExpenses);

                List<DebtDashboardStatementResponse> statements = buildStatements(
                                entries);

                return new DebtDashboardResponse(
                                resolvedFilter.year(),
                                resolvedFilter.month(),
                                money(totalExpenses),
                                money(totalPaid),
                                money(totalPending),
                                totalEntries,
                                money(averageExpense),
                                cards,
                                concepts,
                                statements);
        }

        private List<StatementEntry> findFilteredEntries(
                        DebtDashboardFilter filter,
                        String email) {

                Specification<StatementEntry> specification = Specification
                                .where(
                                                StatementEntrySpecification
                                                                .belongsToUser(
                                                                                email))
                                .and(
                                                StatementEntrySpecification
                                                                .hasYear(
                                                                                filter.year()))
                                .and(
                                                StatementEntrySpecification
                                                                .hasMonth(
                                                                                filter.month()))
                                .and(
                                                StatementEntrySpecification
                                                                .hasUserCardId(
                                                                                filter.userCardId()))
                                .and(
                                                StatementEntrySpecification
                                                                .hasConceptId(
                                                                                filter.conceptId()))
                                .and(
                                                StatementEntrySpecification
                                                                .hasPaid(
                                                                                filter.paid()))
                                .and(
                                                StatementEntrySpecification
                                                                .hasStatementStatus(
                                                                                filter.status()))
                                .and(
                                                StatementEntrySpecification
                                                                .hasDebtor(
                                                                                filter.debtor()));

                return statementEntryRepository.findAll(
                                specification);
        }

        private DebtDashboardFilter resolveFilter(
                        DebtDashboardFilter filter) {

                LocalDate today = LocalDate.now();

                Integer year = filter.year() != null
                                ? filter.year()
                                : today.getYear();

                Integer month = filter.month() != null
                                ? filter.month()
                                : today.getMonthValue();

                return new DebtDashboardFilter(
                                year,
                                month,
                                filter.userCardId(),
                                filter.conceptId(),
                                filter.paid(),
                                filter.status(),
                                filter.debtor());
        }

        private BigDecimal calculateTotalExpenses(
                        List<StatementEntry> entries) {

                return entries
                                .stream()
                                .map(
                                                StatementEntry::getAmount)
                                .reduce(
                                                ZERO,
                                                BigDecimal::add);
        }

        private BigDecimal calculateTotalByPaid(
                        List<StatementEntry> entries,
                        boolean paid) {

                return entries
                                .stream()
                                .filter(
                                                entry -> Boolean.TRUE.equals(
                                                                entry.getPaid()) == paid)
                                .map(
                                                StatementEntry::getAmount)
                                .reduce(
                                                ZERO,
                                                BigDecimal::add);
        }

        private BigDecimal calculateAverageExpense(
                        BigDecimal totalExpenses,
                        long totalEntries) {

                if (totalEntries == 0) {
                        return ZERO;
                }

                return totalExpenses.divide(
                                BigDecimal.valueOf(
                                                totalEntries),
                                MONEY_SCALE,
                                RoundingMode.HALF_UP);
        }

        private List<DebtDashboardCardResponse> buildCards(
                        List<StatementEntry> entries,
                        BigDecimal totalExpenses) {

                Map<Long, CardAccumulator> grouped = new LinkedHashMap<>();

                for (StatementEntry entry : entries) {

                        UserCard userCard = entry
                                        .getStatement()
                                        .getUserCard();

                        Card card = userCard.getCard();

                        CardAccumulator accumulator = grouped.computeIfAbsent(
                                        userCard.getUserCardId(),
                                        ignored -> new CardAccumulator(
                                                        userCard.getUserCardId(),
                                                        card.getCardId(),
                                                        card.getBank(),
                                                        card.getCardName()));

                        BigDecimal amount = entry.getAmount();

                        accumulator.totalExpenses = accumulator.totalExpenses.add(
                                        amount);

                        accumulator.totalEntries++;

                        if (Boolean.TRUE.equals(
                                        entry.getPaid())) {

                                accumulator.totalPaid = accumulator.totalPaid.add(
                                                amount);

                        } else {

                                accumulator.totalPending = accumulator.totalPending.add(
                                                amount);
                        }
                }

                return grouped
                                .values()
                                .stream()
                                .map(
                                                accumulator -> new DebtDashboardCardResponse(
                                                                accumulator.userCardId,
                                                                accumulator.cardId,
                                                                accumulator.bank,
                                                                accumulator.cardName,
                                                                money(
                                                                                accumulator.totalExpenses),
                                                                money(
                                                                                accumulator.totalPaid),
                                                                money(
                                                                                accumulator.totalPending),
                                                                accumulator.totalEntries,
                                                                percentage(
                                                                                accumulator.totalExpenses,
                                                                                totalExpenses)))
                                .sorted(
                                                Comparator.comparing(
                                                                DebtDashboardCardResponse::totalExpenses)
                                                                .reversed())
                                .toList();
        }

        private List<DebtDashboardConceptResponse> buildConcepts(
                        List<StatementEntry> entries,
                        BigDecimal totalExpenses) {

                Map<Long, ConceptAccumulator> grouped = new LinkedHashMap<>();

                for (StatementEntry entry : entries) {

                        Concept concept = entry.getConcept();

                        ConceptAccumulator accumulator = grouped.computeIfAbsent(
                                        concept.getConceptId(),
                                        ignored -> new ConceptAccumulator(
                                                        concept.getConceptId(),
                                                        concept.getName()));

                        accumulator.totalExpenses = accumulator.totalExpenses.add(
                                        entry.getAmount());

                        accumulator.totalEntries++;
                }

                return grouped
                                .values()
                                .stream()
                                .map(
                                                accumulator -> new DebtDashboardConceptResponse(
                                                                accumulator.conceptId,
                                                                accumulator.conceptName,
                                                                money(
                                                                                accumulator.totalExpenses),
                                                                accumulator.totalEntries,
                                                                percentage(
                                                                                accumulator.totalExpenses,
                                                                                totalExpenses)))
                                .sorted(
                                                Comparator.comparing(
                                                                DebtDashboardConceptResponse::totalExpenses)
                                                                .reversed())
                                .toList();
        }

        private List<DebtDashboardStatementResponse> buildStatements(
                        List<StatementEntry> entries) {

                Map<Long, StatementAccumulator> grouped = new LinkedHashMap<>();

                for (StatementEntry entry : entries) {

                        Statement statement = entry.getStatement();

                        StatementAccumulator accumulator = grouped.computeIfAbsent(
                                        statement.getStatementId(),
                                        ignored -> new StatementAccumulator(
                                                        statement));

                        BigDecimal amount = entry.getAmount();

                        accumulator.totalExpenses = accumulator.totalExpenses.add(
                                        amount);

                        accumulator.totalEntries++;

                        if (Boolean.TRUE.equals(
                                        entry.getPaid())) {

                                accumulator.totalPaid = accumulator.totalPaid.add(
                                                amount);

                        } else {

                                accumulator.totalPending = accumulator.totalPending.add(
                                                amount);
                        }
                }

                return grouped
                                .values()
                                .stream()
                                .map(
                                                accumulator -> {

                                                        Statement statement = accumulator.statement;

                                                        UserCard userCard = statement.getUserCard();

                                                        Card card = userCard.getCard();

                                                        return new DebtDashboardStatementResponse(
                                                                        statement.getStatementId(),
                                                                        userCard.getUserCardId(),
                                                                        card.getCardId(),
                                                                        card.getBank(),
                                                                        card.getCardName(),
                                                                        statement.getYear(),
                                                                        statement.getMonth(),
                                                                        statement.getPaymentDate(),
                                                                        statement.getPaid(),
                                                                        statement.getStatus(),
                                                                        money(
                                                                                        accumulator.totalExpenses),
                                                                        money(
                                                                                        accumulator.totalPaid),
                                                                        money(
                                                                                        accumulator.totalPending),
                                                                        accumulator.totalEntries);
                                                })
                                .sorted(
                                                Comparator
                                                                .comparing(
                                                                                DebtDashboardStatementResponse::year)
                                                                .thenComparing(
                                                                                DebtDashboardStatementResponse::month)
                                                                .reversed())
                                .toList();
        }

        private BigDecimal percentage(
                        BigDecimal amount,
                        BigDecimal total) {

                if (total.compareTo(
                                ZERO) == 0) {

                        return ZERO.setScale(
                                        PERCENTAGE_SCALE,
                                        RoundingMode.HALF_UP);
                }

                return amount
                                .multiply(
                                                HUNDRED)
                                .divide(
                                                total,
                                                PERCENTAGE_SCALE,
                                                RoundingMode.HALF_UP);
        }

        private BigDecimal money(
                        BigDecimal value) {

                return value.setScale(
                                MONEY_SCALE,
                                RoundingMode.HALF_UP);
        }

        private static final class CardAccumulator {

                private final Long userCardId;

                private final Long cardId;

                private final String bank;

                private final String cardName;

                private BigDecimal totalExpenses = ZERO;

                private BigDecimal totalPaid = ZERO;

                private BigDecimal totalPending = ZERO;

                private long totalEntries;

                private CardAccumulator(
                                Long userCardId,
                                Long cardId,
                                String bank,
                                String cardName) {

                        this.userCardId = userCardId;
                        this.cardId = cardId;
                        this.bank = bank;
                        this.cardName = cardName;
                }
        }

        private static final class ConceptAccumulator {

                private final Long conceptId;

                private final String conceptName;

                private BigDecimal totalExpenses = ZERO;

                private long totalEntries;

                private ConceptAccumulator(
                                Long conceptId,
                                String conceptName) {

                        this.conceptId = conceptId;
                        this.conceptName = conceptName;
                }
        }

        private static final class StatementAccumulator {

                private final Statement statement;

                private BigDecimal totalExpenses = ZERO;

                private BigDecimal totalPaid = ZERO;

                private BigDecimal totalPending = ZERO;

                private long totalEntries;

                private StatementAccumulator(
                                Statement statement) {

                        this.statement = statement;
                }
        }
}