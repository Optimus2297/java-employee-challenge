package com.reliaquest.api.external;

import com.reliaquest.api.models.CreateEmployeeDTO;
import com.reliaquest.api.models.DeleteEmployeeDTO;
import com.reliaquest.api.models.EmployeeResponseList;
import com.reliaquest.api.models.EmployeeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "EmployeeAPI", url = "${employee.api.url}")
public interface EmployeeClient {

    @GetMapping("/employee")
    EmployeeResponseList getAllEmployees();

    @GetMapping("/employee/{id}")
    EmployeeResponse getEmployeeById(@PathVariable String id);

    @PostMapping("/employee")
    EmployeeResponse createEmployee(@RequestBody CreateEmployeeDTO createEmployeeDTO);

    @DeleteMapping("/employee")
    String deleteEmployeeById(@RequestBody DeleteEmployeeDTO body);
}
