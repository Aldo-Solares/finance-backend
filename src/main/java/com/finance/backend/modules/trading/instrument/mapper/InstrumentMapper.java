package com.finance.backend.modules.trading.instrument.mapper;

import com.finance.backend.modules.catalogs.currency.model.Currency;
import com.finance.backend.modules.trading.instrument.dto.CreateInstrumentRequest;
import com.finance.backend.modules.trading.instrument.dto.InstrumentResponse;
import com.finance.backend.modules.trading.instrument.dto.UpdateInstrumentRequest;
import com.finance.backend.modules.trading.instrument.model.Instrument;

public final class InstrumentMapper {

        private InstrumentMapper() {
        }

        // ===================
        // CREATE REQUEST
        // ===================

        public static Instrument toEntity(
                        CreateInstrumentRequest request,
                        Currency currency) {

                Instrument instrument = new Instrument();

                instrument.setSymbol(request.symbol());
                instrument.setName(request.name());
                instrument.setCurrency(currency);

                return instrument;
        }

        // ===================
        // UPDATE REQUEST
        // ===================

        public static void updateEntity(
                        Instrument instrument,
                        UpdateInstrumentRequest request,
                        Currency currency) {

                instrument.setSymbol(request.symbol());
                instrument.setName(request.name());
                instrument.setCurrency(currency);
        }

        // ===================
        // RESPONSE
        // ===================

        public static InstrumentResponse toResponse(
                        Instrument instrument) {

                Currency currency = instrument.getCurrency();

                return new InstrumentResponse(
                                instrument.getInstrumentId(),
                                instrument.getSymbol(),
                                instrument.getName(),
                                currency.getCurrencyId(),
                                currency.getCode());
        }
}