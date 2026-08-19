package com.finance.backend.modules.investments.investmentmovement.repository;

import com.finance.backend.modules.investments.investmentmovement.model.InvestmentMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvestmentMovementRepository
        extends JpaRepository<InvestmentMovement, Long> {

    List<InvestmentMovement> findAllByOrderByDateAscInvestmentMovementIdAsc();

    List<InvestmentMovement> findByInvestmentAccountInvestmentAccountIdOrderByDateAscInvestmentMovementIdAsc(
            Long investmentAccountId);
}