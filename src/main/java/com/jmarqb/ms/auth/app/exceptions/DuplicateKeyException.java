package com.jmarqb.ms.auth.app.exceptions;

public class DuplicateKeyException extends RuntimeException{
    public DuplicateKeyException(String message) {
        super(message);
    }
}
