package com.finance.backend.modules.investments.investmentaccount.service;

import com.finance.backend.exception.BadRequestException;
import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.modules.investments.investmentaccount.dto.CreateInvestmentAccountRequest;
import com.finance.backend.modules.investments.investmentaccount.dto.InvestmentAccountResponse;
import com.finance.backend.modules.investments.investmentaccount.dto.UpdateInvestmentAccountRequest;
import com.finance.backend.modules.investments.investmentaccount.mapper.InvestmentAccountMapper;
import com.finance.backend.modules.investments.investmentaccount.model.InvestmentAccount;
import com.finance.backend.modules.investments.investmentaccount.repository.InvestmentAccountRepository;
import com.finance.backend.modules.user.model.User;
import com.finance.backend.modules.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional
public class InvestmentAccountService {

    private final InvestmentAccountRepository investmentAccountRepository;
    private final UserRepository userRepository;

    public InvestmentAccountService(
            InvestmentAccountRepository investmentAccountRepository,
            UserRepository userRepository) {
        this.investmentAccountRepository = investmentAccountRepository;
        this.userRepository = userRepository;
    }

    // ===================
    // QUERIES
    // ===================

    @Transactional(readOnly = true)
    public List<InvestmentAccountResponse> findAll() {
        return investmentAccountRepository
                .findAll()
                .stream()
                .map(InvestmentAccountMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public InvestmentAccountResponse findById(
            Long investmentAccountId) {
        return InvestmentAccountMapper.toResponse(
                getEntity(investmentAccountId));
    }

    // ===================
    // CRUD
    // ===================

    public InvestmentAccountResponse create(
            CreateInvestmentAccountRequest request) {

        User user = getUser(request.userId());

        InvestmentAccount account = InvestmentAccountMapper.toEntity(
                request,
                user);

        InvestmentAccount savedAccount = investmentAccountRepository.save(account);

        return InvestmentAccountMapper.toResponse(
                savedAccount);
    }

    public InvestmentAccountResponse update(
            Long investmentAccountId,
            UpdateInvestmentAccountRequest request) {

        InvestmentAccount account = getEntity(investmentAccountId);

        InvestmentAccountMapper.updateEntity(
                account,
                request);

        InvestmentAccount updatedAccount = investmentAccountRepository.save(account);

        return InvestmentAccountMapper.toResponse(
                updatedAccount);
    }

    // ===================
    // BALANCE
    // ===================

    public void increaseBalance(
            InvestmentAccount account,
            BigDecimal amount) {

        account.setBalance(
                money(
                        account.getBalance()
                                .add(amount)));

        investmentAccountRepository.save(account);
    }

    public void withdraw(
            InvestmentAccount account,
            BigDecimal amount) {

        if (account.getBalance().compareTo(amount) < 0) {
            throw new BadRequestException(
                    "Saldo insuficiente en la cuenta de inversión");
        }

        account.setBalance(
                money(
                        account.getBalance()
                                .subtract(amount)));

        investmentAccountRepository.save(account);
    }

    // ===================
    // INTERNAL
    // ===================

    public InvestmentAccount getEntity(
            Long investmentAccountId) {

        return investmentAccountRepository
                .findById(investmentAccountId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Cuenta de inversión no encontrada"));
    }

    private User getUser(
            Long userId) {

        return userRepository
                .findById(userId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Usuario no encontrado"));
    }

    private BigDecimal money(
            BigDecimal value) {
        return value.setScale(
                2,
                RoundingMode.HALF_UP);
    }
}