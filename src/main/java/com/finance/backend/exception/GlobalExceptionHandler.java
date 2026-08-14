package com.finance.backend.exception;

import com.finance.backend.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(
                        HttpMessageNotReadableException exception) {

                return ResponseEntity
                                .badRequest()
                                .body(ApiResponse.error(
                                                "JSON inválido o con formato incorrecto"));
        }

        // ===================
        // 400
        // ===================
        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ApiResponse<Void>> handleBadRequest(
                        BadRequestException exception) {

                return ResponseEntity
                                .badRequest()
                                .body(ApiResponse.error(
                                                exception.getMessage()));
        }

        // ===================
        // 401
        // ===================
        @ExceptionHandler(UnauthorizedException.class)
        public ResponseEntity<ApiResponse<Void>> handleUnauthorized(
                        UnauthorizedException exception) {

                return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(ApiResponse.error(
                                                exception.getMessage()));
        }

        // ===================
        // 403
        // ===================
        @ExceptionHandler(ForbiddenException.class)
        public ResponseEntity<ApiResponse<Void>> handleForbidden(
                        ForbiddenException exception) {

                return ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .body(ApiResponse.error(
                                                exception.getMessage()));
        }

        // ===================
        // 404
        // ===================
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(
                        ResourceNotFoundException exception) {

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(ApiResponse.error(
                                                exception.getMessage()));
        }

        // ===================
        // 409
        // ===================
        @ExceptionHandler(ConflictException.class)
        public ResponseEntity<ApiResponse<Void>> handleConflict(
                        ConflictException exception) {

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(ApiResponse.error(
                                                exception.getMessage()));
        }

        // ===================
        // 500
        // ===================
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Void>> handleException(
                        Exception exception) {

                LOGGER.error(
                                "Error inesperado no controlado",
                                exception);

                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.error(
                                                "Error interno del servidor"));
        }

        // ===================
        // HANDLER
        // ===================
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Void>> handleValidationException(
                        MethodArgumentNotValidException exception) {

                String message = exception
                                .getBindingResult()
                                .getAllErrors()
                                .stream()
                                .map(error -> {
                                        if (error instanceof FieldError fieldError) {
                                                return fieldError.getField()
                                                                + ": "
                                                                + fieldError.getDefaultMessage();
                                        }

                                        return error.getDefaultMessage();
                                })
                                .filter(Objects::nonNull)
                                .distinct()
                                .collect(Collectors.joining("; "));

                if (message.isBlank()) {
                        message = "Datos inválidos";
                }

                return ResponseEntity
                                .badRequest()
                                .body(ApiResponse.error(message));
        }
}