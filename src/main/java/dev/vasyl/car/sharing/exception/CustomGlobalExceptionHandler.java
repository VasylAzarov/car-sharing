package dev.vasyl.car.sharing.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST);
        List<String> errors = ex.getBindingResult().getAllErrors().stream()
                .map(this::getErrorMessage)
                .toList();
        body.put("errors", errors);
        return new ResponseEntity<>(body, headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RegistrationException.class)
    protected ResponseEntity<Object> handleRegistrationException(
            RegistrationException ex, WebRequest request) {
        return processHandlingException(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFoundException(
            EntityNotFoundException ex, WebRequest request) {
        return processHandlingException(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    protected ResponseEntity<Object> handleEntityAlreadyExistsException(
            EntityAlreadyExistsException ex, WebRequest request) {
        return processHandlingException(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InsufficientItemsException.class)
    protected ResponseEntity<Object> handleInsufficientItemsException(
            InsufficientItemsException ex, WebRequest request) {
        return processHandlingException(ex, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(OrderProcessingException.class)
    protected ResponseEntity<Object> handleOrderProcessingException(
            OrderProcessingException ex, WebRequest request) {
        return processHandlingException(ex, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(UserProcessingException.class)
    protected ResponseEntity<Object> handleUserProcessingException(
            UserProcessingException ex, WebRequest request) {
        return processHandlingException(ex, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(NoAvailableCarsException.class)
    protected ResponseEntity<Object> handleNoAvailableCarsException(
            NoAvailableCarsException ex, WebRequest request) {
        return processHandlingException(ex, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(StripePaymentCreatingException.class)
    protected ResponseEntity<Object> handleStripePaymentCreatingException(
            StripePaymentCreatingException ex, WebRequest request) {
        return processHandlingException(ex, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(StripePaymentCheckException.class)
    protected ResponseEntity<Object> handleStripePaymentCheckException(
            StripePaymentCheckException ex, WebRequest request) {
        return processHandlingException(ex, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(StripePaymentUserCheckException.class)
    protected ResponseEntity<Object> handleStripePaymentUserCheckException(
            StripePaymentUserCheckException ex, WebRequest request) {
        return processHandlingException(ex, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(TelegramNotificationException.class)
    protected ResponseEntity<Object> handleTelegramNotificationException(
            StripePaymentUserCheckException ex, WebRequest request) {
        return processHandlingException(ex, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private ResponseEntity<Object> processHandlingException(RuntimeException ex,
                                                            HttpStatus status) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status);
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, status);
    }

    private String getErrorMessage(ObjectError e) {
        if (e instanceof FieldError) {
            String field = ((FieldError) e).getField();
            String message = e.getDefaultMessage();
            return field + " " + message;
        }
        return e.getDefaultMessage();
    }
}
