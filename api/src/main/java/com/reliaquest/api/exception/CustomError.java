package com.reliaquest.api.exception;

import lombok.Getter;

@Getter
public enum CustomError {
    FEIGN_CLIENT_ERROR("E001", "Error while making a Rest API call"),
    NO_DATA_FOUND("E002", "No Employees found"),
    EMPLOYEE_WITH_GIVEN_NAME_NOT_FOUND("E003", "No Employees present for the given name"),
    INVALID_OR_MISSING_NAME("E004", "Invalid or missing name"),
    INVALID_OR_MISSING_SALARY("E005", "Invalid or missing salary"),
    MINIMUM_SALARY_ERROR("E006", "Salary cannot be less than zero"),
    INVALID_OR_MISSING_AGE("E007", "Invalid or missing age"),
    MINIMUM_AGE_ERROR("E008", "Age cannot be less than 16"),
    MAXIMUM_AGE_ERROR("E009", "Age cannot be more than 75"),
    ID_CAN_NOT_BE_NULL("E010", "Id cannot be null"),
    INVALID_OR_MISSING_TITLE("E011","Invalid or missing Title"),
    RETRY_ERROR("E012","Failed to get response");

    private final String code;
    private final String message;

    CustomError(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
