package com.flipkart.sample;

import com.flipkart.sample.core.configuration.RedisConfiguration;
import com.flipkart.sample.core.configuration.SampleConfiguration;
import com.flipkart.sample.repository.EmployeeRepository;
import com.flipkart.sample.repository.EmployeeRepositoryImpl;
import com.flipkart.sample.services.EmployeeService;
import com.flipkart.sample.services.EmployeeServiceImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class SampleModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(EmployeeService.class).to(EmployeeServiceImpl.class);
        bind(EmployeeRepository.class).to(EmployeeRepositoryImpl.class);
    }

    @Provides @Singleton
    public RedisConfiguration provideRedisConfiguration(SampleConfiguration configuration) {
        return configuration.getRedis();
    }

}
