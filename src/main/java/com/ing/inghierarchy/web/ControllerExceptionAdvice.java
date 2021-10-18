package com.ing.inghierarchy.web;

import com.ing.inghierarchy.Exceptions.IngHttpException;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice(assignableTypes = {PersonController.class})
public class ControllerExceptionAdvice {

    // Exception will always have a valid http status code
    @SuppressWarnings("ConstantConditions")
    @ExceptionHandler(IngHttpException.class)
    public ResponseEntity<ExceptionResponse> handleIngHttpException(IngHttpException e) {
        return new ResponseEntity<>(new ExceptionResponse(e), resolve(e.getHttpStatus()));
    }

    public static class ExceptionResponse {
        @Getter
        private final String message;

        public ExceptionResponse(Throwable exception) {
            this.message = exception.getMessage();
        }
    }
}
