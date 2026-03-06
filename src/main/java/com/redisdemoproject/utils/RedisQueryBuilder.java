package com.redisdemoproject.utils;

import com.redisdemoproject.model.QueryOptions;
import jakarta.inject.Singleton;
import java.util.Map;

@Singleton
public class RedisQueryBuilder {

     public static String buildQuery(QueryOptions queryOptions) {
        Map<String, String> filters = queryOptions.getFilters();
         if (filters == null || filters.isEmpty()) {
             return "*";
         }
        StringBuilder query =  new StringBuilder();

        for(Map.Entry<String, String> filter: filters.entrySet()) {
            if (!query.isEmpty()) {
                query.append(" ");
            }
            query
                    .append("@")
                    .append(filter.getKey())
                    .append(":{")
                    .append(filter.getValue())
                    .append("}");
        }
        return query.toString();
    }
}
