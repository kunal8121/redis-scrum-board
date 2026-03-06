package com.redisdemoproject.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


public sealed interface TaskDetails {
    @Data
    @Builder
    @Introspected
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public final class Bug implements TaskDetails{
       @JsonProperty("severity") Severity severity;
       @JsonProperty("description") String description;
    }

    @Data
    @Builder
    @Introspected
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public final class Feature implements TaskDetails{
        @JsonProperty("feature_type") FeatureType featureType;
        @JsonProperty("description") String description;
    }
}
