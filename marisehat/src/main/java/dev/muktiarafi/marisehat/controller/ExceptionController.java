package dev.muktiarafi.marisehat.controller;

import com.microsoft.graph.http.GraphServiceException;
import dev.muktiarafi.marisehat.dto.ResponseListDto;
import dev.muktiarafi.marisehat.exception.ResourceConflictException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionController {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseListDto<String> exceptionHandler(Exception e) {
        return ResponseListDto.<String>builder()
                .status(false)
                .message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .data(List.of(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()))
                .build();
    }

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

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ResourceConflictException.class)
    public ResponseListDto<String> resourceConflictExceptionHandler(ResourceConflictException e) {
        return ResponseListDto.<String>builder()
                .status(false)
                .message(HttpStatus.CONFLICT.getReasonPhrase())
                .data(List.of(e.getMessage()))
                .build();
    }

    @ExceptionHandler(GraphServiceException.class)
    public ResponseEntity<ResponseListDto<String>> graphServiceExceptionHandler(GraphServiceException e) {
        var regex = Pattern.compile("Error message: (.+)");
        var matcher = regex.matcher(e.getMessage());
        String message = e.getMessage();
        if (matcher.find()) {
            message = matcher.group(1);
        }
        var responseList = ResponseListDto.<String>builder()
                .status(false)
                .message(HttpStatus.resolve(e.getResponseCode()).getReasonPhrase())
                .data(List.of(message))
                .build();

        return new ResponseEntity<>(responseList, HttpStatus.resolve(e.getResponseCode()));
    }
}
