package dev.vasyl.car.sharing.exception;

public class SessionUrlException extends RuntimeException {

    public SessionUrlException(String message) {
        super(message);
    }

    public SessionUrlException(String message, Exception e) {
        super(message);
    }
}
