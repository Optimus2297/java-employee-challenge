package com.reliaquest.api.validator;

import com.reliaquest.api.exception.CustomError;
import com.reliaquest.api.exception.ValidationException;
import com.reliaquest.api.models.CreateEmployeeDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class CreateEmployeeInputValidation {
    public static CreateEmployeeDTO ValidateInput(Map<String, Object> employeeInput) throws ValidationException {
        log.info("Validation started for create employee request body");

        if (!employeeInput.containsKey("name") || !(employeeInput.get("name") instanceof String) ||
                ((String) employeeInput.get("name")).isBlank()) {
            throw new ValidationException(CustomError.INVALID_OR_MISSING_NAME);
        }

        if (!employeeInput.containsKey("salary") || !(employeeInput.get("salary") instanceof Integer)) {
            throw new ValidationException(CustomError.INVALID_OR_MISSING_SALARY);
        }

        if ((Integer) employeeInput.get("salary") < 0) {
            throw new ValidationException(CustomError.MINIMUM_SALARY_ERROR);
        }

        if (!employeeInput.containsKey("age") || !(employeeInput.get("age") instanceof Integer)) {
            throw new ValidationException(CustomError.INVALID_OR_MISSING_AGE);
        }

        if ((Integer) employeeInput.get("age") < 16) {
            throw new ValidationException(CustomError.MINIMUM_AGE_ERROR);
        } else if ((Integer) employeeInput.get("age") > 75) {
            throw new ValidationException(CustomError.MAXIMUM_AGE_ERROR);
        }

        if (!employeeInput.containsKey("title") || !(employeeInput.get("title") instanceof String) ||
                ((String) employeeInput.get("title")).isBlank()) {
            throw new ValidationException(CustomError.INVALID_OR_MISSING_TITLE);
        }
        log.info("Validation successful for create employee input");

        return CreateEmployeeDTO.builder()
                .name((String) employeeInput.get("name"))
                .age((Integer) employeeInput.get("age"))
                .salary((Integer) employeeInput.get("salary"))
                .title((String) employeeInput.get("title"))
                .build();
    }
}
