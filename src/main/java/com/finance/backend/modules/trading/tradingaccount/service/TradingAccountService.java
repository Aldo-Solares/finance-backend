package com.finance.backend.modules.trading.tradingaccount.service;

import com.finance.backend.exception.BadRequestException;
import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.modules.trading.tradingaccount.dto.CreateTradingAccountRequest;
import com.finance.backend.modules.trading.tradingaccount.dto.TradingAccountResponse;
import com.finance.backend.modules.trading.tradingaccount.dto.UpdateTradingAccountRequest;
import com.finance.backend.modules.trading.tradingaccount.mapper.TradingAccountMapper;
import com.finance.backend.modules.trading.tradingaccount.model.TradingAccount;
import com.finance.backend.modules.trading.tradingaccount.repository.TradingAccountRepository;
import com.finance.backend.modules.user.model.User;
import com.finance.backend.modules.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional
public class TradingAccountService {

        private final TradingAccountRepository tradingAccountRepository;
        private final UserRepository userRepository;

        public TradingAccountService(
                        TradingAccountRepository tradingAccountRepository,
                        UserRepository userRepository) {
                this.tradingAccountRepository = tradingAccountRepository;
                this.userRepository = userRepository;
        }

        // ===================
        // QUERIES
        // ===================

        @Transactional(readOnly = true)
        public List<TradingAccountResponse> findAll() {
                return tradingAccountRepository
                                .findAll()
                                .stream()
                                .map(TradingAccountMapper::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public TradingAccountResponse findById(
                        Long tradingAccountId) {

                return TradingAccountMapper.toResponse(
                                getEntity(tradingAccountId));
        }

        // ===================
        // CRUD
        // ===================

        public TradingAccountResponse create(
                        CreateTradingAccountRequest request) {

                User user = getUser(request.userId());

                TradingAccount account = TradingAccountMapper.toEntity(
                                request,
                                user);

                TradingAccount savedAccount = tradingAccountRepository.save(account);

                return TradingAccountMapper.toResponse(
                                savedAccount);
        }

        public TradingAccountResponse update(
                        Long tradingAccountId,
                        UpdateTradingAccountRequest request) {

                TradingAccount account = getEntity(tradingAccountId);

                TradingAccountMapper.updateEntity(
                                account,
                                request);

                TradingAccount updatedAccount = tradingAccountRepository.save(account);

                return TradingAccountMapper.toResponse(
                                updatedAccount);
        }

        // ===================
        // CASH
        // ===================

        public void applyDeposit(
                        TradingAccount account,
                        BigDecimal amount) {

                account.setAvailableAmount(
                                money(
                                                account.getAvailableAmount()
                                                                .add(amount)));

                recalculateBalance(account);
        }

        public void applyWithdrawal(
                        TradingAccount account,
                        BigDecimal amount) {

                if (account.getAvailableAmount()
                                .compareTo(amount) < 0) {
                        throw new BadRequestException(
                                        "Saldo disponible insuficiente");
                }

                account.setAvailableAmount(
                                money(
                                                account.getAvailableAmount()
                                                                .subtract(amount)));

                recalculateBalance(account);
        }

        // ===================
        // TRADES
        // ===================

        public void applyBuy(
                        TradingAccount account,
                        BigDecimal totalCost) {

                if (account.getAvailableAmount()
                                .compareTo(totalCost) < 0) {
                        throw new BadRequestException(
                                        "Saldo disponible insuficiente para realizar la compra");
                }

                account.setAvailableAmount(
                                money(
                                                account.getAvailableAmount()
                                                                .subtract(totalCost)));

                account.setInvestedAmount(
                                money(
                                                account.getInvestedAmount()
                                                                .add(totalCost)));

                recalculateBalance(account);
        }

        public void applySell(
                        TradingAccount account,
                        BigDecimal costBasisToRelease,
                        BigDecimal netProceeds) {

                if (account.getInvestedAmount()
                                .compareTo(costBasisToRelease) < 0) {
                        throw new BadRequestException(
                                        "El costo de la posición excede el monto invertido");
                }

                account.setInvestedAmount(
                                money(
                                                account.getInvestedAmount()
                                                                .subtract(costBasisToRelease)));

                account.setAvailableAmount(
                                money(
                                                account.getAvailableAmount()
                                                                .add(netProceeds)));

                recalculateBalance(account);
        }

        // ===================
        // INTERNAL
        // ===================

        public TradingAccount getEntity(
                        Long tradingAccountId) {

                return tradingAccountRepository
                                .findById(tradingAccountId)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Cuenta de trading no encontrada"));
        }

        private User getUser(
                        Long userId) {

                return userRepository
                                .findById(userId)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Usuario no encontrado"));
        }

        private void recalculateBalance(
                        TradingAccount account) {

                account.setBalance(
                                money(
                                                account.getAvailableAmount()
                                                                .add(
                                                                                account.getInvestedAmount())));

                tradingAccountRepository.save(account);
        }

        private BigDecimal money(
                        BigDecimal value) {

                return value.setScale(
                                2,
                                RoundingMode.HALF_UP);
        }
}