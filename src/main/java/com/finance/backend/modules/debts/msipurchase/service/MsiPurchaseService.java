package com.finance.backend.modules.debts.msipurchase.service;

import com.finance.backend.modules.debts.msipurchase.model.MsiPurchase;
import com.finance.backend.modules.debts.msipurchase.repository.MsiPurchaseRepository;
import com.finance.backend.modules.debts.statement.model.Statement;
import com.finance.backend.modules.debts.statemententry.model.StatementEntry;
import com.finance.backend.modules.debts.statemententry.repository.StatementEntryRepository;
import com.finance.backend.modules.debts.usercard.model.UserCard;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class MsiPurchaseService {

    private final MsiPurchaseRepository msiPurchaseRepository;
    private final StatementEntryRepository statementEntryRepository;

    public MsiPurchaseService(
            MsiPurchaseRepository msiPurchaseRepository,
            StatementEntryRepository statementEntryRepository) {
        this.msiPurchaseRepository = msiPurchaseRepository;
        this.statementEntryRepository = statementEntryRepository;
    }

    public MsiPurchase resolveOrCreate(
            UserCard userCard,
            Statement statement,
            Long categoryId,
            String debtor,
            String description,
            String merchant,
            LocalDate purchaseDate,
            Integer monthlyPaymentNumber,
            Integer totalMonthlyPayments,
            StatementEntry currentEntry) {

        if (monthlyPaymentNumber == null && totalMonthlyPayments == null) {
            return null;
        }

        if (monthlyPaymentNumber == null || totalMonthlyPayments == null
                || monthlyPaymentNumber < 1
                || totalMonthlyPayments < 1
                || monthlyPaymentNumber > totalMonthlyPayments) {
            throw new IllegalArgumentException(
                    "La mensualidad MSI debe estar entre 1 y el total de meses");
        }

        String purchaseKey = createPurchaseKey(
                userCard.getUserCardId(), categoryId, debtor, description, merchant,
                purchaseDate, totalMonthlyPayments);

        List<MsiPurchase> matches = msiPurchaseRepository
                .findAllByUserCardUserCardIdAndPurchaseKeyOrderByOccurrenceNumberAsc(
                        userCard.getUserCardId(), purchaseKey);

        for (MsiPurchase candidate : matches) {
            boolean isCurrentPurchase = currentEntry != null
                    && currentEntry.getMsiPurchase() != null
                    && currentEntry.getMsiPurchase().getMsiPurchaseId()
                            .equals(candidate.getMsiPurchaseId());

            StatementEntry existingMonthlyPayment = statementEntryRepository
                    .findFirstByMsiPurchaseMsiPurchaseIdAndStatementStatementIdAndMonthlyPaymentNumber(
                            candidate.getMsiPurchaseId(),
                            statement.getStatementId(),
                            monthlyPaymentNumber)
                    .orElse(null);

            if (existingMonthlyPayment == null
                    || isCurrentPurchase
                    || (currentEntry != null && existingMonthlyPayment.getEntryId()
                            .equals(currentEntry.getEntryId()))) {
                return candidate;
            }
        }

        int occurrence = matches.stream()
                .mapToInt(MsiPurchase::getOccurrenceNumber)
                .max()
                .orElse(0) + 1;

        MsiPurchase purchase = new MsiPurchase();
        purchase.setUserCard(userCard);
        purchase.setPurchaseKey(purchaseKey);
        purchase.setOccurrenceNumber(occurrence);
        purchase.setTotalMonthlyPayments(totalMonthlyPayments);
        return msiPurchaseRepository.save(purchase);
    }

    private String createPurchaseKey(
            Long userCardId,
            Long categoryId,
            String debtor,
            String description,
            String merchant,
            LocalDate purchaseDate,
            Integer totalMonthlyPayments) {

        String[] identityParts = {
                userCardId.toString(),
                categoryId.toString(),
                normalize(debtor),
                normalize(description),
                normalize(merchant),
                purchaseDate == null ? "" : purchaseDate.toString(),
                totalMonthlyPayments.toString()
        };
        StringBuilder identity = new StringBuilder();
        for (String part : identityParts) {
            identity.append(part.length()).append(':').append(part);
        }

        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(identity.toString().getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 no está disponible", exception);
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }
}
