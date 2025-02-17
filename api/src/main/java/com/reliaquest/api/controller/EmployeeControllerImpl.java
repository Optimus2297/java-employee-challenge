package com.reliaquest.api.controller;

import com.reliaquest.api.models.Employee;
import com.reliaquest.api.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class EmployeeControllerImpl implements IEmployeeController{

    private final EmployeeService employeeService;

    @GetMapping("/employee")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        log.info("calling api to get all employees");
        return new ResponseEntity<>(employeeService.getAllEmployees(),  HttpStatus.OK);
    }

    @GetMapping("/employee/search/{searchString}")
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String searchString) {
        log.info("calling api to get all employees whose name contains : {}", searchString);
        return new ResponseEntity<>(employeeService.getAllEmployeesBySearchName(searchString), HttpStatus.OK);
    }

    @GetMapping("/employee/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
        log.info("calling api to get employee with id : {}",id);
        return new ResponseEntity<>(employeeService.getEmployeeById(id), HttpStatus.OK);
    }

    @GetMapping("/employee/highest-salary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        log.info("calling api to get highest salary among all employees ");
        return new ResponseEntity<>(employeeService.getEmployeeWithHighestSalary(), HttpStatus.OK);
    }

    @GetMapping("/employee/top-10-highest-earning")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        log.info("calling api to get top ten highest salaried employees");
        return new ResponseEntity<>(employeeService.getTopTenHighestEarningEmployees(), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Employee> createEmployee(@RequestBody Object employeeInput) {
        log.info("calling api to create employee");
            Map<String, Object> employeeMap = (Map<String, Object>) employeeInput;
            Employee createEmployee = employeeService.createEmployee(employeeMap);
            return new ResponseEntity<>(createEmployee, HttpStatus.CREATED);
    }

    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable String id) {
        log.info("calling api to delete employee with id : {}",id);
        return new ResponseEntity<>(employeeService.deleteEmployeeById(id),HttpStatus.OK);
    }
}
