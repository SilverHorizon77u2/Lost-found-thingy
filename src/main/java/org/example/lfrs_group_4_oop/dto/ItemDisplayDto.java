package org.example.lfrs_group_4_oop.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Data Transfer Object (DTO) for displaying Item information in the UI.
 * 
 * This class aggregates raw item data with category names and provides
 * pre-formatted strings for human-readable display, especially for dates.
 */
public class ItemDisplayDto {
    private final Integer id;
    private final String title;
    private final String description;
    private final String imagePath;
    private final String categoryName;
    private final String status;
    private final String location;
    private final String displayDate;
    private final String reporterName;
    private final String zoneName; // Add 'final' here

    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH);

    private ItemDisplayDto(Builder builder) {
        this.id = builder.id;
        this.title = (builder.title != null && !builder.title.trim().isEmpty()) ? builder.title : "Untitled Item";
        this.description = (builder.description != null) ? builder.description : "No description provided.";
        this.imagePath = builder.imagePath;
        this.categoryName = (builder.categoryName != null) ? builder.categoryName : "Uncategorized";
        this.status = builder.status;

        // Fallbacks for locations so old test entries don't throw null errors
        this.location = (builder.location != null) ? builder.location : "Unknown Location";
        this.zoneName = (builder.zoneName != null) ? builder.zoneName : "ALL_ZONES";

        this.displayDate = (builder.rawDate != null) ? builder.rawDate.format(DISPLAY_FORMATTER) : "N/A";
        this.reporterName = (builder.reporterName != null) ? builder.reporterName : "Anonymous";
    }
    public static class Builder {
        private Integer id;
        private String title;
        private String description;
        private String imagePath;
        private String categoryName;
        private String status;
        private String location;
        private String zoneName; // Add field
        private LocalDateTime rawDate;
        private String reporterName;

        public Builder id(Integer id) { this.id = id; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder imagePath(String imagePath) { this.imagePath = imagePath; return this; }
        public Builder categoryName(String categoryName) { this.categoryName = categoryName; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder location(String location) { this.location = location; return this; }
        public Builder rawDate(LocalDateTime rawDate) { this.rawDate = rawDate; return this; }
        public Builder reporterName(String reporterName) { this.reporterName = reporterName; return this; }

        // CORRECTED BUILDER METHOD
        public Builder zoneName(String zoneName) {
            this.zoneName = zoneName;
            return this; // RETURN THIS!
        }

        public ItemDisplayDto build() {
            return new ItemDisplayDto(this);
        }
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getStatus() {
        return status;
    }

    public String getLocation() {
        return location;
    }

    public String getDisplayDate() {
        return displayDate;
    }

    public String getReporterName() {
        return reporterName;
    }

    public String getZoneName() { return zoneName; }

    @Override
    public String toString() {
        return "ItemDisplayDto{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", status='" + status + '\'' +
                ", location='" + location + '\'' +
                ", displayDate='" + displayDate + '\'' +
                ", reporterName='" + reporterName + '\'' +
                '}';
    }
}
