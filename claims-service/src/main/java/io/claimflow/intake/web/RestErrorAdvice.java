package io.claimflow.intake.web;

import io.claimflow.contracts.http.ErrorEnvelope;
import io.claimflow.intake.errors.ClaimTransitionException;
import io.claimflow.intake.errors.UnknownClaimException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/** Converts expected application failures to the stable API error document. */
@RestControllerAdvice
public class RestErrorAdvice {

    @ExceptionHandler(UnknownClaimException.class)
    public ResponseEntity<ErrorEnvelope> unknownClaim(
            UnknownClaimException failure, HttpServletRequest request) {
        return respond(HttpStatus.NOT_FOUND, failure.getMessage(), request);
    }

    @ExceptionHandler(ClaimTransitionException.class)
    public ResponseEntity<ErrorEnvelope> invalidTransition(
            ClaimTransitionException failure, HttpServletRequest request) {
        return respond(HttpStatus.CONFLICT, failure.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorEnvelope> invalidBody(
            MethodArgumentNotValidException failure, HttpServletRequest request) {
        String details = failure.getBindingResult().getFieldErrors().stream()
                .sorted(Comparator.comparing(field -> field.getField()))
                .map(field -> field.getField() + ": " + field.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return respond(HttpStatus.BAD_REQUEST, details, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorEnvelope> invalidParameter(
            MethodArgumentTypeMismatchException failure, HttpServletRequest request) {
        String details = failure.getName() + ": invalid value '" + failure.getValue() + "'";
        return respond(HttpStatus.BAD_REQUEST, details, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorEnvelope> unreadableBody(
            HttpMessageNotReadableException failure, HttpServletRequest request) {
        return respond(
                HttpStatus.BAD_REQUEST,
                "Request body is malformed or contains an unsupported value",
                request);
    }

    private ResponseEntity<ErrorEnvelope> respond(
            HttpStatus status, String details, HttpServletRequest request) {
        ErrorEnvelope body = ErrorEnvelope.create(
                status.value(), status.getReasonPhrase(), details, request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
