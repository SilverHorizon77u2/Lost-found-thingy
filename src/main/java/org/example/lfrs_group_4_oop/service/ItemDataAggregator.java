package org.example.lfrs_group_4_oop.service;

import org.example.lfrs_group_4_oop.dto.ItemDisplayDto;
import org.example.lfrs_group_4_oop.entity.Category;
import org.example.lfrs_group_4_oop.entity.Item;
import org.example.lfrs_group_4_oop.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Ensuring that the UI layer receives descriptive names instead of raw IDs.
 */
public record ItemDataAggregator(CategoryRepository categoryRepository) {

    /**
     * @param item The item entity to convert.
     * @return A UI-ready DTO.
     */
    public ItemDisplayDto aggregate(Item item) {
        if (item == null) return null;

        String categoryName = "Uncategorized";
        if (item.getCategoryId() != null) {
            Category category = categoryRepository.findById(item.getCategoryId());
            if (category != null) {
                categoryName = category.getCategoryName();
            }
        }

        return new ItemDisplayDto.Builder()
                .id(item.getId())
                .title(item.getTitle())
                .description(item.getDescription())
                .imagePath(item.getImagePath())
                .categoryName(categoryName)
                .status(item.getStatus())
                .location(item.getLocation())
                .rawDate(item.getDateReported())
                .reporterName(item.getReporterName())
                .build();
    }

    /**
     * @param items The list of item entities.
     * @return A list of UI-ready DTOs.
     */
    public List<ItemDisplayDto> aggregateList(List<Item> items) {
        if (items == null || items.isEmpty()) return new ArrayList<>();

        // Fetch all categories once to build a lookup map (Optimization)
        Map<Integer, String> categoryMap = categoryRepository.findAll().stream()
                .collect(Collectors.toMap(Category::getId, Category::getCategoryName));

        return items.stream()
                .map(item -> {
                    String categoryName = categoryMap.getOrDefault(item.getCategoryId(), "Uncategorized");
                    return new ItemDisplayDto.Builder()
                            .id(item.getId())
                            .title(item.getTitle())
                            .description(item.getDescription())
                            .imagePath(item.getImagePath())
                            .categoryName(categoryName)
                            .status(item.getStatus())
                            .location(item.getLocation())
                            .rawDate(item.getDateReported())
                            .reporterName(item.getReporterName())
                            .build();
                })
                .toList();
    }
}
