package org.example.lfrs_group_4_oop.entity;

import java.time.LocalDateTime;

/**
 * Represents a lost or found item.
 */
public class Item extends BaseEntity {
    private String title;
    private String description;
    private String imagePath;
    private LocalDateTime dateReported;
    private String status;
    private String location;
    private Integer categoryId;
    private Integer reporterId;
    private String reporterName;

    //gabs
    private MapZone mapZone;
    public MapZone getMapZone() {
        return this.mapZone;
    }
    public void setMapZone(MapZone mapZone) {
        this.mapZone = mapZone;
    }

    /**
     * Default constructor for reflection and persistence frameworks.
     */
    public Item() {
        // Empty constructor required for reflection and persistence frameworks
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public LocalDateTime getDateReported() {
        return dateReported;
    }

    public void setDateReported(LocalDateTime dateReported) {
        this.dateReported = dateReported;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getReporterId() {
        return reporterId;
    }

    public void setReporterId(Integer reporterId) {
        this.reporterId = reporterId;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", dateReported=" + dateReported +
                ", status='" + status + '\'' +
                ", location='" + location + '\'' +
                ", categoryId=" + categoryId +
                ", reporterId=" + reporterId +
                '}';
    }
}
