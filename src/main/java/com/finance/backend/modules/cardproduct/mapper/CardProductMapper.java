package com.finance.backend.modules.cardproduct.mapper;

import com.finance.backend.modules.cardproduct.dto.CreateCardProductRequest;
import com.finance.backend.modules.cardproduct.dto.UpdateCardProductRequest;
import com.finance.backend.modules.cardproduct.dto.CardProductResponse;
import com.finance.backend.modules.cardproduct.model.CardProduct;

public final class CardProductMapper {

    private CardProductMapper() {
    }

    public static CardProduct toEntity(
            CreateCardProductRequest request) {
        CardProduct product = new CardProduct();

        product.setBank(request.bank());
        product.setCardName(request.cardName());

        return product;
    }

    public static void updateEntity(
            CardProduct product,
            UpdateCardProductRequest request) {
        product.setBank(request.bank());
        product.setCardName(request.cardName());
    }

    public static CardProductResponse toResponse(
            CardProduct product) {
        return new CardProductResponse(
                product.getProductId(),
                product.getBank(),
                product.getCardName());
    }
}