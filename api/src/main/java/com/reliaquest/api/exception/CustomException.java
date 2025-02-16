package com.reliaquest.api.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final CustomError error;

    public CustomException(CustomError error){
        super(error.getMessage());
        this.error = error;
    }
}
