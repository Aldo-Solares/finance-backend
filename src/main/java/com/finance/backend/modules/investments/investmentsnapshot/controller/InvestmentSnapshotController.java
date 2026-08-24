package com.finance.backend.modules.investments.investmentsnapshot.controller;

import com.finance.backend.dto.ApiResponse;
import com.finance.backend.modules.investments.investmentsnapshot.dto.CreateInvestmentSnapshotRequest;
import com.finance.backend.modules.investments.investmentsnapshot.dto.InvestmentPerformanceResponse;
import com.finance.backend.modules.investments.investmentsnapshot.dto.InvestmentSnapshotResponse;
import com.finance.backend.modules.investments.investmentsnapshot.dto.UpdateInvestmentSnapshotRequest;
import com.finance.backend.modules.investments.investmentsnapshot.service.InvestmentSnapshotService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/investment-snapshots")
public class InvestmentSnapshotController {

        private final InvestmentSnapshotService investmentSnapshotService;

        public InvestmentSnapshotController(
                        InvestmentSnapshotService investmentSnapshotService) {
                this.investmentSnapshotService = investmentSnapshotService;
        }

        // ===================
        // FIND ALL
        // ===================

        @GetMapping
        public ResponseEntity<ApiResponse<List<InvestmentSnapshotResponse>>> findAll(
                        Authentication authentication) {

                List<InvestmentSnapshotResponse> snapshots = investmentSnapshotService.findAll(
                                authentication.getName());

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                snapshots));
        }

        // ===================
        // FIND BY ID
        // ===================

        @GetMapping("/{investmentSnapshotId}")
        public ResponseEntity<ApiResponse<InvestmentSnapshotResponse>> findById(
                        @PathVariable Long investmentSnapshotId,
                        Authentication authentication) {

                InvestmentSnapshotResponse snapshot = investmentSnapshotService.findById(
                                investmentSnapshotId,
                                authentication.getName());

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                snapshot));
        }

        // ===================
        // PERFORMANCE
        // ===================

        @GetMapping("/performance")
        public ResponseEntity<ApiResponse<InvestmentPerformanceResponse>> getPerformance(
                        Authentication authentication) {

                InvestmentPerformanceResponse performance = investmentSnapshotService.getPerformance(
                                authentication.getName());

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                performance));
        }

        // ===================
        // CREATE
        // ===================

        @PostMapping
        public ResponseEntity<ApiResponse<InvestmentSnapshotResponse>> create(
                        @Valid @RequestBody CreateInvestmentSnapshotRequest request,
                        Authentication authentication) {

                InvestmentSnapshotResponse snapshot = investmentSnapshotService.create(
                                request,
                                authentication.getName());

                return ResponseEntity
                                .status(
                                                HttpStatus.CREATED)
                                .body(
                                                ApiResponse.success(
                                                                "Registro de inversión creado correctamente",
                                                                snapshot));
        }

        // ===================
        // UPDATE
        // ===================

        @PutMapping("/{investmentSnapshotId}")
        public ResponseEntity<ApiResponse<InvestmentSnapshotResponse>> update(
                        @PathVariable Long investmentSnapshotId,
                        @Valid @RequestBody UpdateInvestmentSnapshotRequest request,
                        Authentication authentication) {

                InvestmentSnapshotResponse snapshot = investmentSnapshotService.update(
                                investmentSnapshotId,
                                request,
                                authentication.getName());

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Registro de inversión actualizado correctamente",
                                                snapshot));
        }

        // ===================
        // DELETE
        // ===================

        @DeleteMapping("/{investmentSnapshotId}")
        public ResponseEntity<ApiResponse<Void>> delete(
                        @PathVariable Long investmentSnapshotId,
                        Authentication authentication) {

                investmentSnapshotService.delete(
                                investmentSnapshotId,
                                authentication.getName());

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Registro de inversión eliminado correctamente",
                                                null));
        }
}