package com.reliaquest.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.exception.CustomError;
import com.reliaquest.api.exception.CustomException;
import com.reliaquest.api.external.EmployeeClient;
import com.reliaquest.api.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTests {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @MockBean
    private EmployeeClient employeeClient;

    @Autowired
    private ObjectMapper objectMapper;

    private List<Employee> employeeList;
    private Employee employee;

    private EmployeeResponseList employeeResponseList;
    private EmployeeResponse employeeResponse;


    @BeforeEach
    void setUp() {
        baseUrl="http://localhost:"+port+"/api/v1";
        employeeList = Arrays.asList(
                new Employee("1","Braelyn Monroe",50000, 30, "Software Developer", "BM@abc.com"),
                new Employee("2","Emersyn Stewart",70000, 40, "Application Developer", "ES@abc.com")
        );
        employee = employeeList.get(0);
        employeeResponseList = new EmployeeResponseList("Success", employeeList);
        employeeResponse = new EmployeeResponse("Success", employee);
    }

    @Test
    void getAllEmployees_Success() {
        when(employeeClient.getAllEmployees()).thenReturn(employeeResponseList);
        ResponseEntity<Employee[]> response = restTemplate.getForEntity(baseUrl + "/employee", Employee[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<Employee> employeeListFromResponse = Arrays.asList(response.getBody());
        assertEquals(employeeResponseList.getData(), employeeListFromResponse);
    }

    @Test
    void getEmployeeById_Success() {
        when(employeeClient.getEmployeeById("1")).thenReturn(employeeResponse);
        ResponseEntity<Employee> employeeResponse = restTemplate.getForEntity(baseUrl + "/employee/1", Employee.class);
        assertEquals(HttpStatus.OK, employeeResponse.getStatusCode());
        assertEquals("1", Objects.requireNonNull(employeeResponse.getBody()).getId());
    }

    @Test
    void getEmployeeById_NotFound() {
        when(employeeClient.getEmployeeById("999")).thenThrow(new CustomException(CustomError.ID_CAN_NOT_BE_NULL));
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/employee/999", String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response);
    }

    @Test
    void getEmployeesByNameSearch_Success() {
        employeeList = Arrays.asList(
                new Employee("1","Braelyn Monroe",50000, 30, "Software Developer", "BM@abc.com")
        );
        employeeResponseList = new EmployeeResponseList("Success", employeeList);
        when(employeeClient.getAllEmployees()).thenReturn(employeeResponseList);

        ResponseEntity<Employee[]> employeeResponse = restTemplate.getForEntity(baseUrl + "/employee/search/Braelyn", Employee[].class);
        assertEquals(HttpStatus.OK, employeeResponse.getStatusCode());
        assertNotNull(employeeResponse);
        List<Employee> employeeListFromResponse = Arrays.asList(employeeResponse.getBody());
        assertEquals(employeeResponseList.getData(), employeeListFromResponse);
    }

    @Test
    void getHighestSalaryOfEmployees_Success() {
        when(employeeClient.getAllEmployees()).thenReturn(employeeResponseList);
        ResponseEntity<Integer> employeeResponse = restTemplate.getForEntity(baseUrl + "/employee/highest-salary", Integer.class);
        assertEquals(HttpStatus.OK, employeeResponse.getStatusCode());
        assertNotNull(employeeResponse);
        Integer salary = employeeResponse.getBody();
        assertEquals(70000, salary);
    }

    @Test
    void getTopTenHighestEarningEmployeeNames_Success() {
        employeeList = Arrays.asList(
                new Employee("1", "Braelyn Monroe", 50000, 30, "Software Developer", "BM@abc.com"),
                new Employee("2", "Emersyn Stewart", 70000, 40, "Application Developer", "ES@abc.com"),
                new Employee("3", "Avery Brooks", 60000, 25, "UI/UX Designer", "AB@xyz.com"),
                new Employee("4", "Jordan Rivera", 55000, 35, "System Analyst", "JR@abc.com"),
                new Employee("5", "Morgan White", 80000, 45, "Product Manager", "MW@abc.com"),
                new Employee("6", "Taylor Smith", 65000, 28, "DevOps Engineer", "TS@abc.com"),
                new Employee("7", "Cameron Davis", 75000, 32, "Security Specialist", "CD@abc.com"),
                new Employee("8", "Riley Harris", 52000, 26, "QA Engineer", "RH@abc.com"),
                new Employee("9", "Dakota Clark", 67000, 38, "Data Scientist", "DC@abc.com"),
                new Employee("10", "Casey Wilson", 72000, 33, "Software Architect", "CW@abc.com")
        );
        employeeResponseList = new EmployeeResponseList("Success", employeeList);
        when(employeeClient.getAllEmployees()).thenReturn(employeeResponseList);
        ResponseEntity<String[]> employeeResponse = restTemplate.getForEntity(baseUrl + "/employee/top-10-highest-earning", String[].class);
        assertEquals(HttpStatus.OK, employeeResponse.getStatusCode());
        assertNotNull(employeeResponse);
    }

    @Test
    void createEmployee_Success() throws Exception {
        CreateEmployeeDTO newEmployee = new CreateEmployeeDTO("Braelyn Monroe", 50000, 30, "Software Developer");
        when(employeeClient.createEmployee(newEmployee)).thenReturn(employeeResponse);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(newEmployee), headers);

        ResponseEntity<Employee> response = restTemplate.postForEntity(baseUrl + "/create", request, Employee.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }


    @Test
    void createEmployee_BadRequest() throws Exception {
        CreateEmployeeDTO invalidEmployee = new CreateEmployeeDTO("", 50000, 30, "Software Developer");
        when(employeeClient.createEmployee(invalidEmployee)).thenThrow(new CustomException(CustomError.INVALID_OR_MISSING_NAME));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(invalidEmployee), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/create", request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void deleteEmployeeById_Success() {
        when(employeeClient.getEmployeeById("1")).thenReturn(employeeResponse);
        DeleteEmployeeDTO deleteEmployeeDTO = new DeleteEmployeeDTO("Braelyn Monroe");
        when(employeeClient.deleteEmployeeById(deleteEmployeeDTO)).thenReturn("Braelyn Monroe");
        ResponseEntity<String> responseForDelete = restTemplate.exchange(baseUrl + "/deleteById/1", HttpMethod.DELETE, null, String.class);
        assertEquals(HttpStatus.OK, responseForDelete.getStatusCode());
        assertNotNull(responseForDelete);
        String responseName = responseForDelete.getBody();
        assertEquals("Braelyn Monroe", responseName);
    }

    @Test
    void deleteEmployeeById_NotFound() {
        when(employeeClient.getEmployeeById("999")).thenThrow(new CustomException(CustomError.ID_CAN_NOT_BE_NULL));
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/deleteById/999", HttpMethod.DELETE, null, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
