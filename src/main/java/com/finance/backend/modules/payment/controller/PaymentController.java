package com.finance.backend.modules.payment.controller;

import com.finance.backend.dto.ApiResponse;
import com.finance.backend.modules.payment.dto.CreatePaymentRequest;
import com.finance.backend.modules.payment.dto.PaymentResponse;
import com.finance.backend.modules.payment.dto.UpdatePaymentRequest;
import com.finance.backend.modules.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(
            PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public ApiResponse<List<PaymentResponse>> findAll() {
        return ApiResponse.success(
                paymentService.findAll());
    }

    @GetMapping("/{paymentId}")
    public ApiResponse<PaymentResponse> findById(
            @PathVariable Long paymentId) {
        return ApiResponse.success(
                paymentService.findById(paymentId));
    }

    @GetMapping("/statement/{statementId}")
    public ApiResponse<List<PaymentResponse>> findByStatementId(
            @PathVariable Long statementId) {
        return ApiResponse.success(
                paymentService.findByStatementId(statementId));
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<PaymentResponse>> findByUserId(
            @PathVariable Long userId) {
        return ApiResponse.success(
                paymentService.findByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> create(
            @Valid @RequestBody CreatePaymentRequest request) {
        PaymentResponse payment = paymentService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Pago creado",
                                payment));
    }

    @PutMapping("/{paymentId}")
    public ApiResponse<PaymentResponse> update(
            @PathVariable Long paymentId,
            @Valid @RequestBody UpdatePaymentRequest request) {
        return ApiResponse.success(
                "Pago actualizado",
                paymentService.update(
                        paymentId,
                        request));
    }

    @DeleteMapping("/{paymentId}")
    public ApiResponse<Void> delete(
            @PathVariable Long paymentId) {
        paymentService.delete(paymentId);

        return ApiResponse.success(
                "Pago eliminado",
                null);
    }
}