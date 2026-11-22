package com.souvaniksaha.spring_security_fundamentals.controller;

import com.souvaniksaha.spring_security_fundamentals.dto.response.ApiResponse;
import com.souvaniksaha.spring_security_fundamentals.dto.response.EmployeeResponse;
import com.souvaniksaha.spring_security_fundamentals.model.Employee;
import com.souvaniksaha.spring_security_fundamentals.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
 * Copyright (c) 2026 Souvanik Saha
 *
 * Licensed under the MIT License.
 * https://opensource.org/licenses/MIT
 */
@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getAllEmployees() {

        List<EmployeeResponse> employees =
                employeeService.getAllEmployees();

        return ResponseEntity.ok(
                ApiResponse.success("Employees fetched successfully", employees)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeById( @PathVariable Long id) {
        EmployeeResponse employee = employeeService.getEmployeeById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Employee fetched successfully", employee)
        );
    }
}