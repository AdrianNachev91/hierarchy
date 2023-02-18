package com.hierarchy.web;

import com.hierarchy.Exceptions.HierarchyHttpException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice(assignableTypes = {EmployeeController.class, TeamController.class, TeamTypeController.class, RoleController.class, HierarchyController.class, ManagementChainController.class})
public class ControllerExceptionAdvice {

    // Exception will always have a valid http status code
    @SuppressWarnings("ConstantConditions")
    @ExceptionHandler(HierarchyHttpException.class)
    public ResponseEntity<ExceptionResponse> handleIngHttpException(HierarchyHttpException e) {
        return new ResponseEntity<>(new ExceptionResponse(e), resolve(e.getHttpStatus()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadableException() {
        return new ResponseEntity<>(ExceptionResponse.builder().message("Malformed JSON request").build(), BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MultiErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : fieldErrors) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return new ResponseEntity<>(MultiErrorResponse.builder().errors(errors).build(), BAD_REQUEST);
    }

    @Data
    @Builder
    @Accessors(chain = true)
    @AllArgsConstructor
    public static class ExceptionResponse {
        private final String message;

        public ExceptionResponse(Throwable exception) {
            this.message = exception.getMessage();
        }
    }

    @Data
    @Builder
    @Accessors(chain = true)
    @AllArgsConstructor
    public static class MultiErrorResponse {
        private final Map<String, String> errors;
    }
}
