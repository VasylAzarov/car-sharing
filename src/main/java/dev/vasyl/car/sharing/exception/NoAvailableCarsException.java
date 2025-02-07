package dev.vasyl.car.sharing.exception;

public class NoAvailableCarsException extends RuntimeException {

    public NoAvailableCarsException(String message) {
        super(message);
    }
}
