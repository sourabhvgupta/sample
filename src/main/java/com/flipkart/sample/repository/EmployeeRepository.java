package com.flipkart.sample.repository;

import com.flipkart.sample.models.Employee;

public interface EmployeeRepository {
    public Employee getEmployee(String employeeId);
    public Boolean saveEmployee(Employee employee);
}
