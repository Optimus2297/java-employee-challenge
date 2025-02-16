package com.reliaquest.api.exception;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {
    private final CustomError error;

    public ValidationException(CustomError error) {
        super(error.getMessage());
        this.error = error;
    }
}
