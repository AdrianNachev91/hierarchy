package com.hierarchy.Exceptions;

import lombok.Getter;

public class HierarchyHttpException extends RuntimeException {

    @Getter
    private final int httpStatus;

    private HierarchyHttpException(String message, int httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public static HierarchyHttpException notFound(String message) {
        return new HierarchyHttpException(message, 404);
    }

    public static HierarchyHttpException badRequest(String message) {
        return new HierarchyHttpException(message, 400);
    }
}
