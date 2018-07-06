package com.flipkart.sample.core.managed.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

@Getter
@Setter
@AllArgsConstructor
public class RedisPool {
    private JedisSentinelPool jedisPool;

    private SlavePool slavePool;

    public Jedis getMasterResource() {
        return jedisPool != null ? jedisPool.getResource() : null;
    }

    public Jedis getSlaveResource() {
        return slavePool != null ? slavePool.getResource() : null;
    }
}
