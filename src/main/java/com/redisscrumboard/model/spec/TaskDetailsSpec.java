package com.redisscrumboard.model.spec;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.redisscrumboard.model.FeatureType;
import com.redisscrumboard.model.Severity;

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
