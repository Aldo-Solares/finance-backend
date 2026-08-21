package com.finance.backend.modules.investments.investmentmovement.service;

import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.modules.investments.investmentaccount.model.InvestmentAccount;
import com.finance.backend.modules.investments.investmentaccount.service.InvestmentAccountService;
import com.finance.backend.modules.investments.investmentmovement.dto.CreateInvestmentMovementRequest;
import com.finance.backend.modules.investments.investmentmovement.dto.InvestmentMovementResponse;
import com.finance.backend.modules.investments.investmentmovement.mapper.InvestmentMovementMapper;
import com.finance.backend.modules.investments.investmentmovement.model.InvestmentMovement;
import com.finance.backend.modules.investments.investmentmovement.repository.InvestmentMovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class InvestmentMovementService {

        private final InvestmentMovementRepository investmentMovementRepository;
        private final InvestmentAccountService investmentAccountService;

        public InvestmentMovementService(
                        InvestmentMovementRepository investmentMovementRepository,
                        InvestmentAccountService investmentAccountService) {
                this.investmentMovementRepository = investmentMovementRepository;
                this.investmentAccountService = investmentAccountService;
        }

        @Transactional(readOnly = true)
        public List<InvestmentMovementResponse> findAll() {
                return investmentMovementRepository
                                .findAllByOrderByDateAscInvestmentMovementIdAsc()
                                .stream()
                                .map(InvestmentMovementMapper::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public InvestmentMovementResponse findById(
                        Long investmentMovementId) {

                return InvestmentMovementMapper.toResponse(
                                getMovement(investmentMovementId));
        }

        @Transactional(readOnly = true)
        public List<InvestmentMovementResponse> findByInvestmentAccountId(
                        Long investmentAccountId) {

                investmentAccountService.getEntity(
                                investmentAccountId);

                return investmentMovementRepository
                                .findByInvestmentAccountInvestmentAccountIdOrderByDateAscInvestmentMovementIdAsc(
                                                investmentAccountId)
                                .stream()
                                .map(InvestmentMovementMapper::toResponse)
                                .toList();
        }

        public InvestmentMovementResponse create(
                        CreateInvestmentMovementRequest request) {

                InvestmentAccount account = investmentAccountService.getEntity(
                                request.investmentAccountId());

                switch (request.type()) {
                        case DEPOSIT, YIELD ->
                                investmentAccountService.increaseBalance(
                                                account,
                                                request.amount());

                        case WITHDRAWAL ->
                                investmentAccountService.withdraw(
                                                account,
                                                request.amount());
                }

                LocalDate date = request.date() != null
                                ? request.date()
                                : LocalDate.now();

                InvestmentMovement movement = InvestmentMovementMapper.toEntity(
                                request,
                                account,
                                date);

                InvestmentMovement savedMovement = investmentMovementRepository.save(
                                movement);

                return InvestmentMovementMapper.toResponse(
                                savedMovement);
        }

        private InvestmentMovement getMovement(
                        Long investmentMovementId) {

                return investmentMovementRepository
                                .findById(investmentMovementId)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Movimiento de inversión no encontrado"));
        }
}