package com.flipkart.sample.retreat.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.sample.core.managed.redis.Redis;
import com.flipkart.sample.models.Employee;
import com.flipkart.sample.models.RedisOperations;
import com.flipkart.sample.models.RedisPartitionNamespace;
import com.google.inject.Inject;
import com.yammer.tenacity.core.TenacityCommand;
import com.yammer.tenacity.core.properties.TenacityPropertyKey;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

public class SaveEmployeeCommand extends TenacityCommand<Boolean> {

    private final Redis redisPool;
    private final ObjectMapper mapper;
    private final Employee employee;

    @Inject
    public SaveEmployeeCommand(TenacityPropertyKey tenacityPropertyKey, Redis redisPool, ObjectMapper mapper, Employee employee) {
        super(tenacityPropertyKey);
        this.redisPool = redisPool;
        this.mapper = mapper;
        this.employee = employee;
    }

    @Override
    protected Boolean run() throws Exception {
        Jedis jedis = redisPool.getResource(RedisOperations.WRITE,employee.getEmployeeId());
        String employeeString  = mapper.writeValueAsString(employee);
        Map<String,String> map = new HashMap<String,String>();
        map.put(employee.getEmployeeId(),employeeString);
        try{
            jedis.hmset("employee",map);
        }catch (Exception e){
            return false;
        }
        return true;
    }
}
