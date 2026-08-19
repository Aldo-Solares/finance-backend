package com.finance.backend.modules.trading.instrument.mapper;

import com.finance.backend.modules.trading.instrument.dto.CreateInstrumentRequest;
import com.finance.backend.modules.trading.instrument.dto.InstrumentResponse;
import com.finance.backend.modules.trading.instrument.dto.UpdateInstrumentRequest;
import com.finance.backend.modules.trading.instrument.model.Instrument;

public final class InstrumentMapper {

    private InstrumentMapper() {
    }

    public static Instrument toEntity(
            CreateInstrumentRequest request) {

        Instrument instrument = new Instrument();

        instrument.setSymbol(
                request.symbol().toUpperCase());
        instrument.setName(request.name());
        instrument.setType(request.type());
        instrument.setCurrency(
                request.currency().toUpperCase());

        return instrument;
    }

    public static void updateEntity(
            Instrument instrument,
            UpdateInstrumentRequest request) {

        instrument.setSymbol(
                request.symbol().toUpperCase());
        instrument.setName(request.name());
        instrument.setType(request.type());
        instrument.setCurrency(
                request.currency().toUpperCase());
    }

    public static InstrumentResponse toResponse(
            Instrument instrument) {

        return new InstrumentResponse(
                instrument.getInstrumentId(),
                instrument.getSymbol(),
                instrument.getName(),
                instrument.getType(),
                instrument.getCurrency());
    }
}