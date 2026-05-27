package org.example.lfrs_group_4_oop.dto;

/**
 * Aggregated month-over-month trend data for all dashboard overview cards.
 *
 * @param totalTrend     Trend for total reports.
 * @param lostTrend      Trend for lost items.
 * @param foundTrend     Trend for found items.
 * @param claimedTrend   Trend for claimed items.
 * @param unclaimedTrend Trend for unclaimed items (mirrors found).
 */
public record TrendMetrics(
        StatusTrend totalTrend,
        StatusTrend lostTrend,
        StatusTrend foundTrend,
        StatusTrend claimedTrend,
        StatusTrend unclaimedTrend) {}
