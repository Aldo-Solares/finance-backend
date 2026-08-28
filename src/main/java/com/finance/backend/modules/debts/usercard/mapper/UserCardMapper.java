package com.finance.backend.modules.debts.usercard.mapper;

import com.finance.backend.modules.debts.card.model.Card;
import com.finance.backend.modules.debts.usercard.dto.CreateUserCardRequest;
import com.finance.backend.modules.debts.usercard.dto.UpdateUserCardRequest;
import com.finance.backend.modules.debts.usercard.dto.UserCardResponse;
import com.finance.backend.modules.debts.usercard.model.UserCard;
import com.finance.backend.modules.user.model.User;

public final class UserCardMapper {

        private UserCardMapper() {
        }

        public static UserCard toEntity(
                        CreateUserCardRequest request,
                        User user,
                        Card card) {

                UserCard userCard = new UserCard();

                userCard.setUser(
                                user);

                userCard.setCard(
                                card);

                userCard.setActive(
                                request.active());

                return userCard;
        }

        public static void updateEntity(
                        UserCard userCard,
                        UpdateUserCardRequest request) {

                userCard.setActive(
                                request.active());
        }

        public static UserCardResponse toResponse(
                        UserCard userCard) {

                return new UserCardResponse(
                                userCard.getUserCardId(),
                                userCard.getUser().getUserId(),
                                userCard.getCard().getCardId(),
                                userCard.getCard().getBank(),
                                userCard.getCard().getCardName(),
                                userCard.getActive());
        }
}