package com.flipkart.sample.retreat.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.sample.core.managed.redis.Redis;
import com.flipkart.sample.models.Employee;
import com.flipkart.sample.models.RedisOperations;
import com.flipkart.sample.models.RedisPartitionNamespace;
import com.google.inject.Inject;
import com.sun.jersey.api.client.ClientResponse;
import com.yammer.tenacity.core.TenacityCommand;
import com.yammer.tenacity.core.properties.TenacityPropertyKey;
import io.dropwizard.servlets.assets.ResourceNotFoundException;
import redis.clients.jedis.Jedis;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

public class GetEmployeeCommand extends TenacityCommand<Employee> {

    private final Redis redisPool;
    private final ObjectMapper mapper;
    private final String employeeId;


    @Inject
    public GetEmployeeCommand(TenacityPropertyKey tenacityPropertyKey,Redis redisPool,ObjectMapper mapper,String employeeId) {
        super(tenacityPropertyKey);
        this.redisPool = redisPool;
        this.employeeId = employeeId;
        this.mapper = mapper;
    }

    @Override
    protected Employee run() throws Exception {
        Employee employee;
        Jedis jedis = redisPool.getResource(RedisOperations.READ,employeeId);
        try {
            List<String> employees = jedis.hmget("employee", employeeId);
            employee = mapper.readValue(employees.get(0), Employee.class);
        }catch (Exception e){
            throw new WebApplicationException(Response.status(ClientResponse.Status.NOT_FOUND).entity("Employee Not found").build());
        }
        return employee;
    }
}
