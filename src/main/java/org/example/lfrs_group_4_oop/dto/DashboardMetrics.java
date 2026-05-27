package org.example.lfrs_group_4_oop.dto;

/**
 * DTO for holding dashboard metrics dynamically calculated from a filtered list of items.
 */
public record DashboardMetrics(long total, long lost, long found, long claimed, long unclaimed) {}
