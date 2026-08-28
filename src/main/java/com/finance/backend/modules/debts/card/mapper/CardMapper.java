package com.finance.backend.modules.debts.card.mapper;

import com.finance.backend.modules.debts.card.dto.CardResponse;
import com.finance.backend.modules.debts.card.dto.CreateCardRequest;
import com.finance.backend.modules.debts.card.dto.UpdateCardRequest;
import com.finance.backend.modules.debts.card.model.Card;

public final class CardMapper {

        private CardMapper() {
        }

        public static Card toEntity(
                        CreateCardRequest request) {

                Card card = new Card();

                card.setBank(
                                request.bank().trim());

                card.setCardName(
                                request.cardName().trim());

                card.setActive(
                                request.active());

                return card;
        }

        public static void updateEntity(
                        Card card,
                        UpdateCardRequest request) {

                card.setBank(
                                request.bank().trim());

                card.setCardName(
                                request.cardName().trim());

                card.setActive(
                                request.active());
        }

        public static CardResponse toResponse(
                        Card card) {

                return new CardResponse(
                                card.getCardId(),
                                card.getBank(),
                                card.getCardName(),
                                card.getActive());
        }
}