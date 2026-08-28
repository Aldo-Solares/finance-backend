package com.finance.backend.modules.debts.statement.mapper;

import com.finance.backend.modules.debts.statement.dto.CreateStatementRequest;
import com.finance.backend.modules.debts.statement.dto.StatementResponse;
import com.finance.backend.modules.debts.statement.dto.UpdateStatementRequest;
import com.finance.backend.modules.debts.statement.model.Statement;
import com.finance.backend.modules.debts.usercard.model.UserCard;

public final class StatementMapper {

        private StatementMapper() {
        }

        public static Statement toEntity(
                        CreateStatementRequest request,
                        UserCard userCard) {

                Statement statement = new Statement();

                statement.setUserCard(userCard);

                statement.setYear(
                                request.periodEnd().getYear());

                statement.setMonth(
                                request.periodEnd().getMonthValue());

                statement.setPeriodStart(
                                request.periodStart());

                statement.setPeriodEnd(
                                request.periodEnd());

                statement.setPaymentDate(
                                request.paymentDate());

                return statement;
        }

        public static void updateEntity(
                        Statement statement,
                        UpdateStatementRequest request,
                        UserCard userCard) {

                statement.setUserCard(
                                userCard);

                statement.setYear(
                                request.periodEnd().getYear());

                statement.setMonth(
                                request.periodEnd().getMonthValue());

                statement.setPeriodStart(
                                request.periodStart());

                statement.setPeriodEnd(
                                request.periodEnd());

                statement.setPaymentDate(
                                request.paymentDate());

                statement.setNotes(
                                request.notes());
        }

        public static StatementResponse toResponse(
                        Statement statement) {

                UserCard userCard = statement.getUserCard();

                return new StatementResponse(
                                statement.getStatementId(),
                                userCard.getUserCardId(),
                                userCard.getCard().getCardId(),
                                userCard.getCard().getBank(),
                                userCard.getCard().getCardName(),
                                statement.getYear(),
                                statement.getMonth(),
                                statement.getPeriodStart(),
                                statement.getPeriodEnd(),
                                statement.getPaymentDate(),
                                statement.getStatus(),
                                statement.getPaid(),
                                statement.getNotes());
        }
}