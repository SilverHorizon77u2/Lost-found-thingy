package org.example.lfrs_group_4_oop.dto;

/**
 * Represents a single row from the monthly status aggregation query.
 * Each instance maps a (monthKey, status) pair to a count.
 *
 * @param monthKey Year-month string in "YYYY-MM" format.
 * @param status   Item status (Lost, Found, Claimed).
 * @param count    Number of items matching this status in this month.
 */
public record MonthlyStatusCount(String monthKey, String status, long count) {}
