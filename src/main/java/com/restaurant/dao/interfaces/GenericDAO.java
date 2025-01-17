package com.restaurant.dao.interfaces;

import java.io.IOException;
import java.util.List;

/**
 * Generic DAO interface that defines common CRUD operations
 * @param <T> The type of entity
 * @param <ID> The type of entity's identifier
 */
public interface GenericDAO<T, ID> {
    T create(T entity) throws IOException;
    void update(T entity) throws IOException;
    void delete(ID id) throws IOException;
    List<T> findAll() throws IOException;
    T findById(ID id) throws IOException;
}
