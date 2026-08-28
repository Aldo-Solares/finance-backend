package com.finance.backend.modules.debts.card.service;

import com.finance.backend.exception.ConflictException;
import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.modules.debts.card.dto.CardResponse;
import com.finance.backend.modules.debts.card.dto.CreateCardRequest;
import com.finance.backend.modules.debts.card.dto.UpdateCardRequest;
import com.finance.backend.modules.debts.card.mapper.CardMapper;
import com.finance.backend.modules.debts.card.model.Card;
import com.finance.backend.modules.debts.card.repository.CardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CardService {

        private final CardRepository cardRepository;

        public CardService(
                        CardRepository cardRepository) {

                this.cardRepository = cardRepository;
        }

        // ===================
        // FIND ALL
        // ===================

        @Transactional(readOnly = true)
        public List<CardResponse> findAll() {

                return cardRepository
                                .findAllByOrderByBankAscCardNameAsc()
                                .stream()
                                .map(CardMapper::toResponse)
                                .toList();
        }

        // ===================
        // FIND ACTIVE
        // ===================

        @Transactional(readOnly = true)
        public List<CardResponse> findAllActive() {

                return cardRepository
                                .findByActiveTrueOrderByBankAscCardNameAsc()
                                .stream()
                                .map(CardMapper::toResponse)
                                .toList();
        }

        // ===================
        // FIND BY ID
        // ===================

        @Transactional(readOnly = true)
        public CardResponse findById(
                        Long cardId) {

                return CardMapper.toResponse(
                                getCard(cardId));
        }

        // ===================
        // CREATE
        // ===================

        public CardResponse create(
                        CreateCardRequest request) {

                boolean exists = cardRepository
                                .existsByBankIgnoreCaseAndCardNameIgnoreCase(
                                                request.bank(),
                                                request.cardName());

                if (exists) {
                        throw new ConflictException(
                                        "La tarjeta ya existe en el catálogo");
                }

                Card card = CardMapper.toEntity(
                                request);

                Card savedCard = cardRepository.save(
                                card);

                return CardMapper.toResponse(
                                savedCard);
        }

        // ===================
        // UPDATE
        // ===================

        public CardResponse update(
                        Long cardId,
                        UpdateCardRequest request) {

                Card card = getCard(
                                cardId);

                boolean identityChanged = !card.getBank()
                                .equalsIgnoreCase(
                                                request.bank())
                                || !card.getCardName()
                                                .equalsIgnoreCase(
                                                                request.cardName());

                if (identityChanged) {

                        boolean exists = cardRepository
                                        .existsByBankIgnoreCaseAndCardNameIgnoreCase(
                                                        request.bank(),
                                                        request.cardName());

                        if (exists) {
                                throw new ConflictException(
                                                "La tarjeta ya existe en el catálogo");
                        }
                }

                CardMapper.updateEntity(
                                card,
                                request);

                Card updatedCard = cardRepository.save(
                                card);

                return CardMapper.toResponse(
                                updatedCard);
        }

        // ===================
        // DELETE
        // ===================

        public void delete(
                        Long cardId) {

                Card card = getCard(
                                cardId);

                cardRepository.delete(
                                card);
        }

        // ===================
        // CARD
        // ===================

        private Card getCard(
                        Long cardId) {

                return cardRepository
                                .findById(cardId)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Tarjeta de catálogo no encontrada"));
        }
}