package org.example.lfrs_group_4_oop.repository;

import org.example.lfrs_group_4_oop.dto.MonthlyStatusCount;
import org.example.lfrs_group_4_oop.entity.Item;
import java.util.List;

/**
 * Contract for Item-related data operations.
 */
public interface ItemRepository extends BaseRepository<Item> {
    /**
     * Searches for items by description keywords.
     * 
     * @param keyword The search keyword.
     * @return List of matching items.
     */
    List<Item> searchByDescription(String keyword);
    
    List<Item> findByStatus(String status);
    
    /**
     * Searches for items using combined optional filters.
     */
    List<Item> searchWithFilters(String keyword, String status, Integer categoryId, Integer year, Integer month, Integer reporterId);
    
    void updateStatus(Integer id, String status);

    /**
     * Returns item counts grouped by month and status for two specified months.
     * Uses a single SQL query with GROUP BY for optimal performance.
     *
     * @param currentMonthKey  Current month in "YYYY-MM" format.
     * @param previousMonthKey Previous month in "YYYY-MM" format.
     * @param reporterId       Optional reporter ID filter (null for all items).
     * @return List of MonthlyStatusCount rows.
     */
    List<MonthlyStatusCount> getMonthlyStatusCounts(
            String currentMonthKey, String previousMonthKey, Integer reporterId);
}
