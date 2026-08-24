package com.finance.backend.modules.investments.investmentsnapshot.repository;

import com.finance.backend.modules.investments.investmentsnapshot.model.InvestmentSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InvestmentSnapshotRepository
                extends JpaRepository<InvestmentSnapshot, Long> {

        List<InvestmentSnapshot> findByUserEmailIgnoreCaseOrderByBalanceDateDescInvestmentSnapshotIdDesc(
                        String email);

        Optional<InvestmentSnapshot> findByInvestmentSnapshotIdAndUserEmailIgnoreCase(
                        Long investmentSnapshotId,
                        String email);

        Optional<InvestmentSnapshot> findFirstByUserEmailIgnoreCaseOrderByBalanceDateDescInvestmentSnapshotIdDesc(
                        String email);

        Optional<InvestmentSnapshot> findFirstByUserEmailIgnoreCaseAndBalanceDateLessThanOrderByBalanceDateDescInvestmentSnapshotIdDesc(
                        String email,
                        LocalDate balanceDate);

        boolean existsByUserEmailIgnoreCaseAndBalanceDate(
                        String email,
                        LocalDate balanceDate);
}