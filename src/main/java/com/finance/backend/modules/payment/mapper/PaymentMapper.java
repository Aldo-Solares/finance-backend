package com.finance.backend.modules.payment.mapper;

import com.finance.backend.modules.payment.dto.CreatePaymentRequest;
import com.finance.backend.modules.payment.dto.PaymentResponse;
import com.finance.backend.modules.payment.dto.UpdatePaymentRequest;
import com.finance.backend.modules.payment.model.Payment;
import com.finance.backend.modules.statement.model.Statement;
import com.finance.backend.modules.user.model.User;

public final class PaymentMapper {

    private PaymentMapper() {
    }

    public static Payment toEntity(
            CreatePaymentRequest request,
            Statement statement,
            User user) {
        Payment payment = new Payment();

        payment.setStatement(statement);
        payment.setUser(user);
        payment.setAmount(request.amount());
        payment.setPaymentType(request.paymentType());

        return payment;
    }

    public static void updateEntity(
            Payment payment,
            UpdatePaymentRequest request,
            Statement statement,
            User user) {
        payment.setStatement(statement);
        payment.setUser(user);
        payment.setAmount(request.amount());
        payment.setPaymentType(request.paymentType());
    }

    public static PaymentResponse toResponse(
            Payment payment) {
        return new PaymentResponse(
                payment.getPaymentId(),
                payment.getStatement().getStatementId(),
                payment.getUser().getUserId(),
                payment.getUser().getName(),
                payment.getAmount(),
                payment.getPaymentType());
    }
}