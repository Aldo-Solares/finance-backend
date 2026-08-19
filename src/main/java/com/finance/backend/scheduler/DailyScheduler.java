package com.finance.backend.scheduler;

import com.finance.backend.modules.debts.statement.service.StatementService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DailyScheduler {

    private final StatementService statementService;

    public DailyScheduler(
            StatementService statementService) {
        this.statementService = statementService;
    }

    @Scheduled(cron = "0 5 0 * * *", zone = "America/Mexico_City")
    public void runDailyTasks() {
        statementService.refreshStatuses();
    }
}