package com.flipkart.sample;

import com.flipkart.sample.core.configuration.SampleConfiguration;
import com.google.inject.Stage;
import com.hubspot.dropwizard.guice.GuiceBundle;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class SampleApplication extends Application<SampleConfiguration> {

    public static void main(String[] args) throws Exception {
        new SampleApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<SampleConfiguration> bootstrap) {
        GuiceBundle<SampleConfiguration> guiceBundle = GuiceBundle.<SampleConfiguration>newBuilder()
                .addModule(new SampleModule())
                .enableAutoConfig(getClass().getPackage().getName())
                .setConfigClass(SampleConfiguration.class)
                .build(Stage.DEVELOPMENT);
        bootstrap.addBundle(guiceBundle);
    }

    @Override
    public void run(SampleConfiguration configuration, Environment environment) throws Exception {

    }
}
