package dev.muktiarafi.marisehat.controller;

import dev.muktiarafi.marisehat.dto.ResponseListDto;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseListDto<String> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        var messages = e.getBindingResult()
                .getAllErrors().stream()
                .map(err -> ((FieldError) err).getField() + " " + err.getDefaultMessage())
                .collect(Collectors.toList());

        return ResponseListDto.<String>builder()
                .status(false)
                .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .data(messages)
                .build();
    }
}
