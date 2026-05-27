package org.example.lfrs_group_4_oop.dto;

/**
 * Trend data for a single status metric, encapsulating month-over-month comparison.
 *
 * @param currentCount     Count in the current month.
 * @param previousCount    Count in the previous month.
 * @param percentageChange Percentage change (positive = increase, negative = decrease).
 *                         {@link Double#NaN} when previous month has zero items but current has items.
 */
public record StatusTrend(long currentCount, long previousCount, double percentageChange) {

    /**
     * Returns a formatted display string for the UI trend indicator.
     * Includes both percentage and absolute count changes.
     * Examples: "↑ 50% (+2) vs last month", "↓ 33% (-1) vs last month",
     *           "— No change (10)", "↑ New (3 this month)", "— No data".
     *
     * @return Formatted trend string.
     */
    public String toDisplayString() {
        if (currentCount == 0 && previousCount == 0) {
            return "\u2014 No data";
        }
        if (Double.isNaN(percentageChange)) {
            return "\u2191 New (" + currentCount + " this month)";
        }
        if (percentageChange == 0) {
            return "\u2014 No change (" + currentCount + ")";
        }
        String arrow = percentageChange > 0 ? "\u2191" : "\u2193";
        long rounded = Math.round(Math.abs(percentageChange));
        long diff = currentCount - previousCount;
        String sign = diff > 0 ? "+" : "";
        return arrow + " " + rounded + "% (" + sign + diff + ") vs last month";
    }

    /**
     * Returns the CSS style class for coloring the trend label.
     *
     * @return "trend-up", "trend-down", or "trend-neutral".
     */
    public String getStyleClass() {
        if (Double.isNaN(percentageChange) || percentageChange > 0) {
            return "trend-up";
        }
        if (percentageChange < 0) {
            return "trend-down";
        }
        return "trend-neutral";
    }
}
