package net.breezeware.SpringBootCafeteria.exception;

public class InvalidStatusException extends RuntimeException{
    public InvalidStatusException(String message)
    {
        super(message);
    }
}
