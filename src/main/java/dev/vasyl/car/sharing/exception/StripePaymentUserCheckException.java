package dev.vasyl.car.sharing.exception;

public class StripePaymentUserCheckException extends RuntimeException {

    public StripePaymentUserCheckException(String message) {
        super(message);
    }

}
