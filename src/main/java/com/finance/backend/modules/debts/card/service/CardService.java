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

        @Transactional(readOnly = true)
        public List<CardResponse> findAll() {
                return cardRepository
                                .findAll()
                                .stream()
                                .map(CardMapper::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public CardResponse findById(
                        Long cardId) {
                return CardMapper.toResponse(
                                getCard(cardId));
        }

        public CardResponse create(
                        CreateCardRequest request) {
                CardProduct product = getProduct(
                                request.productId());

                User user = getUser(
                                request.userId());

                Card card = CardMapper.toEntity(
                                request,
                                product,
                                user);

                Card savedCard = cardRepository.save(
                                card);

                return CardMapper.toResponse(
                                savedCard);
        }

        public CardResponse update(
                        Long cardId,
                        UpdateCardRequest request) {
                Card card = getCard(
                                cardId);

                CardProduct product = getProduct(
                                request.productId());

                User user = getUser(
                                request.userId());

                CardMapper.updateEntity(
                                card,
                                request,
                                product,
                                user);

                Card updatedCard = cardRepository.save(
                                card);

                return CardMapper.toResponse(
                                updatedCard);
        }

        public void delete(
                        Long cardId) {
                Card card = getCard(
                                cardId);

                cardRepository.delete(
                                card);
        }

        private Card getCard(
                        Long cardId) {
                return cardRepository
                                .findById(cardId)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Tarjeta no encontrada"));
        }

        private CardProduct getProduct(
                        Long productId) {
                return cardProductRepository
                                .findById(productId)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Producto de tarjeta no encontrado"));
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