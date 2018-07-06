package com.flipkart.sample.core.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.NotEmpty;
import lombok.Setter;

import javax.validation.Valid;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SampleConfiguration extends Configuration{
    @NotEmpty
    @JsonProperty
    private String name;

    @Valid
    @JsonProperty
    private RedisConfiguration redis;
}
