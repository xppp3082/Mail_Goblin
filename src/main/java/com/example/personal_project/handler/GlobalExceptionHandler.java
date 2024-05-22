package com.example.personal_project.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice
@RestController
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleJSONProcessingException(JsonProcessingException e) {
        return new ResponseEntity<>("Json processing fail, plz check your format : " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleJSONProcessingException(IllegalArgumentException e) {
        return new ResponseEntity<>("Illegal argument from your function call : " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
