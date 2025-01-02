package com.jmarqb.ms.auth.app.controllers;

import com.jmarqb.ms.auth.app.entities.Error;
import com.jmarqb.ms.auth.app.exceptions.DuplicateKeyException;
import com.jmarqb.ms.auth.app.exceptions.RoleNotFoundException;
import com.jmarqb.ms.auth.app.exceptions.UserNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class HandlerExceptionController {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Error> handleValidationException(MethodArgumentNotValidException ex) {
        List<Error.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> Error.FieldError.builder()
                        .field(error.getField())
                        .rejectedValue(error.getRejectedValue() != null ? error.getRejectedValue().toString() : "null")
                        .message(error.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());

        Error response = Error.builder()
                .timestamp(new Date())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation failed")
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler({DuplicateKeyException.class, DataIntegrityViolationException.class})
    public ResponseEntity<Error> handleDuplicateValidationException(Exception ex) {

        Error response = Error.builder()
                .timestamp(new Date())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Duplicate Key")
                .message("Could not execute statement: Duplicate key or Duplicate entry")
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler({RoleNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<Error> handleValidationException(Exception ex) {

        Error response = Error.builder()
                .timestamp(new Date())
                .status(HttpStatus.NOT_FOUND.value())
                .error("NOT FOUND")
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<Error> handleValidationException(HttpMessageNotReadableException ex,
                                                           WebRequest request) {

        Error response = Error.builder()
                .timestamp(new Date())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Json Error")
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<Error> handleValidationException(BadCredentialsException ex,
                                                           WebRequest request) {

        Error response = Error.builder()
                .timestamp(new Date())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Invalid credentials")
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}
