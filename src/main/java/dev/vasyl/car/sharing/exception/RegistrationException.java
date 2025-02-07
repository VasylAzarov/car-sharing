package dev.vasyl.car.sharing.exception;

public class RegistrationException extends RuntimeException {

    public RegistrationException(String message) {
        super(message);
    }

    public RegistrationException(String message, Exception e) {
        super(message, e);
    }
}
