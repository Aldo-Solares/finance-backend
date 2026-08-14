package com.finance.backend.modules.card.mapper;

import com.finance.backend.modules.card.dto.CardResponse;
import com.finance.backend.modules.card.dto.CreateCardRequest;
import com.finance.backend.modules.card.dto.UpdateCardRequest;
import com.finance.backend.modules.card.model.Card;
import com.finance.backend.modules.cardproduct.model.CardProduct;
import com.finance.backend.modules.user.model.User;

public final class CardMapper {

    private CardMapper() {
    }

    public static Card toEntity(
            CreateCardRequest request,
            CardProduct product,
            User user) {
        Card card = new Card();

        card.setCardCode(request.cardCode());
        card.setProduct(product);
        card.setUser(user);

        card.setActive(
                request.active() != null
                        ? request.active()
                        : true);

        return card;
    }

    public static void updateEntity(
            Card card,
            UpdateCardRequest request,
            CardProduct product,
            User user) {
        card.setCardCode(request.cardCode());
        card.setProduct(product);
        card.setUser(user);
        card.setActive(request.active());
    }

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