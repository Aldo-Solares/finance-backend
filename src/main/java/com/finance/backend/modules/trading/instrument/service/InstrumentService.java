package com.finance.backend.modules.trading.instrument.service;

import com.finance.backend.exception.ConflictException;
import com.finance.backend.exception.ResourceNotFoundException;
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

    public InstrumentService(
            InstrumentRepository instrumentRepository) {
        this.instrumentRepository = instrumentRepository;
    }

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

    public InstrumentResponse create(
            CreateInstrumentRequest request) {

        if (instrumentRepository
                .existsBySymbolIgnoreCase(
                        request.symbol())) {
            throw new ConflictException(
                    "Ya existe un instrumento con ese símbolo");
        }

        Instrument instrument = InstrumentMapper.toEntity(request);

        Instrument savedInstrument = instrumentRepository.save(
                instrument);

        return InstrumentMapper.toResponse(
                savedInstrument);
    }

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

        InstrumentMapper.updateEntity(
                instrument,
                request);

        Instrument updatedInstrument = instrumentRepository.save(
                instrument);

        return InstrumentMapper.toResponse(
                updatedInstrument);
    }

    public Instrument getEntity(
            Long instrumentId) {

        return instrumentRepository
                .findById(instrumentId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Instrumento no encontrado"));
    }
}