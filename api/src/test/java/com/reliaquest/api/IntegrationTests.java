package com.reliaquest.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.controller.EmployeeControllerImpl;
import com.reliaquest.api.exception.CustomError;
import com.reliaquest.api.exception.CustomException;
import com.reliaquest.api.exception.ValidationException;
import com.reliaquest.api.models.Employee;
import com.reliaquest.api.models.EmployeeResponse;
import com.reliaquest.api.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(EmployeeControllerImpl.class)
public class IntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    private List<Employee> employeeList;
    private Employee employee;
    private final String baseUrl = "/api/v1";

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        employeeList = Arrays.asList(
                new Employee("1","Braelyn Monroe",50000, 30, "Software Developer", "BM@abc.com"),
                new Employee("2","Emersyn Stewart",70000, 40, "Application Developer", "ES@abc.com")
        );
        employee = employeeList.get(0);
    }

    @Test
    void getAllEmployees_Success() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(employeeList);

        String getAllEmployeesUrl = baseUrl + "/employee";

        mockMvc.perform(get(getAllEmployeesUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(employeeList)));
    }

    @Test
    void getAllEmployees_RestClientError() throws Exception {
        when(employeeService.getAllEmployees()).thenThrow(new CustomException(CustomError.FEIGN_CLIENT_ERROR));

        String getAllEmployeesUrl = baseUrl + "/employees";
        mockMvc.perform(get(getAllEmployeesUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"E001\",\"message\":\"Error while making a Rest API call\"}"));
    }

    @Test
    void getEmployeesByNameSearch_Success() throws Exception {
        employeeList = Arrays.asList(
                new Employee("1","Braelyn Monroe",50000, 30, "Software Developer", "BM@abc.com"),
                new Employee("2","Braelyn Stewart",70000, 40, "Application Developer", "ES@abc.com")
        );
        when(employeeService.getAllEmployeesBySearchName("Braelyn")).thenReturn(employeeList);
        String employeeName = "Braelyn";

        String getAllEmployeesUrl = baseUrl + "/employee/search/{searchString}";
        mockMvc.perform(get(getAllEmployeesUrl, employeeName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(employeeList)));
    }

    @Test
    void getHighestSalaryOfEmployees() throws Exception {
        Integer highestSalaryOf= 70000;
        when(employeeService.getEmployeeWithHighestSalary()).thenReturn(highestSalaryOf);

        String getAllEmployeesUrl = baseUrl + "/employee/highest-salary";
        mockMvc.perform(get(getAllEmployeesUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(highestSalaryOf)));
    }

    @Test
    void getTopTenHighestEarningEmployeeNames_Success() throws Exception {
        List<String> expectedTop10HighestEarningEmployeeNames = List.of(
                "Ashlynn Rowe",
                "Amirah Hendricks",
                "Coraline Weaver",
                "Brinley Giles",
                "Ayleen Greene",
                "Maya Rowland",
                "Magdalena Richards",
                "Gracie Drake",
                "Annalise Salas",
                "Haven Martin"
        );
        when(employeeService.getTopTenHighestEarningEmployees()).thenReturn(expectedTop10HighestEarningEmployeeNames);

        String getAllEmployeesUrl = baseUrl + "/employee/top-10-highest-earning";
        mockMvc.perform(get(getAllEmployeesUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedTop10HighestEarningEmployeeNames)));
    }

    @Test
    void getEmployeeById_Success() throws Exception {
        when(employeeService.getEmployeeById("1")).thenReturn(employee);

        String getEmployeeByIdUrl = baseUrl + "/employee/1";
        mockMvc.perform(get(getEmployeeByIdUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(employee)));
    }

    @Test
    void addEmployee_Success() throws Exception {
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("name", "John Doe");
        employeeInput.put("salary", 50000);
        employeeInput.put("age", 30);
        employeeInput.put("title", "Developer");

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(employeeInput);

        String response = """
            {
                "status": "Successfully processed request.",
                "data": {
                    "id": "84a33498-a539-4d8e-bcdb-bfbce4278612",
                    "employee_name": "Shane",
                    "employee_salary": 5000,
                    "employee_age": 30,
                    "employee_title": "Accountant",
                    "employee_email": "xxxdirtydanxxx@company.com"
                }
            }
            """;

        EmployeeResponse employeeResponse = objectMapper.readValue(response, EmployeeResponse.class);

        when(employeeService.createEmployee(employeeInput)).thenReturn(employeeResponse);

        String createEmployeeUrl = baseUrl + "/create";
        mockMvc.perform(post(createEmployeeUrl)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(employeeResponse)));
    }

    @Test
    void addEmployee_nameValidationException() throws Exception {
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("salary", 50000);
        employeeInput.put("age", 30);
        employeeInput.put("title", "Developer");

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(employeeInput);

        when(employeeService.createEmployee(employeeInput)).thenThrow(new ValidationException(CustomError.INVALID_OR_MISSING_NAME));

        String createEmployeeUrl = baseUrl + "/create";
        mockMvc.perform(post(createEmployeeUrl)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"E004\",\"message\":\"Invalid or missing name\"}"));
    }

    @Test
    void addEmployee_salaryValidationException() throws Exception {
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("name", "john Doe");
        employeeInput.put("age", 30);
        employeeInput.put("title", "Developer");

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(employeeInput);

        when(employeeService.createEmployee(employeeInput)).thenThrow(new ValidationException(CustomError.INVALID_OR_MISSING_SALARY));

        String createEmployeeUrl = baseUrl + "/create";
        mockMvc.perform(post(createEmployeeUrl)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"E005\",\"message\":\"Invalid or missing salary\"}"));
    }

    @Test
    void addEmployee_minimumSalaryValidationException() throws Exception {
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("name", "john Doe");
        employeeInput.put("age", 30);
        employeeInput.put("salary", -50000);
        employeeInput.put("title", "Developer");

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(employeeInput);

        when(employeeService.createEmployee(employeeInput)).thenThrow(new ValidationException(CustomError.MINIMUM_SALARY_ERROR));

        String createEmployeeUrl = baseUrl + "/create";
        mockMvc.perform(post(createEmployeeUrl)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"E006\",\"message\":\"Salary cannot be less than zero\"}"));
    }

    @Test
    void addEmployee_ageValidationException() throws Exception {
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("name", "John Doe");
        employeeInput.put("salary", 50000);
        employeeInput.put("title", "Developer");

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(employeeInput);

        when(employeeService.createEmployee(employeeInput)).thenThrow(new ValidationException(CustomError.INVALID_OR_MISSING_AGE));

        String createEmployeeUrl = baseUrl + "/create";
        mockMvc.perform(post(createEmployeeUrl)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"E007\",\"message\":\"Invalid or missing age\"}"));
    }

    @Test
    void addEmployee_minimumAgeValidationException() throws Exception {
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("name", "John Doe");
        employeeInput.put("salary", 50000);
        employeeInput.put("title", "Developer");
        employeeInput.put("age", 10);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(employeeInput);

        when(employeeService.createEmployee(employeeInput)).thenThrow(new ValidationException(CustomError.MINIMUM_AGE_ERROR));

        String createEmployeeUrl = baseUrl + "/create";
        mockMvc.perform(post(createEmployeeUrl)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"E008\",\"message\":\"Age cannot be less than 16\"}"));
    }

    @Test
    void addEmployee_maximumAgeValidationException() throws Exception {
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("name", "John Doe");
        employeeInput.put("salary", 50000);
        employeeInput.put("title", "Developer");
        employeeInput.put("age", 100);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(employeeInput);

        when(employeeService.createEmployee(employeeInput)).thenThrow(new ValidationException(CustomError.MAXIMUM_AGE_ERROR));

        String createEmployeeUrl = baseUrl + "/create";
        mockMvc.perform(post(createEmployeeUrl)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"E009\",\"message\":\"Age cannot be more than 75\"}"));
    }

    @Test
    void addEmployee_titleValidationException() throws Exception {
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("name", "John Doe");
        employeeInput.put("salary", 50000);
        employeeInput.put("age", 30);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(employeeInput);

        when(employeeService.createEmployee(employeeInput)).thenThrow(new ValidationException(CustomError.INVALID_OR_MISSING_TITLE));

        String createEmployeeUrl = baseUrl + "/create";
        mockMvc.perform(post(createEmployeeUrl)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"E0011\",\"message\":\"Invalid or missing title\"}"));
    }

    @Test
    void deleteEmployeeById_Success() throws Exception {
        String id = "1";
        String employeeName = "Braelyn Monroe";

        when(employeeService.deleteEmployeeById(id)).thenReturn(employeeName);

        String deleteEmployeeUrl = baseUrl + "/employee/{id}";
        mockMvc.perform(delete(deleteEmployeeUrl, id))
                .andExpect(status().isOk())
                .andExpect(content().string(employeeName));
    }
}

