package org.sh.controller;

import org.sh.dao.DeletionException;
import org.sh.dao.NotUniqueException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.time.DateTimeException;

@ControllerAdvice
@RestController
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException ex) {
        return new ResponseEntity<>(
                "Not valid due to validation error: " + ex.getMessage(),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(DateTimeException.class)
    ResponseEntity<String> handleDateTimeException(DateTimeException ex) {
        return new ResponseEntity<>(
                "Not valid due to: " + ex.getMessage(),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(NotUniqueException.class)
    ResponseEntity<String> handleNotUniqueException(NotUniqueException ex) {
        return new ResponseEntity<>(
                "Not valid due to integrity error: " + ex.getMessage(),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DeletionException.class)
    ResponseEntity<String> handleNotUniqueException(DeletionException ex) {
        return new ResponseEntity<>(
                "Failed: " + ex.getMessage(),
                HttpStatus.FORBIDDEN);
    }
}
