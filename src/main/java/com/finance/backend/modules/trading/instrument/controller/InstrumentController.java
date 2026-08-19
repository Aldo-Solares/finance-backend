package com.finance.backend.modules.trading.instrument.controller;

import com.finance.backend.dto.ApiResponse;
import com.finance.backend.modules.trading.instrument.dto.CreateInstrumentRequest;
import com.finance.backend.modules.trading.instrument.dto.InstrumentResponse;
import com.finance.backend.modules.trading.instrument.dto.UpdateInstrumentRequest;
import com.finance.backend.modules.trading.instrument.service.InstrumentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instruments")
public class InstrumentController {

    private final InstrumentService instrumentService;

    public InstrumentController(
            InstrumentService instrumentService) {
        this.instrumentService = instrumentService;
    }

    @GetMapping
    public ApiResponse<List<InstrumentResponse>> findAll() {
        return ApiResponse.success(
                instrumentService.findAll());
    }

    @GetMapping("/{instrumentId}")
    public ApiResponse<InstrumentResponse> findById(
            @PathVariable Long instrumentId) {

        return ApiResponse.success(
                instrumentService.findById(
                        instrumentId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InstrumentResponse>> create(
            @Valid @RequestBody CreateInstrumentRequest request) {

        InstrumentResponse instrument = instrumentService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Instrumento creado",
                                instrument));
    }

    @PutMapping("/{instrumentId}")
    public ApiResponse<InstrumentResponse> update(
            @PathVariable Long instrumentId,
            @Valid @RequestBody UpdateInstrumentRequest request) {

        return ApiResponse.success(
                "Instrumento actualizado",
                instrumentService.update(
                        instrumentId,
                        request));
    }
}