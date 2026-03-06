package com.redisscrumboard.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.redisscrumboard.model.FeatureType;
import com.redisscrumboard.model.Severity;

import io.micronaut.core.annotation.Introspected;
import lombok.Builder;

public sealed interface RestTaskDetailsResponse {

    @Builder
    @Introspected
    record RestBugResponse(
            @JsonProperty("severity") Severity severity,
            @JsonProperty("description") String description
    )implements RestTaskDetailsResponse {}

    @Builder
    @Introspected
    record RestFeatureResponse(
            @JsonProperty("type") FeatureType featureType,
            @JsonProperty("description") String description
    ) implements RestTaskDetailsResponse {}
}
