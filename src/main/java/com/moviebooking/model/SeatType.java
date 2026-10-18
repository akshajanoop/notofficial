package com.moviebooking.model;

/**
 * Standard Seat Categories in the Theatre:
 * - STANDARD: Front rows - Clear close-up screen view.
 * - PREMIUM: Center / Middle rows - Optimal eye-level viewing sweet spot.
 * - RECLINER: Back rows - Luxury motorized recliners with panoramic screen view.
 */
public enum SeatType {
    STANDARD("Standard", "#0d6efd", "Front Section: Immersive close-up screen view"),
    PREMIUM("Premium", "#ffc107", "Prime Center: Best eye-level viewing angle & acoustic sweet spot ⭐⭐⭐"),
    RECLINER("Recliner", "#6f42c1", "Rear VIP: Maximum luxury motorized recliners with full panoramic view ⭐⭐⭐⭐⭐");

    private final String displayName;
    private final String colorCode;
    private final String comfortDescription;

    SeatType(String displayName, String colorCode, String comfortDescription) {
        this.displayName = displayName;
        this.colorCode = colorCode;
        this.comfortDescription = comfortDescription;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColorCode() {
        return colorCode;
    }

    public String getComfortDescription() {
        return comfortDescription;
    }
}
