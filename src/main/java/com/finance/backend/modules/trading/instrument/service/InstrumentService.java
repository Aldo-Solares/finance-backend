package com.finance.backend.modules.trading.instrument.service;

import com.finance.backend.exception.ConflictException;
import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.modules.catalogs.currency.model.Currency;
import com.finance.backend.modules.catalogs.currency.repository.CurrencyRepository;
import com.finance.backend.modules.trading.instrument.dto.CreateInstrumentRequest;
import com.finance.backend.modules.trading.instrument.dto.InstrumentResponse;
import com.finance.backend.modules.trading.instrument.dto.UpdateInstrumentRequest;
import com.finance.backend.modules.trading.instrument.mapper.InstrumentMapper;
import com.finance.backend.modules.trading.instrument.model.Instrument;
import com.finance.backend.modules.trading.instrument.repository.InstrumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class InstrumentService {

        private final InstrumentRepository instrumentRepository;
        private final CurrencyRepository currencyRepository;

        public InstrumentService(
                        InstrumentRepository instrumentRepository,
                        CurrencyRepository currencyRepository) {

                this.instrumentRepository = instrumentRepository;
                this.currencyRepository = currencyRepository;
        }

        // ===================
        // QUERIES
        // ===================

        @Transactional(readOnly = true)
        public List<InstrumentResponse> findAll() {

                return instrumentRepository
                                .findAll()
                                .stream()
                                .map(InstrumentMapper::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public InstrumentResponse findById(
                        Long instrumentId) {

                return InstrumentMapper.toResponse(
                                getEntity(instrumentId));
        }

        // ===================
        // CREATE
        // ===================

        public InstrumentResponse create(
                        CreateInstrumentRequest request) {

                if (instrumentRepository
                                .existsBySymbolIgnoreCase(
                                                request.symbol())) {

                        throw new ConflictException(
                                        "Ya existe un instrumento con ese símbolo");
                }

                Currency currency = getCurrency(
                                request.currencyId());

                Instrument instrument = InstrumentMapper.toEntity(
                                request,
                                currency);

                Instrument savedInstrument = instrumentRepository.save(
                                instrument);

                return InstrumentMapper.toResponse(
                                savedInstrument);
        }

        // ===================
        // UPDATE
        // ===================

        public InstrumentResponse update(
                        Long instrumentId,
                        UpdateInstrumentRequest request) {

                Instrument instrument = getEntity(instrumentId);

                instrumentRepository
                                .findBySymbolIgnoreCase(
                                                request.symbol())
                                .filter(
                                                existing -> !existing.getInstrumentId()
                                                                .equals(instrumentId))
                                .ifPresent(
                                                existing -> {
                                                        throw new ConflictException(
                                                                        "Ya existe un instrumento con ese símbolo");
                                                });

                Currency currency = getCurrency(
                                request.currencyId());

                InstrumentMapper.updateEntity(
                                instrument,
                                request,
                                currency);

                Instrument updatedInstrument = instrumentRepository.save(
                                instrument);

                return InstrumentMapper.toResponse(
                                updatedInstrument);
        }

        // ===================
        // ENTITY
        // ===================

        @Transactional(readOnly = true)
        public Instrument getEntity(
                        Long instrumentId) {

                return instrumentRepository
                                .findById(instrumentId)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Instrumento no encontrado"));
        }

        // ===================
        // CURRENCY
        // ===================

        @Transactional(readOnly = true)
        private Currency getCurrency(
                        Long currencyId) {

                return currencyRepository
                                .findById(currencyId)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Moneda no encontrada"));
        }
}