package com.flipkart.sample.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.flipkart.sample.core.managed.redis.Redis;
import com.flipkart.sample.models.Employee;
import com.flipkart.sample.retreat.SampleCommand;
import com.flipkart.sample.retreat.commands.GetEmployeeCommand;
import com.flipkart.sample.retreat.commands.SaveEmployeeCommand;
import com.google.inject.Inject;

public class CommandRepository {

    private final Redis redis;
    private final ObjectMapper mapper;

    @Inject
    public CommandRepository(Redis redis,ObjectMapper mapper) {
        this.redis = redis;
        this.mapper = mapper;
    }


    public GetEmployeeCommand getGetEmployeeCommand(String employeeId){

        return new GetEmployeeCommand(SampleCommand.GET_EMPLOYEE_DETAILS,redis,mapper,employeeId);
    }

    public SaveEmployeeCommand getSaveEmployeeCommand(Employee employee){
        return new SaveEmployeeCommand(SampleCommand.SAVE_EMPLOYEE_DETAILS,redis,mapper,employee);
    }
}
