package com.reliaquest.api.Validator;

import com.reliaquest.api.exception.CustomError;
import com.reliaquest.api.exception.ValidationException;
import com.reliaquest.api.models.CreateEmployeeDTO;
import com.reliaquest.api.validator.CreateEmployeeInputValidation;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CreateEmployeeInputValidationTest {

    @Test
    void ValidateInput_Success() throws ValidationException {
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("name", "Peter Parker");
        employeeInput.put("age", 40);
        employeeInput.put("salary", 70000);
        employeeInput.put("title", "Accountant");

        CreateEmployeeDTO result = CreateEmployeeInputValidation.ValidateInput(employeeInput);

        assertNotNull(result);
        assertEquals(result, result);
        assertEquals(70000, result.getSalary());
        assertEquals(40, result.getAge());
        assertEquals("Accountant", result.getTitle());
    }


    @Test
    void ValidateInput_MissingName() {
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("age", 40);
        employeeInput.put("salary", 70000);
        employeeInput.put("title", "Accountant");

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            CreateEmployeeInputValidation.ValidateInput(employeeInput);
        });

        assertEquals(CustomError.INVALID_OR_MISSING_NAME, exception.getError());
    }

    @Test
    void ValidateInput_BlankName() throws ValidationException {
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("name", "");
        employeeInput.put("age", 40);
        employeeInput.put("salary", 70000);
        employeeInput.put("title", "Accountant");

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            CreateEmployeeInputValidation.ValidateInput(employeeInput);
        });

        assertEquals(CustomError.INVALID_OR_MISSING_NAME, exception.getError());
    }

    @Test
    void ValidateInput_MissingAge() throws ValidationException {
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("name", "Peter Parker");
        employeeInput.put("salary", 70000);
        employeeInput.put("title", "Accountant");

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            CreateEmployeeInputValidation.ValidateInput(employeeInput);
        });

        assertEquals(CustomError.INVALID_OR_MISSING_AGE, exception.getError());
    }

    @Test
    void ValidateInput_MinimumAge() throws ValidationException {
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("name", "Peter Parker");
        employeeInput.put("age", 10);
        employeeInput.put("salary", 70000);
        employeeInput.put("title", "Accountant");

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            CreateEmployeeInputValidation.ValidateInput(employeeInput);
        });

        assertEquals(CustomError.MINIMUM_AGE_ERROR, exception.getError());
    }

    @Test
    void ValidateInput_MaximumAge() throws ValidationException {
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("name", "Peter Parker");
        employeeInput.put("age", 100);
        employeeInput.put("salary", 70000);
        employeeInput.put("title", "Accountant");

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            CreateEmployeeInputValidation.ValidateInput(employeeInput);
        });

        assertEquals(CustomError.MAXIMUM_AGE_ERROR, exception.getError());
    }

    @Test
    void ValidateInput_MissingSalary() throws ValidationException {
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("name", "Peter Parker");
        employeeInput.put("age", 100);
        employeeInput.put("title", "Accountant");

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            CreateEmployeeInputValidation.ValidateInput(employeeInput);
        });

        assertEquals(CustomError.INVALID_OR_MISSING_SALARY, exception.getError());
    }

    @Test
    void ValidateInput_MinimumSalary() throws ValidationException {
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("name", "Peter Parker");
        employeeInput.put("age", 100);
        employeeInput.put("salary", -200);
        employeeInput.put("title", "Accountant");

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            CreateEmployeeInputValidation.ValidateInput(employeeInput);
        });

        assertEquals(CustomError.MINIMUM_SALARY_ERROR, exception.getError());
    }

    @Test
    void ValidateInput_MissingTitle() throws ValidationException {
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("name", "Peter Parker");
        employeeInput.put("age", 30);
        employeeInput.put("salary", 200);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            CreateEmployeeInputValidation.ValidateInput(employeeInput);
        });

        assertEquals(CustomError.INVALID_OR_MISSING_TITLE, exception.getError());
    }

    @Test
    void ValidateInput_BlankTitle() throws ValidationException {
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("name", "Peter Parker");
        employeeInput.put("age", 30);
        employeeInput.put("salary", 2000);
        employeeInput.put("title", "");

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            CreateEmployeeInputValidation.ValidateInput(employeeInput);
        });

        assertEquals(CustomError.INVALID_OR_MISSING_TITLE, exception.getError());
    }
}
