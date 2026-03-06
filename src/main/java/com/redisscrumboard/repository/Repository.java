package com.redisscrumboard.repository;

import com.redisscrumboard.model.QueryOptions;

import java.util.List;
import java.util.UUID;

public interface Repository<T> {

    T save(T entity);

    T update(String id, T entity);

    T getById(UUID id);

    List<T> findAll(int pageSize, int offset);

    List<T> query(QueryOptions queryOptions);

    void delete(String id);
}
