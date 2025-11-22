package Apex.app.exception;


import Apex.app.dto.ExternalApiResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExternalApiResponseDto> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        String errorMessage = String.join(", ", errors);

        ExternalApiResponseDto errorResponse = createErrorResponse(
                "Validation Failed",
                "Please check your input: " + errorMessage,
                "Invalid input data provided",
                true
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExternalApiResponseDto> handleUserNotFound(UserNotFoundException ex) {
        ExternalApiResponseDto errorResponse = createErrorResponse(
                "User Not Found",
                "Sorry, we couldn't find your profile in our system. Please contact support.",
                ex.getMessage(),
                true
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(CsvProcessingException.class)
    public ResponseEntity<ExternalApiResponseDto> handleCsvProcessingError(CsvProcessingException ex) {
        ExternalApiResponseDto errorResponse = createErrorResponse(
                "System Error",
                "We're experiencing technical difficulties. Please try again later.",
                "Data processing error: " + ex.getMessage(),
                true
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ExternalApiResponseDto> handleExternalApiError(ExternalApiException ex) {
        ExternalApiResponseDto errorResponse = createErrorResponse(
                "Service Unavailable",
                "Our EMI calculation service is temporarily unavailable. Please try again in a few minutes.",
                "External API error: " + ex.getMessage(),
                true
        );

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExternalApiResponseDto> handleMalformedJson(HttpMessageNotReadableException ex) {
        ExternalApiResponseDto errorResponse = createErrorResponse(
                "Invalid Request",
                "Please check your request format and try again.",
                "Malformed JSON: " + ex.getMessage(),
                true
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExternalApiResponseDto> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        ExternalApiResponseDto errorResponse = createErrorResponse(
                "Invalid Data Type",
                "Please check that all values are in the correct format (numbers, text, etc.)",
                "Type mismatch for parameter: " + ex.getName(),
                true
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExternalApiResponseDto> handleGenericError(Exception ex) {
        ExternalApiResponseDto errorResponse = createErrorResponse(
                "System Error",
                "An unexpected error occurred. Our team has been notified.",
                "Internal server error: " + ex.getMessage(),
                true
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    private ExternalApiResponseDto createErrorResponse(String headline, String subcopy, String riskExplanation, boolean isThinFile) {
        return ExternalApiResponseDto.builder()
                .build();
    }
}