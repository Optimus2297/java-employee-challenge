package com.reliaquest.api.external;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.reliaquest.api.models.*;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class EmployeeClientTest {

    @Mock
    private EmployeeClient employeeClient;

    private List<Employee> employeeList;
    private Employee employee;
    private EmployeeResponse employeeResponse;
    private EmployeeResponseList employeeResponseList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        employeeList = Arrays.asList(
                new Employee("1", "John Doe", 50000, 30, "Developer", "john.doe@example.com"),
                new Employee("2", "Jane Smith", 60000, 35, "Manager", "jane.smith@example.com")
        );

        employee = employeeList.get(0);
        employeeResponse = new EmployeeResponse("Success", employee);
        employeeResponseList = new EmployeeResponseList("Success", employeeList);
    }

    @Test
    void testGetAllEmployees_Success() {
        when(employeeClient.getAllEmployees()).thenReturn(employeeResponseList);

        EmployeeResponseList result = employeeClient.getAllEmployees();

        assertNotNull(result);
        assertEquals(2, result.getData().size());
        verify(employeeClient, times(1)).getAllEmployees();
    }

    @Test
    void testGetAllEmployees_EmptyList() {
        when(employeeClient.getAllEmployees()).thenReturn(new EmployeeResponseList("Success", List.of()));

        EmployeeResponseList result = employeeClient.getAllEmployees();

        assertNotNull(result);
        assertTrue(result.getData().isEmpty());
        verify(employeeClient, times(1)).getAllEmployees();
    }

    @Test
    void testGetEmployeeById_Success() {
        when(employeeClient.getEmployeeById("1")).thenReturn(employeeResponse);

        EmployeeResponse result = employeeClient.getEmployeeById("1");

        assertNotNull(result);
        assertEquals("John Doe", result.getData().getName());
        verify(employeeClient, times(1)).getEmployeeById("1");
    }

    @Test
    void testGetEmployeeById_NotFound() {
        when(employeeClient.getEmployeeById("99")).thenReturn(new EmployeeResponse("Success", null));

        EmployeeResponse result = employeeClient.getEmployeeById("99");

        assertNotNull(result);
        assertNull(result.getData());
        verify(employeeClient, times(1)).getEmployeeById("99");
    }

    @Test
    void testGetEmployeeById_FeignException() {
        when(employeeClient.getEmployeeById("99")).thenThrow(FeignException.class);

        assertThrows(FeignException.class, () -> employeeClient.getEmployeeById("99"));
        verify(employeeClient, times(1)).getEmployeeById("99");
    }

    @Test
    void testCreateEmployee_Success() {
        CreateEmployeeDTO createEmployeeDTO = new CreateEmployeeDTO("Alex", 70000, 28, "Engineer");
        EmployeeResponse createdEmployeeResponse = new EmployeeResponse("Success", employee);

        when(employeeClient.createEmployee(createEmployeeDTO)).thenReturn(createdEmployeeResponse);

        EmployeeResponse response = employeeClient.createEmployee(createEmployeeDTO);

        assertNotNull(response);
        assertEquals("Success", response.getStatus());
        verify(employeeClient, times(1)).createEmployee(createEmployeeDTO);
    }

    @Test
    void testCreateEmployee_FeignException() {
        CreateEmployeeDTO createEmployeeDTO = new CreateEmployeeDTO("Alex", 70000, 28, "Engineer");

        when(employeeClient.createEmployee(createEmployeeDTO)).thenThrow(FeignException.class);

        assertThrows(FeignException.class, () -> employeeClient.createEmployee(createEmployeeDTO));
        verify(employeeClient, times(1)).createEmployee(createEmployeeDTO);
    }

    @Test
    void testDeleteEmployeeById_Success() {
        DeleteEmployeeDTO deleteEmployeeDTO = new DeleteEmployeeDTO("John Doe");

        when(employeeClient.deleteEmployeeById(deleteEmployeeDTO)).thenReturn("Deleted Successfully");

        String response = employeeClient.deleteEmployeeById(deleteEmployeeDTO);

        assertEquals("Deleted Successfully", response);
        verify(employeeClient, times(1)).deleteEmployeeById(deleteEmployeeDTO);
    }

    @Test
    void testDeleteEmployeeById_FeignException() {
        DeleteEmployeeDTO deleteEmployeeDTO = new DeleteEmployeeDTO("John Doe");

        when(employeeClient.deleteEmployeeById(deleteEmployeeDTO)).thenThrow(FeignException.class);

        assertThrows(FeignException.class, () -> employeeClient.deleteEmployeeById(deleteEmployeeDTO));
        verify(employeeClient, times(1)).deleteEmployeeById(deleteEmployeeDTO);
    }
}
