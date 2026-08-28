package com.finance.backend.modules.trading.usertradingaccount.service;

import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.modules.trading.tradingaccount.model.TradingAccount;
import com.finance.backend.modules.trading.tradingaccount.repository.TradingAccountRepository;
import com.finance.backend.modules.trading.usertradingaccount.dto.CreateUserTradingAccountRequest;
import com.finance.backend.modules.trading.usertradingaccount.dto.UpdateUserTradingAccountRequest;
import com.finance.backend.modules.trading.usertradingaccount.dto.UserTradingAccountResponse;
import com.finance.backend.modules.trading.usertradingaccount.mapper.UserTradingAccountMapper;
import com.finance.backend.modules.trading.usertradingaccount.model.UserTradingAccount;
import com.finance.backend.modules.trading.usertradingaccount.repository.UserTradingAccountRepository;
import com.finance.backend.modules.user.model.User;
import com.finance.backend.modules.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserTradingAccountService {

    private final UserTradingAccountRepository userTradingAccountRepository;
    private final TradingAccountRepository tradingAccountRepository;
    private final UserRepository userRepository;

    public UserTradingAccountService(
            UserTradingAccountRepository userTradingAccountRepository,
            TradingAccountRepository tradingAccountRepository,
            UserRepository userRepository) {

        this.userTradingAccountRepository = userTradingAccountRepository;
        this.tradingAccountRepository = tradingAccountRepository;
        this.userRepository = userRepository;
    }

    // ===================
    // QUERIES
    // ===================

    @Transactional(readOnly = true)
    public List<UserTradingAccountResponse> findAll(
            String email) {

        return userTradingAccountRepository
                .findByUserEmailIgnoreCaseOrderByUserTradingAccountIdAsc(
                        email)
                .stream()
                .map(UserTradingAccountMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserTradingAccountResponse findById(
            Long userTradingAccountId,
            String email) {

        return UserTradingAccountMapper.toResponse(
                getEntity(
                        userTradingAccountId,
                        email));
    }

    // ===================
    // CREATE
    // ===================

    public UserTradingAccountResponse create(
            CreateUserTradingAccountRequest request,
            String email) {

        User user = getUserByEmail(
                email);

        TradingAccount tradingAccount = getTradingAccount(
                request.tradingAccountId());

        UserTradingAccount userTradingAccount = UserTradingAccountMapper.toEntity(
                request,
                user,
                tradingAccount);

        UserTradingAccount savedUserTradingAccount = userTradingAccountRepository.save(
                userTradingAccount);

        return UserTradingAccountMapper.toResponse(
                savedUserTradingAccount);
    }

    // ===================
    // UPDATE
    // ===================

    public UserTradingAccountResponse update(
            Long userTradingAccountId,
            UpdateUserTradingAccountRequest request,
            String email) {

        UserTradingAccount userTradingAccount = getEntity(
                userTradingAccountId,
                email);

        TradingAccount tradingAccount = getTradingAccount(
                request.tradingAccountId());

        UserTradingAccountMapper.updateEntity(
                userTradingAccount,
                request,
                tradingAccount);

        UserTradingAccount savedUserTradingAccount = userTradingAccountRepository.save(
                userTradingAccount);

        return UserTradingAccountMapper.toResponse(
                savedUserTradingAccount);
    }

    // ===================
    // DELETE
    // ===================

    public void delete(
            Long userTradingAccountId,
            String email) {

        UserTradingAccount userTradingAccount = getEntity(
                userTradingAccountId,
                email);

        userTradingAccountRepository.delete(
                userTradingAccount);
    }

    // ===================
    // ENTITY
    // ===================

    @Transactional(readOnly = true)
    public UserTradingAccount getEntity(
            Long userTradingAccountId,
            String email) {

        return userTradingAccountRepository
                .findByUserTradingAccountIdAndUserEmailIgnoreCase(
                        userTradingAccountId,
                        email)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Cuenta de trading del usuario no encontrada"));
    }

    // ===================
    // TRADING ACCOUNT
    // ===================

    private TradingAccount getTradingAccount(
            Long tradingAccountId) {

        return tradingAccountRepository
                .findById(
                        tradingAccountId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Cuenta de trading no encontrada"));
    }

    // ===================
    // USER
    // ===================

    private User getUserByEmail(
            String email) {

        return userRepository
                .findByEmailIgnoreCase(
                        email)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Usuario no encontrado"));
    }
}