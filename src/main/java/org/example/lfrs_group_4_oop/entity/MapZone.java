package org.example.lfrs_group_4_oop.entity;

/**
 * Type-safe constants representing the distinct selectable structural
 * campus layout regions matching the graphic overview map.
 */
public enum MapZone {
    BUILDING("Building (Main Complex)"),
    KUBOS_LEON_ARCILLAS("Kubos and Leon Arcillas"),
    PARKING_GATE("Parking and Gate"),
    NEAR_COURT("Near Court"),
    COURT("Court"),
    CANTEEN("Canteen"),
    ALL_ZONES("All Locations"); // Default choice for reset/unfiltered states

    private final String displayName;

    // Constructor mapping internal constant to readable UI text strings
    MapZone(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Helper to safely match string descriptions back to Enum constants.
     */
    public static MapZone fromDisplayName(String text) {
        for (MapZone zone : MapZone.values()) {
            if (zone.getDisplayName().equalsIgnoreCase(text)) {
                return zone;
            }
        }
        return ALL_ZONES;
    }
}