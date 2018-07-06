package com.flipkart.sample.core.managed.redis;

import com.google.common.collect.Iterators;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.util.Pool;

import java.util.Iterator;
import java.util.List;

public class SlavePool extends Pool<Jedis> {

    private static final int DEFAULT_DATABASE = 0;

    public SlavePool(final GenericObjectPoolConfig poolConfig, List<JedisShardInfo> shards, int database) {
        super(poolConfig, new RoundRobinFactory(shards, database));
    }

    @Override
    public Jedis getResource() {
        Jedis jedis = super.getResource();
        jedis.setDataSource(this);
        return jedis;
    }

    private static class RoundRobinFactory implements PooledObjectFactory<Jedis> {
        private final List<JedisShardInfo> shards;
        private final Iterator<JedisShardInfo> shardIterator;
        private int database = 0;

        public RoundRobinFactory(List<JedisShardInfo> shards, int database) {
            this.shards = shards;
            this.shardIterator = Iterators.cycle(this.shards);
            this.database = database;
        }

        public PooledObject<Jedis> makeObject() throws Exception {
            JedisShardInfo jsi;
            synchronized (shardIterator) {
                jsi = Iterators.getNext(shardIterator, shards.get(0));
            }

            Jedis jedis = new Jedis(jsi.getHost(), jsi.getPort());
            if(database != DEFAULT_DATABASE) jedis.select(database);
            return new DefaultPooledObject<>(jedis);
        }

        public void destroyObject(PooledObject<Jedis> jedis) {
            try {
                try {
                    jedis.getObject().quit();
                } catch (Exception e) {
                }
                jedis.getObject().disconnect();
            } catch (Exception e) {
            }
        }

        public boolean validateObject(PooledObject<Jedis> jedis) {
            try {
                return jedis.getObject().ping().equals("PONG");
            } catch (Exception ex) {
                return false;
            }
        }

        @Override
        public void activateObject(PooledObject<Jedis> p) throws Exception {

        }

        @Override
        public void passivateObject(PooledObject<Jedis> p) throws Exception {

        }
    }
}

