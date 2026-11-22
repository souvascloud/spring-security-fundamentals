package com.souvaniksaha.spring_security_fundamentals.service;

import com.souvaniksaha.spring_security_fundamentals.dto.response.EmployeeResponse;
import com.souvaniksaha.spring_security_fundamentals.exception.EmployeeNotFoundException;
import com.souvaniksaha.spring_security_fundamentals.model.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

/*
 * Copyright (c) 2026 Souvanik Saha
 *
 * Licensed under the MIT License.
 * https://opensource.org/licenses/MIT
 */
@Service
public class EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    private final List<Employee> employees = List.of(
            new Employee(101L, "Rahul", "Engineering"),
            new Employee(102L, "Priya", "Finance"),
            new Employee(103L, "Amit", "Operations")
    );

    public List<EmployeeResponse> getAllEmployees() {

        log.info("Fetching all employees. Total: {}", employees.size());

        return employees.stream()
                .map(this::toResponse)
                .toList();
    }

    public EmployeeResponse getEmployeeById(Long id) {

        log.debug("Fetching employee with ID: {}", id);

        Employee employee = employees.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        return toResponse(employee);
    }

    private EmployeeResponse toResponse(Employee employee) {

        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getDepartment()
        );
    }
}