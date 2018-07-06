package com.flipkart.sample.core.managed.redis;

import com.flipkart.sample.core.configuration.RedisConfiguration;
import com.flipkart.sample.core.configuration.RedisPartitionConfiguration;
import com.flipkart.sample.models.RedisOperations;
import com.flipkart.sample.models.RedisPartitionNamespace;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import io.dropwizard.lifecycle.Managed;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import redis.clients.jedis.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Redis implements Managed {

    private RedisConfiguration redisConfiguration;
    private Map<RedisPartitionNamespace, RedisPool> partitions;

    @Inject
    public Redis(EventBus eventBus,RedisConfiguration redisConfiguration) {
        this.redisConfiguration = redisConfiguration;
        createFromConfig(redisConfiguration);
        if(eventBus != null) {
            eventBus.register(this);
        }
    }

    @Override
    public void start() throws Exception {
        //Do nothing
    }

    @Override
    public void stop() throws Exception {
        //Do nothing
    }

    private void createFromConfig(RedisConfiguration redisConfiguration) {
        this.partitions = getPartitionsPool(redisConfiguration);
    }

    private Map<RedisPartitionNamespace, RedisPool> getPartitionsPool(RedisConfiguration redisConfiguration){

        Map<RedisPartitionNamespace, RedisPool> partitionPool = Maps.newHashMap();
        for (RedisPartitionNamespace partitionNamespace : RedisPartitionNamespace.values()) {
            final JedisSentinelPool masterPool = getJedisSentinelPool(redisConfiguration,partitionNamespace);
            final SlavePool slavePool = getJedisSlavePool(redisConfiguration, partitionNamespace);

            if (masterPool != null || slavePool != null) {
                partitionPool.put(partitionNamespace, new RedisPool(masterPool, slavePool));
            }
        }
        return partitionPool;
    }

    private JedisSentinelPool getJedisSentinelPool(RedisConfiguration redisConfiguration,
                                                   RedisPartitionNamespace redisNamespace) {

        if(redisConfiguration.getPartitions().get(redisNamespace) != null) {
            int masterMaxThreads = redisConfiguration.getPartitions().get(redisNamespace).getMasterMaxThreads();

            int db = redisConfiguration.getPartitions().get(redisNamespace).getDatabase();
            int timeout = redisConfiguration.getPartitions().get(redisNamespace).getTimeout();

            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(masterMaxThreads);
            poolConfig.setJmxNameBase("redis.clients.jedis:type=JedisPool,name=");
            poolConfig.setJmxNamePrefix(redisNamespace.toString().toLowerCase());

            String storeName = getStoreName(redisConfiguration, redisNamespace);

            if (storeName == null || storeName.isEmpty()) {
                return null;
            }

            return new JedisSentinelPool(storeName, redisConfiguration.getSentinels(),
                    poolConfig, timeout, null, db);
        }
        return null;
    }

    private String getStoreName(RedisConfiguration redisConfiguration, RedisPartitionNamespace redisNamespace) {
        RedisPartitionConfiguration redisPartitionConfiguration = redisConfiguration.getPartitions().get(redisNamespace);
        if(redisPartitionConfiguration != null) {
            return redisConfiguration.getPartitions().get(redisNamespace).getMasterName();
        }
        return null;
    }

    private SlavePool getJedisSlavePool(RedisConfiguration redis,
                                        RedisPartitionNamespace redisNamespace){


        if(redisConfiguration.getPartitions().get(redisNamespace)!=null) {

            int slaveMaxThreads = redisConfiguration.getPartitions().get(redisNamespace).getSlaveMaxThreads();
            int timeout = redisConfiguration.getPartitions().get(redisNamespace).getTimeout();

            int db = redisConfiguration.getPartitions().get(redisNamespace).getDatabase();

            String masterName = getStoreName(redis, redisNamespace);

            if(masterName != null && !masterName.isEmpty()) {
                List<JedisShardInfo> shards = Lists.newArrayList();
                boolean slavesFound;
                for (String sentinel : redis.getSentinels()) {
                    final HostAndPort hap = toHostAndPort(Arrays.asList(sentinel.split(":")));

                    try (Jedis jedis = new Jedis(hap.getHost(), hap.getPort())) {
                        final List<Map<String, String>> slaves = jedis.sentinelSlaves(masterName);
                        slavesFound = !slaves.isEmpty();
                        for (Map<String, String> slaveInfo : slaves) {
                            String host = slaveInfo.get("ip");
                            Integer port = Integer.parseInt(slaveInfo.get("port"));
                            shards.add(new JedisShardInfo(host, port, timeout));
                        }
                    }
                    if (slavesFound)
                        break;
                }

                JedisPoolConfig poolConfig = new JedisPoolConfig();
                poolConfig.setMaxTotal(slaveMaxThreads);

                // The default maxIdle is 8.
                // If numActive < maxIdle < maxTotal, then only maxIdle number of slaves will be used. This will cause uneven load distribution

                poolConfig.setMinIdle(shards.size());
                poolConfig.setMaxIdle(shards.size());

                // To avoid eviction.
                poolConfig.setMinEvictableIdleTimeMillis(Long.MAX_VALUE);

                // A borrowObject() should timeout after 1sec.
                poolConfig.setMaxWaitMillis(1000);

                // The default lifo is true.
                // If true, the last returned Jedis object from the idleList will be used by borrowObject(). That'll cause greater load on that Jedis instance.
                // Setting it to false so that load is uniformly distributed among the idle
                // instances.
                poolConfig.setLifo(false);

                // SlavePool is NOT a JedisPool. Using the base name JedisPool only for
                // consistency with RedisNamespace WRITE and CONFIG.
                poolConfig.setJmxNameBase("redis.clients.jedis:type=JedisPool,name=");
                poolConfig.setJmxNamePrefix(redisNamespace.toString().toLowerCase());

                return new SlavePool(poolConfig, shards, db);
            }
        }
        return null;
    }

    private HostAndPort toHostAndPort(List<String> getMasterAddrByNameResult) {
        String host = getMasterAddrByNameResult.get(0);
        int port = Integer.parseInt(getMasterAddrByNameResult.get(1));
        return new HostAndPort(host, port);
    }

    public Jedis getResource(RedisOperations redisOperations, String employeeId){

        switch(redisOperations){
            case READ:
                return partitions.get(getRedisNamespace(employeeId)).getSlaveResource();
            case WRITE:
                return partitions.get(getRedisNamespace(employeeId)).getMasterResource();
        }

        return partitions.get(RedisPartitionNamespace.EVEN).getMasterResource();
    }

    public RedisPartitionNamespace getRedisNamespace(String employeeId){
        Integer key = Integer.parseInt(employeeId) % 2;
        switch(key){
            case 0:
                return RedisPartitionNamespace.EVEN;
            case 1:
                return RedisPartitionNamespace.ODD;
        }

        return RedisPartitionNamespace.EVEN;
    }
}
