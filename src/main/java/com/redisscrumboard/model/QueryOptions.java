package com.redisscrumboard.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder
@Data
public class QueryOptions {
    private int pageSize;
    private int  offset;
    private Map<String, String> filters;
}
