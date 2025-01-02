package com.jmarqb.ms.auth.app.exceptions;

public class RoleNotFoundException extends RuntimeException{
    public RoleNotFoundException(String message) {
        super(message);
    }
}
