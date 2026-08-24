package com.finance.backend.modules.investments.investmentsnapshot.service;

import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.modules.investments.investmentsnapshot.dto.CreateInvestmentSnapshotRequest;
import com.finance.backend.modules.investments.investmentsnapshot.dto.InvestmentPerformanceResponse;
import com.finance.backend.modules.investments.investmentsnapshot.dto.InvestmentSnapshotResponse;
import com.finance.backend.modules.investments.investmentsnapshot.dto.UpdateInvestmentSnapshotRequest;
import com.finance.backend.modules.investments.investmentsnapshot.mapper.InvestmentSnapshotMapper;
import com.finance.backend.modules.investments.investmentsnapshot.model.InvestmentSnapshot;
import com.finance.backend.modules.investments.investmentsnapshot.repository.InvestmentSnapshotRepository;
import com.finance.backend.modules.user.model.User;
import com.finance.backend.modules.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class InvestmentSnapshotService {

        private final InvestmentSnapshotRepository investmentSnapshotRepository;
        private final UserRepository userRepository;

        public InvestmentSnapshotService(
                        InvestmentSnapshotRepository investmentSnapshotRepository,
                        UserRepository userRepository) {
                this.investmentSnapshotRepository = investmentSnapshotRepository;
                this.userRepository = userRepository;
        }

        // ===================
        // FIND ALL
        // ===================

        @Transactional(readOnly = true)
        public List<InvestmentSnapshotResponse> findAll(
                        String email) {

                return investmentSnapshotRepository
                                .findByUserEmailIgnoreCaseOrderByBalanceDateDescInvestmentSnapshotIdDesc(
                                                email)
                                .stream()
                                .map(InvestmentSnapshotMapper::toResponse)
                                .toList();
        }

        // ===================
        // FIND BY ID
        // ===================

        @Transactional(readOnly = true)
        public InvestmentSnapshotResponse findById(
                        Long investmentSnapshotId,
                        String email) {

                InvestmentSnapshot snapshot = getOwnedSnapshot(
                                investmentSnapshotId,
                                email);

                return InvestmentSnapshotMapper.toResponse(snapshot);
        }

        // ===================
        // CREATE
        // ===================

        public InvestmentSnapshotResponse create(
                        CreateInvestmentSnapshotRequest request,
                        String email) {

                if (investmentSnapshotRepository
                                .existsByUserEmailIgnoreCaseAndBalanceDate(
                                                email,
                                                request.balanceDate())) {

                        throw new ResponseStatusException(
                                        HttpStatus.CONFLICT,
                                        "Ya existe un registro para esa fecha");
                }

                User user = getUser(email);

                InvestmentSnapshot snapshot = InvestmentSnapshotMapper.toEntity(
                                request,
                                user);

                BigDecimal generatedAmount = calculateGeneratedAmount(
                                email,
                                request.balanceDate(),
                                request.balance(),
                                request.contribution(),
                                request.withdrawal());

                snapshot.setGeneratedAmount(
                                generatedAmount);

                InvestmentSnapshot savedSnapshot = investmentSnapshotRepository.save(
                                snapshot);

                recalculateFollowingSnapshots(
                                email,
                                savedSnapshot);

                return InvestmentSnapshotMapper.toResponse(
                                savedSnapshot);
        }

        // ===================
        // UPDATE
        // ===================

        public InvestmentSnapshotResponse update(
                        Long investmentSnapshotId,
                        UpdateInvestmentSnapshotRequest request,
                        String email) {

                InvestmentSnapshot snapshot = getOwnedSnapshot(
                                investmentSnapshotId,
                                email);

                InvestmentSnapshotMapper.updateEntity(
                                snapshot,
                                request);

                BigDecimal generatedAmount = calculateGeneratedAmountExcludingCurrent(
                                email,
                                snapshot);

                snapshot.setGeneratedAmount(
                                generatedAmount);

                InvestmentSnapshot updatedSnapshot = investmentSnapshotRepository.save(
                                snapshot);

                recalculateAll(
                                email);

                return InvestmentSnapshotMapper.toResponse(
                                investmentSnapshotRepository
                                                .findById(
                                                                updatedSnapshot.getInvestmentSnapshotId())
                                                .orElseThrow(() -> new ResourceNotFoundException(
                                                                "Registro de inversión no encontrado")));
        }

        // ===================
        // DELETE
        // ===================

        public void delete(
                        Long investmentSnapshotId,
                        String email) {

                InvestmentSnapshot snapshot = getOwnedSnapshot(
                                investmentSnapshotId,
                                email);

                investmentSnapshotRepository.delete(
                                snapshot);

                investmentSnapshotRepository.flush();

                recalculateAll(
                                email);
        }

        // ===================
        // PERFORMANCE
        // ===================

        @Transactional(readOnly = true)
        public InvestmentPerformanceResponse getPerformance(
                        String email) {

                List<InvestmentSnapshot> snapshots = investmentSnapshotRepository
                                .findByUserEmailIgnoreCaseOrderByBalanceDateDescInvestmentSnapshotIdDesc(
                                                email);

                if (snapshots.isEmpty()) {
                        return new InvestmentPerformanceResponse(
                                        BigDecimal.ZERO,
                                        BigDecimal.ZERO,
                                        BigDecimal.ZERO,
                                        BigDecimal.ZERO,
                                        BigDecimal.ZERO,
                                        null);
                }

                InvestmentSnapshot latest = snapshots.getFirst();

                BigDecimal generatedTotal = snapshots.stream()
                                .map(
                                                InvestmentSnapshot::getGeneratedAmount)
                                .reduce(
                                                BigDecimal.ZERO,
                                                BigDecimal::add);

                BigDecimal totalContributions = snapshots.stream()
                                .map(
                                                InvestmentSnapshot::getContribution)
                                .reduce(
                                                BigDecimal.ZERO,
                                                BigDecimal::add);

                BigDecimal totalWithdrawals = snapshots.stream()
                                .map(
                                                InvestmentSnapshot::getWithdrawal)
                                .reduce(
                                                BigDecimal.ZERO,
                                                BigDecimal::add);

                return new InvestmentPerformanceResponse(
                                latest.getBalance(),
                                latest.getGeneratedAmount(),
                                generatedTotal,
                                totalContributions,
                                totalWithdrawals,
                                latest.getBalanceDate());
        }

        // ===================
        // CALCULATE GENERATED
        // ===================

        private BigDecimal calculateGeneratedAmount(
                        String email,
                        java.time.LocalDate balanceDate,
                        BigDecimal balance,
                        BigDecimal contribution,
                        BigDecimal withdrawal) {

                return investmentSnapshotRepository
                                .findFirstByUserEmailIgnoreCaseAndBalanceDateLessThanOrderByBalanceDateDescInvestmentSnapshotIdDesc(
                                                email,
                                                balanceDate)
                                .map(previous -> balance
                                                .subtract(
                                                                previous.getBalance())
                                                .subtract(
                                                                contribution)
                                                .add(
                                                                withdrawal))
                                .orElse(BigDecimal.ZERO);
        }

        // ===================
        // CALCULATE EXCLUDING CURRENT
        // ===================

        private BigDecimal calculateGeneratedAmountExcludingCurrent(
                        String email,
                        InvestmentSnapshot current) {

                List<InvestmentSnapshot> snapshots = investmentSnapshotRepository
                                .findByUserEmailIgnoreCaseOrderByBalanceDateDescInvestmentSnapshotIdDesc(
                                                email);

                InvestmentSnapshot previous = null;

                for (InvestmentSnapshot candidate : snapshots) {

                        if (candidate
                                        .getInvestmentSnapshotId()
                                        .equals(
                                                        current.getInvestmentSnapshotId())) {
                                continue;
                        }

                        if (candidate
                                        .getBalanceDate()
                                        .isBefore(
                                                        current.getBalanceDate())) {

                                previous = candidate;
                                break;
                        }
                }

                if (previous == null) {
                        return BigDecimal.ZERO;
                }

                return current
                                .getBalance()
                                .subtract(
                                                previous.getBalance())
                                .subtract(
                                                current.getContribution())
                                .add(
                                                current.getWithdrawal());
        }

        // ===================
        // RECALCULATE FOLLOWING
        // ===================

        private void recalculateFollowingSnapshots(
                        String email,
                        InvestmentSnapshot createdSnapshot) {

                recalculateAll(
                                email);
        }

        // ===================
        // RECALCULATE ALL
        // ===================

        private void recalculateAll(
                        String email) {

                List<InvestmentSnapshot> snapshots = investmentSnapshotRepository
                                .findByUserEmailIgnoreCaseOrderByBalanceDateDescInvestmentSnapshotIdDesc(
                                                email)
                                .reversed();

                InvestmentSnapshot previous = null;

                for (InvestmentSnapshot snapshot : snapshots) {

                        BigDecimal generatedAmount;

                        if (previous == null) {
                                generatedAmount = BigDecimal.ZERO;
                        } else {
                                generatedAmount = snapshot
                                                .getBalance()
                                                .subtract(
                                                                previous.getBalance())
                                                .subtract(
                                                                snapshot.getContribution())
                                                .add(
                                                                snapshot.getWithdrawal());
                        }

                        snapshot.setGeneratedAmount(
                                        generatedAmount);

                        previous = snapshot;
                }

                investmentSnapshotRepository.saveAll(
                                snapshots);
        }

        // ===================
        // OWNED SNAPSHOT
        // ===================

        private InvestmentSnapshot getOwnedSnapshot(
                        Long investmentSnapshotId,
                        String email) {

                return investmentSnapshotRepository
                                .findByInvestmentSnapshotIdAndUserEmailIgnoreCase(
                                                investmentSnapshotId,
                                                email)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Registro de inversión no encontrado"));
        }

        // ===================
        // USER
        // ===================

        private User getUser(
                        String email) {

                return userRepository
                                .findByEmailIgnoreCase(
                                                email)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Usuario no encontrado"));
        }
}