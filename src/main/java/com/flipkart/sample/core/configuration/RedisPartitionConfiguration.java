package com.flipkart.sample.core.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;


@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class RedisPartitionConfiguration implements Serializable {

    @NotEmpty
    @JsonProperty
    private String masterName;

    @Min(2)
    @Max(4000)
    @JsonProperty
    private int timeout = 1000;

    @Min(0)
    @JsonProperty
    private int database = 0;

    @Min(0)
    @Max(4096)
    @JsonProperty
    private int masterMaxThreads;

    @Min(0)
    @Max(4096)
    @JsonProperty
    private int slaveMaxThreads;
}
