package dev.vasyl.car.sharing.exception;

public class InsufficientItemsException extends RuntimeException {

    public InsufficientItemsException(String message) {
        super(message);
    }
}
