package com.flipkart.sample.core.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flipkart.sample.models.RedisPartitionNamespace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class RedisConfiguration {
    @JsonProperty
    @NotEmpty
    Set<String> sentinels;

    @NotNull
    @Valid
    @JsonProperty
    private Map<RedisPartitionNamespace, RedisPartitionConfiguration> partitions;
}
