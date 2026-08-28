package com.finance.backend.modules.trading.usertradingaccount.repository;

import com.finance.backend.modules.trading.usertradingaccount.model.UserTradingAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserTradingAccountRepository
                extends JpaRepository<UserTradingAccount, Long> {

        List<UserTradingAccount> findByUserEmailIgnoreCaseOrderByUserTradingAccountIdAsc(
                        String email);

        Optional<UserTradingAccount> findByUserTradingAccountIdAndUserEmailIgnoreCase(
                        Long userTradingAccountId,
                        String email);
}