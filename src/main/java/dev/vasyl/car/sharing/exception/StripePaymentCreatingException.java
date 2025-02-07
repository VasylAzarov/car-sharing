package dev.vasyl.car.sharing.exception;

public class StripePaymentCreatingException extends RuntimeException {

    public StripePaymentCreatingException(String message) {
        super(message);
    }

    public StripePaymentCreatingException(String message, Exception e) {
        super(message);
    }
}
