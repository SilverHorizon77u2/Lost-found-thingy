package org.example.lfrs_group_4_oop.service;

import org.example.lfrs_group_4_oop.entity.Item;
import org.example.lfrs_group_4_oop.model.ItemModel;
import org.example.lfrs_group_4_oop.repository.ItemRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for matching lost items with found items based on description and category.
 */
public record ItemMatcherService(ItemRepository itemRepository) {

    /**
     * Finds potential matches for a lost item among the found items.
     * 
     * @param lostItem The lost item to find matches for.
     * @return A list of potential matching found items.
     */
    public List<Item> findMatches(Item lostItem) {
        if (lostItem == null || !ItemModel.STATUS_LOST.equalsIgnoreCase(lostItem.getStatus())) {
            return new ArrayList<>();
        }

        List<Item> allFoundItems = itemRepository.findAll().stream()
                .filter(item -> ItemModel.STATUS_FOUND.equalsIgnoreCase(item.getStatus()))
                .toList();

        return allFoundItems.stream()
                .filter(foundItem -> isPotentialMatch(lostItem, foundItem))
                .toList();
    }

    /**
     * Basic matching logic based on category and description keywords.
     */
    private boolean isPotentialMatch(Item lost, Item found) {
        // Must be same category if specified
        if (lost.getCategoryId() != null && found.getCategoryId() != null && !lost.getCategoryId().equals(found.getCategoryId())) {
            return false;
        }

        // Check if description shares any words (case-insensitive)
        String[] lostWords = lost.getDescription().toLowerCase().split("\\s+");
        String foundDesc = found.getDescription().toLowerCase();

        for (String word : lostWords) {
            if (word.length() > 3 && foundDesc.contains(word)) {
                return true;
            }
        }

        return false;
    }
}
