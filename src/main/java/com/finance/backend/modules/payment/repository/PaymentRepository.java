package com.finance.backend.modules.payment.repository;

import com.finance.backend.modules.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    List<Payment> findByStatementStatementId(
            Long statementId);

    List<Payment> findByUserUserId(
            Long userId);
}