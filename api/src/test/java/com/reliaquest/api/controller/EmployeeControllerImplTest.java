package com.reliaquest.api.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.reliaquest.api.exception.CustomError;
import com.reliaquest.api.models.CreateEmployeeDTO;
import com.reliaquest.api.models.Employee;
import com.reliaquest.api.models.EmployeeResponse;
import com.reliaquest.api.service.EmployeeService;
import com.reliaquest.api.exception.CustomException;
import com.reliaquest.api.exception.ValidationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.*;

class EmployeeControllerImplTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeControllerImpl employeeController;

    private List<Employee> employeeList;
    private Employee employee;
    private EmployeeResponse employeeResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        employeeList = Arrays.asList(
                new Employee("1", "John Doe", 50000, 30, "Developer", "john.doe@example.com"),
                new Employee("2", "Jane Smith", 60000, 35, "Manager", "jane.smith@example.com")
        );

        employee = employeeList.get(0);
        employeeResponse = new EmployeeResponse("Success", employee);
    }

    @Test
    void testGetAllEmployees_Success() {
        when(employeeService.getAllEmployees()).thenReturn(employeeList);

        ResponseEntity<List> response = employeeController.getAllEmployees();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(employeeService, times(1)).getAllEmployees();
    }

    @Test
    void testGetAllEmployees_EmptyList() {
        when(employeeService.getAllEmployees()).thenReturn(Collections.emptyList());

        ResponseEntity<List> response = employeeController.getAllEmployees();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
        verify(employeeService, times(1)).getAllEmployees();
    }

    @Test
    void testGetEmployeesByNameSearch_Success() {
        when(employeeService.getAllEmployeesBySearchName("John")).thenReturn(Collections.singletonList(employee));

        ResponseEntity<List> response = employeeController.getEmployeesByNameSearch("John");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(employeeService, times(1)).getAllEmployeesBySearchName("John");
    }

    @Test
    void testGetEmployeesByNameSearch_NotFound() {
        when(employeeService.getAllEmployeesBySearchName("XYZ")).thenThrow(new CustomException(CustomError.EMPLOYEE_WITH_GIVEN_NAME_NOT_FOUND));

        assertThrows(CustomException.class, () -> employeeController.getEmployeesByNameSearch("XYZ"));
        verify(employeeService, times(1)).getAllEmployeesBySearchName("XYZ");
    }

    @Test
    void testGetEmployeeById_Success() {
        when(employeeService.getEmployeeById("1")).thenReturn(employee);

        ResponseEntity<Employee> response = employeeController.getEmployeeById("1");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("John Doe", response.getBody().getName());
        verify(employeeService, times(1)).getEmployeeById("1");
    }

    @Test
    void testGetEmployeeById_InvalidId() {
        when(employeeService.getEmployeeById(null)).thenThrow(new ValidationException(CustomError.ID_CAN_NOT_BE_NULL));

        assertThrows(ValidationException.class, () -> employeeController.getEmployeeById(null));
        verify(employeeService, times(1)).getEmployeeById(null);
    }

    @Test
    void testGetHighestSalaryOfEmployees_Success() {
        when(employeeService.getEmployeeWithHighestSalary()).thenReturn(60000);

        ResponseEntity<Integer> response = employeeController.getHighestSalaryOfEmployees();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(60000, response.getBody());
        verify(employeeService, times(1)).getEmployeeWithHighestSalary();
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames_Success() {
        List<String> topEarningEmployees = Arrays.asList("Jane Smith", "John Doe");

        when(employeeService.getTopTenHighestEarningEmployees()).thenReturn(topEarningEmployees);

        ResponseEntity<List<String>> response = employeeController.getTopTenHighestEarningEmployeeNames();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(employeeService, times(1)).getTopTenHighestEarningEmployees();
    }

    @Test
    void testCreateEmployee_Success() {
        Map<String, Object> employeeMap = Map.of(
                "name", "Alex",
                "salary", 70000,
                "age", 28,
                "title", "Engineer",
                "email", "alex@company.com"
        );

        when(employeeService.createEmployee(employeeMap)).thenReturn(employeeResponse);

        ResponseEntity<EmployeeResponse> response = employeeController.createEmployee(employeeMap);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Success", response.getBody().getStatus());
        verify(employeeService, times(1)).createEmployee(employeeMap);
    }

    @Test
    void testCreateEmployee_InvalidInput() {
        when(employeeService.createEmployee(any())).thenThrow(new ValidationException(CustomError.INVALID_OR_MISSING_NAME));

        assertThrows(ValidationException.class, () -> employeeController.createEmployee(Map.of()));
        verify(employeeService, times(1)).createEmployee(any());
    }

    @Test
    void testDeleteEmployeeById_Success() {
        when(employeeService.deleteEmployeeById("1")).thenReturn("John Doe");

        ResponseEntity<String> response = employeeController.deleteEmployeeById("1");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("John Doe", response.getBody());
        verify(employeeService, times(1)).deleteEmployeeById("1");
    }

    @Test
    void testDeleteEmployeeById_InvalidId() {
        when(employeeService.deleteEmployeeById(null)).thenThrow(new ValidationException(CustomError.ID_CAN_NOT_BE_NULL));

        assertThrows(ValidationException.class, () -> employeeController.deleteEmployeeById(null));
        verify(employeeService, times(1)).deleteEmployeeById(null);
    }
}
