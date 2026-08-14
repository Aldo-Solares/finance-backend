package com.finance.backend.modules.payment.service;

import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.modules.payment.dto.CreatePaymentRequest;
import com.finance.backend.modules.payment.dto.PaymentResponse;
import com.finance.backend.modules.payment.dto.UpdatePaymentRequest;
import com.finance.backend.modules.payment.mapper.PaymentMapper;
import com.finance.backend.modules.payment.model.Payment;
import com.finance.backend.modules.payment.repository.PaymentRepository;
import com.finance.backend.modules.statement.model.Statement;
import com.finance.backend.modules.statement.repository.StatementRepository;
import com.finance.backend.modules.user.model.User;
import com.finance.backend.modules.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final StatementRepository statementRepository;
    private final UserRepository userRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            StatementRepository statementRepository,
            UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.statementRepository = statementRepository;
        this.userRepository = userRepository;
    }

    public List<PaymentResponse> findAll() {
        return paymentRepository
                .findAll()
                .stream()
                .map(PaymentMapper::toResponse)
                .toList();
    }

    public PaymentResponse findById(
            Long paymentId) {
        return PaymentMapper.toResponse(
                getPayment(paymentId));
    }

    public List<PaymentResponse> findByStatementId(
            Long statementId) {
        getStatement(statementId);

        return paymentRepository
                .findByStatementStatementId(statementId)
                .stream()
                .map(PaymentMapper::toResponse)
                .toList();
    }

    public List<PaymentResponse> findByUserId(
            Long userId) {
        getUser(userId);

        return paymentRepository
                .findByUserUserId(userId)
                .stream()
                .map(PaymentMapper::toResponse)
                .toList();
    }

    public PaymentResponse create(
            CreatePaymentRequest request) {
        Statement statement = getStatement(request.statementId());

        User user = getUser(request.userId());

        Payment payment = PaymentMapper.toEntity(
                request,
                statement,
                user);

        Payment savedPayment = paymentRepository.save(payment);

        return PaymentMapper.toResponse(
                savedPayment);
    }

    public PaymentResponse update(
            Long paymentId,
            UpdatePaymentRequest request) {
        Payment payment = getPayment(paymentId);

        Statement statement = getStatement(request.statementId());

        User user = getUser(request.userId());

        PaymentMapper.updateEntity(
                payment,
                request,
                statement,
                user);

        Payment updatedPayment = paymentRepository.save(payment);

        return PaymentMapper.toResponse(
                updatedPayment);
    }

    public void delete(
            Long paymentId) {
        Payment payment = getPayment(paymentId);

        paymentRepository.delete(payment);
    }

    private Payment getPayment(
            Long paymentId) {
        return paymentRepository
                .findById(paymentId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Pago no encontrado"));
    }

    private Statement getStatement(
            Long statementId) {
        return statementRepository
                .findById(statementId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Estado de cuenta no encontrado"));
    }

    private User getUser(
            Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Usuario no encontrado"));
    }
}