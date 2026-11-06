package com.finance.backend.modules.debts.msipurchase.repository;

import com.finance.backend.modules.debts.msipurchase.model.MsiPurchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MsiPurchaseRepository extends JpaRepository<MsiPurchase, Long> {

    List<MsiPurchase> findAllByUserCardUserCardIdAndPurchaseKeyOrderByOccurrenceNumberAsc(
            Long userCardId,
            String purchaseKey);
}
