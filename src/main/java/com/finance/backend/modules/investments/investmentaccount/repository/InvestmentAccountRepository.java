package com.finance.backend.modules.investments.investmentaccount.repository;

import com.finance.backend.modules.investments.investmentaccount.model.InvestmentAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestmentAccountRepository
        extends JpaRepository<InvestmentAccount, Long> {
}