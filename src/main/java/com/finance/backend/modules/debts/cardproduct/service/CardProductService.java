package com.finance.backend.modules.debts.cardproduct.service;

import com.finance.backend.modules.debts.cardproduct.dto.CreateCardProductRequest;
import com.finance.backend.modules.debts.cardproduct.dto.UpdateCardProductRequest;
import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.modules.debts.cardproduct.dto.CardProductResponse;
import com.finance.backend.modules.debts.cardproduct.mapper.CardProductMapper;
import com.finance.backend.modules.debts.cardproduct.model.CardProduct;
import com.finance.backend.modules.debts.cardproduct.repository.CardProductRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardProductService {

        private final CardProductRepository cardProductRepository;

        public CardProductService(
                        CardProductRepository cardProductRepository) {
                this.cardProductRepository = cardProductRepository;
        }

        public List<CardProductResponse> findAll() {
                return cardProductRepository
                                .findAll()
                                .stream()
                                .map(CardProductMapper::toResponse)
                                .toList();
        }

        public CardProductResponse findById(
                        Long productId) {
                return CardProductMapper.toResponse(
                                getCardProduct(productId));
        }

        public CardProductResponse create(
                        CreateCardProductRequest request) {
                CardProduct product = CardProductMapper.toEntity(request);

                CardProduct savedProduct = cardProductRepository.save(product);

                return CardProductMapper.toResponse(
                                savedProduct);
        }

        public CardProductResponse update(
                        Long productId,
                        UpdateCardProductRequest request) {
                CardProduct product = getCardProduct(productId);

                CardProductMapper.updateEntity(
                                product,
                                request);

                CardProduct updatedProduct = cardProductRepository.save(product);

                return CardProductMapper.toResponse(
                                updatedProduct);
        }

        public void delete(Long productId) {
                CardProduct product = getCardProduct(productId);

                cardProductRepository.delete(product);
        }

        private CardProduct getCardProduct(
                        Long productId) {
                return cardProductRepository
                                .findById(productId)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Producto de tarjeta no encontrado"));
        }
}