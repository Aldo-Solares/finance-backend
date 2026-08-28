package com.finance.backend.modules.dashboard.debts.controller;

import com.finance.backend.dto.ApiResponse;
import com.finance.backend.modules.dashboard.debts.dto.DebtDashboardFilter;
import com.finance.backend.modules.dashboard.debts.dto.DebtDashboardResponse;
import com.finance.backend.modules.dashboard.debts.service.DebtDashboardService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard/debts")
public class DebtDashboardController {

    private final DebtDashboardService service;

    public DebtDashboardController(
            DebtDashboardService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<DebtDashboardResponse> getDashboard(
            @ModelAttribute DebtDashboardFilter filter,

            Authentication authentication) {
        return ApiResponse.success(
                service.getDashboard(
                        filter,
                        authentication.getName()));
    }
}