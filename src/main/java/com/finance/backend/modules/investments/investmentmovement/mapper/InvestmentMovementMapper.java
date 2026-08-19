package com.finance.backend.modules.investments.investmentmovement.mapper;

import com.finance.backend.modules.investments.investmentaccount.model.InvestmentAccount;
import com.finance.backend.modules.investments.investmentmovement.dto.CreateInvestmentMovementRequest;
import com.finance.backend.modules.investments.investmentmovement.dto.InvestmentMovementResponse;
import com.finance.backend.modules.investments.investmentmovement.model.InvestmentMovement;

import java.time.LocalDate;

public final class InvestmentMovementMapper {

    private InvestmentMovementMapper() {
    }

    public static InvestmentMovement toEntity(
            CreateInvestmentMovementRequest request,
            InvestmentAccount account,
            LocalDate date) {

        InvestmentMovement movement = new InvestmentMovement();

        movement.setInvestmentAccount(account);
        movement.setType(request.type());
        movement.setAmount(request.amount());
        movement.setDate(date);
        movement.setNotes(request.notes());

        return movement;
    }

    public static InvestmentMovementResponse toResponse(
            InvestmentMovement movement) {

        return new InvestmentMovementResponse(
                movement.getInvestmentMovementId(),
                movement.getInvestmentAccount()
                        .getInvestmentAccountId(),
                movement.getType(),
                movement.getAmount(),
                movement.getDate(),
                movement.getNotes());
    }
}