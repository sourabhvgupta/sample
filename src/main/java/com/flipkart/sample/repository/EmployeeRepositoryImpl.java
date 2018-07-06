package com.flipkart.sample.repository;

import com.flipkart.sample.models.Employee;
import com.google.inject.Inject;

public class EmployeeRepositoryImpl implements EmployeeRepository{

    private final CommandRepository commandRepository;


    @Inject
    public EmployeeRepositoryImpl(CommandRepository commandRepository) {
        this.commandRepository = commandRepository;
    }


    @Override
    public Employee getEmployee(String employeeId) {
        return commandRepository.getGetEmployeeCommand(employeeId).execute();
    }

    @Override
    public Boolean saveEmployee(Employee employee) {
        return commandRepository.getSaveEmployeeCommand(employee).execute();
    }
}
