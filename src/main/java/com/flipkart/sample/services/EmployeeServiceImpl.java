package com.flipkart.sample.services;

import com.flipkart.sample.models.Employee;
import com.flipkart.sample.repository.EmployeeRepository;
import com.google.inject.Inject;

public class EmployeeServiceImpl implements EmployeeService {

    private EmployeeRepository employeeRepository;

    @Inject
    public EmployeeServiceImpl(EmployeeRepository employeeRepository){
        this.employeeRepository = employeeRepository;
    }


    @Override
    public Employee getEmployee(String employeeId) {
        return employeeRepository.getEmployee(employeeId);
    }

    @Override
    public Boolean saveEmployee(Employee employee) {
        return employeeRepository.saveEmployee(employee);
    }
}
