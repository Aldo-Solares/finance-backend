package com.finance.backend.modules.investments.investmentsnapshot.mapper;

import com.finance.backend.modules.investments.investmentsnapshot.dto.CreateInvestmentSnapshotRequest;
import com.finance.backend.modules.investments.investmentsnapshot.dto.InvestmentSnapshotResponse;
import com.finance.backend.modules.investments.investmentsnapshot.dto.UpdateInvestmentSnapshotRequest;
import com.finance.backend.modules.investments.investmentsnapshot.model.InvestmentSnapshot;
import com.finance.backend.modules.user.model.User;

public final class InvestmentSnapshotMapper {

    private InvestmentSnapshotMapper() {
    }

    // ===================
    // TO ENTITY
    // ===================

    public static InvestmentSnapshot toEntity(
            CreateInvestmentSnapshotRequest request,
            User user) {

        InvestmentSnapshot snapshot = new InvestmentSnapshot();

        snapshot.setUser(user);
        snapshot.setBalanceDate(request.balanceDate());
        snapshot.setBalance(request.balance());
        snapshot.setContribution(request.contribution());
        snapshot.setWithdrawal(request.withdrawal());

        return snapshot;
    }

    // ===================
    // UPDATE ENTITY
    // ===================

    public static void updateEntity(
            InvestmentSnapshot snapshot,
            UpdateInvestmentSnapshotRequest request) {

        snapshot.setBalanceDate(request.balanceDate());
        snapshot.setBalance(request.balance());
        snapshot.setContribution(request.contribution());
        snapshot.setWithdrawal(request.withdrawal());
    }

    // ===================
    // TO RESPONSE
    // ===================

    public static InvestmentSnapshotResponse toResponse(
            InvestmentSnapshot snapshot) {

        return new InvestmentSnapshotResponse(
                snapshot.getInvestmentSnapshotId(),
                snapshot.getBalanceDate(),
                snapshot.getBalance(),
                snapshot.getContribution(),
                snapshot.getWithdrawal(),
                snapshot.getGeneratedAmount());
    }
}