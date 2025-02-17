package com.reliaquest.api.service;

import com.reliaquest.api.exception.CustomError;
import com.reliaquest.api.exception.CustomException;
import com.reliaquest.api.exception.ValidationException;
import com.reliaquest.api.external.EmployeeClient;
import com.reliaquest.api.models.*;
import com.reliaquest.api.validator.CreateEmployeeInputValidation;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import io.github.resilience4j.retry.annotation.Retry;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeClient employeeClient;

    @Retry(name = "employeeServiceRetry", fallbackMethod = "fallbackGetAllEmployees")
    public List<Employee> getAllEmployees(){
        List<Employee> employeeResponseList;
        log.info("Calling external service");
        employeeResponseList = employeeClient.getAllEmployees().getData();
        if(employeeResponseList.isEmpty()){
            throw new CustomException(CustomError.NO_DATA_FOUND);
        }
        log.info("Successfully fetched List of all employees: {}", employeeResponseList);
        return employeeResponseList;
    }

    public List<Employee> getAllEmployeesBySearchName(String name){
        List<Employee> employeesFoundByName = getAllEmployees().stream()
                .filter(employee -> employee.getName().contains(name))
                .toList();
        if(employeesFoundByName.isEmpty()){
            throw new CustomException(CustomError.EMPLOYEE_WITH_GIVEN_NAME_NOT_FOUND);
        }
        log.info("Successfully fetched Employees whose name contains the {} are : {}",name, employeesFoundByName);
        return employeesFoundByName;
    }

    @Retry(name = "employeeServiceRetry", fallbackMethod = "fallbackGetAllEmployees")
    public Employee getEmployeeById(String id){
        if(id == null || id.isEmpty())
            throw new ValidationException(CustomError.ID_CAN_NOT_BE_NULL);
        Employee employee;
        log.info("Calling external service");
        employee = employeeClient.getEmployeeById(id).getData();
        if(employee == null)
            throw new CustomException(CustomError.NO_DATA_FOUND);
        log.info("Successfully found Employee with id : {}", employee);
        return employee;
    }

    public Integer getEmployeeWithHighestSalary(){
        Integer highestSalary = getAllEmployees().stream()
                .mapToInt(Employee::getSalary)
                .max()
                .orElse(0);
        log.info("Highest earning employee salary is :{}", highestSalary);
        return highestSalary;
    }

    public List<String> getTopTenHighestEarningEmployees(){
        List<String> highestEarningEmmployeeList =  getAllEmployees().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getSalary(), e1.getSalary()))
                .limit(10)
                .map(Employee::getName)
                .collect(Collectors.toList());
        log.info("Top 10 highest earning employees are : {}", highestEarningEmmployeeList);
        return highestEarningEmmployeeList;
    }

    public Employee createEmployee(Map<String, Object> employeeMap){
        log.info("Calling external service");
        try{
            CreateEmployeeDTO createEmployeeDTO = CreateEmployeeInputValidation.ValidateInput(employeeMap);
            EmployeeResponse createdEmployee = employeeClient.createEmployee(createEmployeeDTO);
            log.info("Successfully created Employee with id : {}", createdEmployee);
            return createdEmployee.getData();
        }catch (FeignException e) {
            log.error("Error creating employee: {}", e.getMessage());
            throw new CustomException(CustomError.FEIGN_CLIENT_ERROR);
        }
    }

    public String deleteEmployeeById(String id){
        if(id == null){
            throw new ValidationException(CustomError.ID_CAN_NOT_BE_NULL);
        }
        Employee employee = getEmployeeById(id);
        DeleteEmployeeDTO deleteEmployeeDTO = new DeleteEmployeeDTO(employee.getName());
        log.info("Calling external service");
        try{
            employeeClient.deleteEmployeeById(deleteEmployeeDTO);
            log.info("Successfully deleted employee with id : {}", id);
            return employee.getName();
        }catch (FeignException e) {
            log.error("Error deleting employee: {}", e.getMessage());
            throw new CustomException(CustomError.FEIGN_CLIENT_ERROR);
        }
    }

    //fallback method when retry fails.
    public void fallbackGetAllEmployees(FeignException e) throws CustomException{
        log.error("Feign client exception : {}",e.getMessage());
        throw new CustomException(CustomError.RETRY_ERROR);
    }
}
