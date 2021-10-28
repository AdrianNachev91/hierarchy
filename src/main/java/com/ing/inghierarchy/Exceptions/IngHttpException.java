package com.ing.inghierarchy.Exceptions;

import lombok.Getter;

public class IngHttpException extends RuntimeException {

    @Getter
    private final int httpStatus;

    private IngHttpException(String message, int httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public static IngHttpException notFound(String message) {
        return new IngHttpException(message, 404);
    }

    public static IngHttpException badRequest(String message) {
        return new IngHttpException(message, 400);
    }
}
