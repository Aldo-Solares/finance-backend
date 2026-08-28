package com.finance.backend.modules.debts.usercard.service;

import com.finance.backend.exception.ConflictException;
import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.modules.debts.card.model.Card;
import com.finance.backend.modules.debts.card.repository.CardRepository;
import com.finance.backend.modules.debts.usercard.dto.CreateUserCardRequest;
import com.finance.backend.modules.debts.usercard.dto.UpdateUserCardRequest;
import com.finance.backend.modules.debts.usercard.dto.UserCardResponse;
import com.finance.backend.modules.debts.usercard.mapper.UserCardMapper;
import com.finance.backend.modules.debts.usercard.model.UserCard;
import com.finance.backend.modules.debts.usercard.repository.UserCardRepository;
import com.finance.backend.modules.user.model.User;
import com.finance.backend.modules.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserCardService {

        private final UserCardRepository userCardRepository;
        private final CardRepository cardRepository;
        private final UserRepository userRepository;

        public UserCardService(
                        UserCardRepository userCardRepository,
                        CardRepository cardRepository,
                        UserRepository userRepository) {

                this.userCardRepository = userCardRepository;
                this.cardRepository = cardRepository;
                this.userRepository = userRepository;
        }

        // ===================
        // FIND ALL
        // ===================

        @Transactional(readOnly = true)
        public List<UserCardResponse> findAll(
                        String email) {

                return userCardRepository
                                .findByUserEmailIgnoreCaseOrderByUserCardIdAsc(
                                                email)
                                .stream()
                                .map(UserCardMapper::toResponse)
                                .toList();
        }

        // ===================
        // FIND ACTIVE
        // ===================

        @Transactional(readOnly = true)
        public List<UserCardResponse> findAllActive(
                        String email) {

                return userCardRepository
                                .findByUserEmailIgnoreCaseAndActiveTrueOrderByUserCardIdAsc(
                                                email)
                                .stream()
                                .map(UserCardMapper::toResponse)
                                .toList();
        }

        // ===================
        // FIND BY ID
        // ===================

        @Transactional(readOnly = true)
        public UserCardResponse findById(
                        Long userCardId,
                        String email) {

                return UserCardMapper.toResponse(
                                getOwnedUserCard(
                                                userCardId,
                                                email));
        }

        // ===================
        // CREATE
        // ===================

        public UserCardResponse create(
                        CreateUserCardRequest request,
                        String email) {

                boolean exists = userCardRepository
                                .existsByUserEmailIgnoreCaseAndCardCardId(
                                                email,
                                                request.cardId());

                if (exists) {
                        throw new ConflictException(
                                        "La tarjeta ya está agregada al usuario");
                }

                User user = getUser(
                                email);

                Card card = getCard(
                                request.cardId());

                UserCard userCard = UserCardMapper.toEntity(
                                request,
                                user,
                                card);

                UserCard savedUserCard = userCardRepository.save(
                                userCard);

                return UserCardMapper.toResponse(
                                savedUserCard);
        }

        // ===================
        // UPDATE
        // ===================

        public UserCardResponse update(
                        Long userCardId,
                        UpdateUserCardRequest request,
                        String email) {

                UserCard userCard = getOwnedUserCard(
                                userCardId,
                                email);

                UserCardMapper.updateEntity(
                                userCard,
                                request);

                UserCard updatedUserCard = userCardRepository.save(
                                userCard);

                return UserCardMapper.toResponse(
                                updatedUserCard);
        }

        // ===================
        // DELETE
        // ===================

        public void delete(
                        Long userCardId,
                        String email) {

                UserCard userCard = getOwnedUserCard(
                                userCardId,
                                email);

                userCardRepository.delete(
                                userCard);
        }

        // ===================
        // OWNED USER CARD
        // ===================

        private UserCard getOwnedUserCard(
                        Long userCardId,
                        String email) {

                return userCardRepository
                                .findByUserCardIdAndUserEmailIgnoreCase(
                                                userCardId,
                                                email)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Tarjeta del usuario no encontrada"));
        }

        // ===================
        // CARD
        // ===================

        private Card getCard(
                        Long cardId) {

                return cardRepository
                                .findById(
                                                cardId)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Tarjeta de catálogo no encontrada"));
        }

        // ===================
        // USER
        // ===================

        private User getUser(
                        String email) {

                return userRepository
                                .findByEmailIgnoreCase(
                                                email)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Usuario no encontrado"));
        }
}