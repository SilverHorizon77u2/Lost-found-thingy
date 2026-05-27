package org.example.lfrs_group_4_oop.service;

import org.example.lfrs_group_4_oop.dto.DashboardMetrics;
import org.example.lfrs_group_4_oop.dto.MonthlyStatusCount;
import org.example.lfrs_group_4_oop.dto.StatusTrend;
import org.example.lfrs_group_4_oop.dto.TrendMetrics;
import org.example.lfrs_group_4_oop.entity.Item;
import org.example.lfrs_group_4_oop.model.ItemModel;
import org.example.lfrs_group_4_oop.repository.ItemRepository;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for calculating report metrics and overview data.
 */
public record ReportService(ItemRepository itemRepository) {

    /**
     * Calculates metrics directly from a provided list of items in-memory.
     * This avoids hitting the database for filtered views.
     */
    public DashboardMetrics calculateMetrics(List<Item> currentItems) {
        if (currentItems == null || currentItems.isEmpty()) {
            return new DashboardMetrics(0, 0, 0, 0, 0);
        }

        long total = currentItems.size();
        long lost = currentItems.stream()
                .filter(item -> ItemModel.STATUS_LOST.equalsIgnoreCase(item.getStatus()))
                .count();
        long found = currentItems.stream()
                .filter(item -> ItemModel.STATUS_FOUND.equalsIgnoreCase(item.getStatus()))
                .count();
        long claimed = currentItems.stream()
                .filter(item -> ItemModel.STATUS_CLAIMED.equalsIgnoreCase(item.getStatus()))
                .count();
        // Maintain existing logic where unclaimed is equivalent to found for this specific system
        long unclaimed = found;

        return new DashboardMetrics(total, lost, found, claimed, unclaimed);
    }

    private List<Item> getItems(Integer reporterId) {
        if (reporterId == null) {
            return itemRepository.findAll();
        } else {
            return itemRepository.searchWithFilters(null, null, null, null, null, reporterId);
        }
    }

    public long getTotalReports() {
        return getTotalReports(null);
    }

    public long getTotalReports(Integer reporterId) {
        return getItems(reporterId).size();
    }

    public long getLostItemsCount() {
        return getLostItemsCount(null);
    }

    public long getLostItemsCount(Integer reporterId) {
        return countByStatus(ItemModel.STATUS_LOST, reporterId);
    }

    public long getFoundItemsCount() {
        return getFoundItemsCount(null);
    }

    public long getFoundItemsCount(Integer reporterId) {
        return countByStatus(ItemModel.STATUS_FOUND, reporterId);
    }

    public long getClaimedItemsCount() {
        return getClaimedItemsCount(null);
    }

    public long getClaimedItemsCount(Integer reporterId) {
        return countByStatus(ItemModel.STATUS_CLAIMED, reporterId);
    }

    public long getUnclaimedItemsCount() {
        return getUnclaimedItemsCount(null);
    }

    public long getUnclaimedItemsCount(Integer reporterId) {
        return getFoundItemsCount(reporterId);
    }

    private long countByStatus(String status, Integer reporterId) {
        return getItems(reporterId).stream()
                .filter(item -> status.equalsIgnoreCase(item.getStatus()))
                .count();
    }

    private static final DateTimeFormatter YEAR_MONTH_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * Calculates month-over-month trend metrics using an optimized SQL aggregation query.
     *
     * @param reporterId Optional reporter ID for "My Reports" filtering (null for all items).
     * @return TrendMetrics with percentage changes for all status types.
     */
    public TrendMetrics calculateTrends(Integer reporterId) {
        YearMonth currentMonth = YearMonth.now();
        YearMonth previousMonth = currentMonth.minusMonths(1);
        String currentKey = currentMonth.format(YEAR_MONTH_FMT);
        String previousKey = previousMonth.format(YEAR_MONTH_FMT);

        List<MonthlyStatusCount> counts =
                itemRepository.getMonthlyStatusCounts(currentKey, previousKey, reporterId);

        // Build lookup: monthKey -> status -> count
        Map<String, Map<String, Long>> lookup = counts.stream()
                .collect(Collectors.groupingBy(
                        MonthlyStatusCount::monthKey,
                        Collectors.toMap(
                                MonthlyStatusCount::status,
                                MonthlyStatusCount::count)));

        Map<String, Long> currentCounts = lookup.getOrDefault(currentKey, Map.of());
        Map<String, Long> previousCounts = lookup.getOrDefault(previousKey, Map.of());

        StatusTrend lostTrend = buildTrend(currentCounts, previousCounts, ItemModel.STATUS_LOST);
        StatusTrend foundTrend = buildTrend(currentCounts, previousCounts, ItemModel.STATUS_FOUND);
        StatusTrend claimedTrend = buildTrend(currentCounts, previousCounts, ItemModel.STATUS_CLAIMED);

        // Total = sum of all statuses for each month
        long currentTotal = currentCounts.values().stream().mapToLong(Long::longValue).sum();
        long previousTotal = previousCounts.values().stream().mapToLong(Long::longValue).sum();
        StatusTrend totalTrend = new StatusTrend(currentTotal, previousTotal,
                computePercentageChange(currentTotal, previousTotal));

        // Unclaimed mirrors Found
        return new TrendMetrics(totalTrend, lostTrend, foundTrend, claimedTrend, foundTrend);
    }

    private StatusTrend buildTrend(Map<String, Long> current, Map<String, Long> previous, String status) {
        long cur = current.getOrDefault(status, 0L);
        long prev = previous.getOrDefault(status, 0L);
        return new StatusTrend(cur, prev, computePercentageChange(cur, prev));
    }

    private double computePercentageChange(long current, long previous) {
        if (previous == 0) {
            return current == 0 ? 0 : Double.NaN;
        }
        return ((double) (current - previous) / previous) * 100;
    }
}
