package net.breezeware.SpringBootCafeteria.exception;

import org.springframework.http.HttpStatus;

public class AppCustomException extends RuntimeException {

    private final HttpStatus status;

    public AppCustomException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
