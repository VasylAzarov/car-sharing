package dev.vasyl.car.sharing.exception;

public class StripePaymentCheckException extends RuntimeException {

    public StripePaymentCheckException(String message) {
        super(message);
    }

    public StripePaymentCheckException(String message, Exception e) {
        super(message);
    }
}
