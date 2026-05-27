package org.example.lfrs_group_4_oop.repository;

import org.example.lfrs_group_4_oop.entity.User;

/**
 * Contract for User-related data operations.
 */
public interface UserRepository extends BaseRepository<User> {
    User findByEmail(String email);
}
