package net.breezeware.SpringBootCafeteria.exception;

import jakarta.persistence.criteria.CriteriaBuilder;

public class InvalidCredentialException extends  RuntimeException{
    public InvalidCredentialException(String message)
    {
        super(message);
    }
}
