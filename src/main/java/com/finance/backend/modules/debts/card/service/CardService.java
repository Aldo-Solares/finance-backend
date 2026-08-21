package com.finance.backend.modules.debts.card.service;

import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.modules.debts.card.dto.CardResponse;
import com.finance.backend.modules.debts.card.dto.CreateCardRequest;
import com.finance.backend.modules.debts.card.dto.UpdateCardRequest;
import com.finance.backend.modules.debts.card.mapper.CardMapper;
import com.finance.backend.modules.debts.card.model.Card;
import com.finance.backend.modules.debts.card.repository.CardRepository;
import com.finance.backend.modules.debts.cardproduct.model.CardProduct;
import com.finance.backend.modules.debts.cardproduct.repository.CardProductRepository;
import com.finance.backend.modules.user.model.User;
import com.finance.backend.modules.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CardService {

        private final CardRepository cardRepository;
        private final CardProductRepository cardProductRepository;
        private final UserRepository userRepository;

        public CardService(
                        CardRepository cardRepository,
                        CardProductRepository cardProductRepository,
                        UserRepository userRepository) {

                this.cardRepository = cardRepository;
                this.cardProductRepository = cardProductRepository;
                this.userRepository = userRepository;
        }

        // ===================
        // FIND ALL
        // ===================

        @Transactional(readOnly = true)
        public List<CardResponse> findAll(
                        String email) {

                return cardRepository
                                .findByUserEmailIgnoreCaseOrderByCardIdAsc(email)
                                .stream()
                                .map(CardMapper::toResponse)
                                .toList();
        }

        // ===================
        // FIND BY ID
        // ===================

        @Transactional(readOnly = true)
        public CardResponse findById(
                        Long cardId,
                        String email) {

                return CardMapper.toResponse(
                                getCard(
                                                cardId,
                                                email));
        }

        // ===================
        // CREATE
        // ===================

        public CardResponse create(
                        CreateCardRequest request,
                        String email) {

                CardProduct product = getProduct(
                                request.productId());

                User user = getUser(
                                email);

                Card card = CardMapper.toEntity(
                                request,
                                product,
                                user);

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
                        UpdateCardRequest request,
                        String email) {

                Card card = getCard(
                                cardId,
                                email);

                CardProduct product = getProduct(
                                request.productId());

                CardMapper.updateEntity(
                                card,
                                request,
                                product);

                Card updatedCard = cardRepository.save(
                                card);

                return CardMapper.toResponse(
                                updatedCard);
        }

        // ===================
        // DELETE
        // ===================

        public void delete(
                        Long cardId,
                        String email) {

                Card card = getCard(
                                cardId,
                                email);

                cardRepository.delete(
                                card);
        }

        // ===================
        // CARD
        // ===================

        private Card getCard(
                        Long cardId,
                        String email) {

                return cardRepository
                                .findByCardIdAndUserEmailIgnoreCase(
                                                cardId,
                                                email)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Tarjeta no encontrada"));
        }

        // ===================
        // PRODUCT
        // ===================

        private CardProduct getProduct(
                        Long productId) {

                return cardProductRepository
                                .findById(productId)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Producto de tarjeta no encontrado"));
        }

        // ===================
        // USER
        // ===================

        private User getUser(
                        String email) {

                return userRepository
                                .findByEmailIgnoreCase(email)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Usuario no encontrado"));
        }
}