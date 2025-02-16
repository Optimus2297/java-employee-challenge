package com.reliaquest.api.service;

import com.reliaquest.api.exception.CustomError;
import com.reliaquest.api.exception.CustomException;
import com.reliaquest.api.exception.ValidationException;
import com.reliaquest.api.external.EmployeeClient;
import com.reliaquest.api.models.*;
import com.reliaquest.api.validator.CreateEmployeeInputValidation;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeClient employeeClient;

    @InjectMocks
    private EmployeeService employeeService;

    private List<Employee> employeeList;

    @BeforeEach
    void setUp() {
        employeeList = List.of(
                new Employee("1", "Alice", 8000, 30, "Manager", "alice@company.com"),
                new Employee("2", "Bob", 5000, 28, "Developer", "bob@company.com"),
                new Employee("3", "Charlie", 7000, 35, "Accountant", "charlie@company.com")
        );
    }

    @Test
    void getAllEmployees_Success() {
        EmployeeResponseList response = new EmployeeResponseList("Success", employeeList);
        when(employeeClient.getAllEmployees()).thenReturn(response);

        List<Employee> result = employeeService.getAllEmployees();

        assertEquals(3, result.size());
        verify(employeeClient, times(1)).getAllEmployees();
    }

    @Test
    void getAllEmployees_FeignException() {
        when(employeeClient.getAllEmployees()).thenThrow(FeignException.class);

        CustomException exception = assertThrows(CustomException.class, employeeService::getAllEmployees);
        assertEquals(CustomError.FEIGN_CLIENT_ERROR, exception.getError());
    }

    @Test
    void getAllEmployees_NoDataFound() {
        when(employeeClient.getAllEmployees()).thenReturn(new EmployeeResponseList("Success", List.of()));

        CustomException exception = assertThrows(CustomException.class, employeeService::getAllEmployees);
        assertEquals(CustomError.NO_DATA_FOUND, exception.getError());
    }

    @Test
    void getAllEmployeesBySearchName_Success() {
        EmployeeResponseList response = new EmployeeResponseList("Success", employeeList);
        when(employeeClient.getAllEmployees()).thenReturn(response);

        List<Employee> result = employeeService.getAllEmployeesBySearchName("Alice");

        assertEquals(1, result.size());
        assertEquals("Alice", result.get(0).getName());
    }

    @Test
    void getEmployeeById_Success() {
        Employee employee = employeeList.get(0);
        when(employeeClient.getEmployeeById("1")).thenReturn(new EmployeeResponse("Success", employee));

        Employee result = employeeService.getEmployeeById("1");

        assertEquals("Alice", result.getName());
    }

    @Test
    void getEmployeeById_NullId() {
        ValidationException exception = assertThrows(ValidationException.class, () -> employeeService.getEmployeeById(null));
        assertEquals(CustomError.ID_CAN_NOT_BE_NULL, exception.getError());
    }

    @Test
    void getEmployeeWithHighestSalary() {
        EmployeeResponseList response = new EmployeeResponseList("Success", employeeList);
        when(employeeClient.getAllEmployees()).thenReturn(response);

        int highestSalary = employeeService.getEmployeeWithHighestSalary();

        assertEquals(8000, highestSalary);
    }

    @Test
    void getTopTenHighestEarningEmployees() {
        EmployeeResponseList response = new EmployeeResponseList("Success", employeeList);
        when(employeeClient.getAllEmployees()).thenReturn(response);

        List<String> topEarners = employeeService.getTopTenHighestEarningEmployees();

        assertEquals(3, topEarners.size());
        assertEquals("Alice", topEarners.get(0));
    }

    @Test
    void createEmployee_Success() {
        Map<String, Object> employeeMap = Map.of("name", "David", "salary", 6000, "age", 32, "title", "Engineer", "email", "david@company.com");
        CreateEmployeeDTO createEmployeeDTO = new CreateEmployeeDTO("David", 6000, 32, "Engineer");
        EmployeeResponse employeeResponse = new EmployeeResponse("Success", new Employee("4", "David", 6000, 32, "Engineer", "david@company.com"));

        when(employeeClient.createEmployee(createEmployeeDTO)).thenReturn(employeeResponse);

        EmployeeResponse result = employeeService.createEmployee(employeeMap);

        assertEquals("David", result.getData().getName());
    }

    @Test
    void deleteEmployeeById_Success() {
        Employee employee = new Employee("1", "Alice", 8000, 30, "Manager", "alice@company.com");
        when(employeeClient.getEmployeeById("1")).thenReturn(new EmployeeResponse("Success", employee));
        when(employeeClient.deleteEmployeeById(any(DeleteEmployeeDTO.class))).thenReturn("Success");

        String deletedEmployeeName = employeeService.deleteEmployeeById("1");

        assertEquals("Alice", deletedEmployeeName);
    }

    @Test
    void deleteEmployeeById_NullId() {
        ValidationException exception = assertThrows(ValidationException.class, () -> employeeService.deleteEmployeeById(null));
        assertEquals(CustomError.ID_CAN_NOT_BE_NULL, exception.getError());
    }
}
