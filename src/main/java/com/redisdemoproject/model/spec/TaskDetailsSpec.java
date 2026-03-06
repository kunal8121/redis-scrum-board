package com.redisdemoproject.model.spec;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.redisdemoproject.model.FeatureType;
import com.redisdemoproject.model.Severity;

import io.micronaut.core.annotation.Introspected;
import lombok.Builder;


public sealed interface TaskDetailsSpec {

    @Builder
    @Introspected
    public record BugDetailsSpec(
            @JsonProperty("severity") Severity severity,
            @JsonProperty("description") String description
    ) implements TaskDetailsSpec {
    }


    @Builder
    @Introspected
    public record FeatureDetailsSpec(
            @JsonProperty("type") FeatureType featureType,
            @JsonProperty("description") String description
    ) implements TaskDetailsSpec {
    }
}
