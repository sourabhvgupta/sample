package com.flipkart.sample.services;

import com.flipkart.sample.models.Employee;

public interface EmployeeService {

    public Employee getEmployee(String employeeId);
    public Boolean saveEmployee(Employee employee);
}
