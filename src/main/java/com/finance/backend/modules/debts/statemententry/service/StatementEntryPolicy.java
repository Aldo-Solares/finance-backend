package com.finance.backend.modules.debts.statemententry.service;

import com.finance.backend.modules.debts.concept.model.Concept;
import com.finance.backend.modules.debts.statement.model.Statement;
import com.finance.backend.modules.debts.statemententry.model.StatementEntryType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Component
public class StatementEntryPolicy {

    private static final Set<String> RECURRING_MERCHANTS = Set.of(
            "netflix", "totalplay", "gimnasio", "smartfit", "spotify");

    private static final Map<String, RecurringDateRule> RECURRING_DATE_RULES = Map.of(
            "netflix", new RecurringDateRule(true, 22),
            "totalplay", new RecurringDateRule(true, 19),
            "gimnasio", new RecurringDateRule(false, 10),
            "spotify", new RecurringDateRule(false, 5));

    public StatementEntryType classify(Concept category, String merchant) {
        if ("suscripciones".equalsIgnoreCase(category.getName().trim())) {
            return StatementEntryType.RECURRING;
        }

        return RECURRING_MERCHANTS.contains(normalizeMerchant(merchant))
                ? StatementEntryType.RECURRING
                : StatementEntryType.PURCHASE;
    }

    public LocalDate resolveDate(
            LocalDate requestedDate,
            Statement statement,
            String merchant,
            StatementEntryType entryType) {

        if (requestedDate != null || entryType != StatementEntryType.RECURRING) {
            return requestedDate;
        }

        RecurringDateRule rule = RECURRING_DATE_RULES.get(normalizeMerchant(merchant));
        if (rule == null) {
            return null;
        }

        LocalDate reference = rule.fromPeriodStart()
                ? statement.getPeriodStart()
                : statement.getPeriodEnd();
        return reference == null ? null : reference.withDayOfMonth(rule.dayOfMonth());
    }

    private String normalizeMerchant(String merchant) {
        if (merchant == null) {
            return "";
        }

        String normalized = merchant.toLowerCase(Locale.ROOT);
        StringBuilder key = new StringBuilder();
        normalized.codePoints()
                .filter(Character::isLetterOrDigit)
                .forEach(key::appendCodePoint);
        return key.toString();
    }

    private record RecurringDateRule(boolean fromPeriodStart, int dayOfMonth) {
    }
}
