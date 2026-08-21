package com.finance.backend.modules.debts.card.mapper;

import com.finance.backend.modules.debts.card.dto.CardResponse;
import com.finance.backend.modules.debts.card.dto.CreateCardRequest;
import com.finance.backend.modules.debts.card.dto.UpdateCardRequest;
import com.finance.backend.modules.debts.card.model.Card;
import com.finance.backend.modules.debts.cardproduct.model.CardProduct;
import com.finance.backend.modules.user.model.User;

public final class CardMapper {

        private CardMapper() {
        }

        // ===================
        // CREATE
        // ===================

        public static Card toEntity(
                        CreateCardRequest request,
                        CardProduct product,
                        User user) {

                Card card = new Card();

                card.setCardCode(
                                request.cardCode().trim());

                card.setProduct(product);
                card.setUser(user);

                card.setActive(
                                request.active() != null
                                                ? request.active()
                                                : true);

                return card;
        }

        // ===================
        // UPDATE
        // ===================

        public static void updateEntity(
                        Card card,
                        UpdateCardRequest request,
                        CardProduct product) {

                card.setCardCode(
                                request.cardCode().trim());

                card.setProduct(product);
                card.setActive(request.active());
        }

        // ===================
        // RESPONSE
        // ===================

        public static CardResponse toResponse(
                        Card card) {

                return new CardResponse(
                                card.getCardId(),
                                card.getCardCode(),
                                card.getActive(),

                                card.getProduct().getProductId(),
                                card.getProduct().getBank(),
                                card.getProduct().getCardName(),

                                card.getUser().getUserId(),
                                card.getUser().getName());
        }
}