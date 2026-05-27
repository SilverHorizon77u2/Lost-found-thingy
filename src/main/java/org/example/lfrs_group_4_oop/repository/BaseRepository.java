package org.example.lfrs_group_4_oop.repository;

import java.util.List;

/**
 * Common interface for all repository operations.
 * @param <T> The entity type.
 */
public interface BaseRepository<T> {
    void save(T t);
    void update(T t);
    void delete(Integer id);
    T findById(Integer id);
    List<T> findAll();
}
