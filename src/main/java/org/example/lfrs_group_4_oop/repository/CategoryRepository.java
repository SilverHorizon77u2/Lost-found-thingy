package org.example.lfrs_group_4_oop.repository;

import org.example.lfrs_group_4_oop.entity.Category;

/**
 * Contract for Category-related data operations.
 */
public interface CategoryRepository extends BaseRepository<Category> {
    Category findByCategoryName(String categoryName);
}
