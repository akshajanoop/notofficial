package com.moviebooking.model;

/**
 * Enum representing the current booking state of a seat.
 *
 * State machine:
 *   AVAILABLE --> LOCKED   (when user starts checkout)
 *   LOCKED    --> AVAILABLE (if checkout is abandoned / timeout)
 *   LOCKED    --> RESERVED  (when payment is confirmed)
 *   AVAILABLE --> RESERVED  (direct, for auction winners)
 */
public enum SeatStatus {
    /** Seat is free and can be selected by any user */
    AVAILABLE,

    /**
     * Seat is temporarily held for a specific user during the checkout flow.
     * Lock expires after a short window (handled by a scheduler).
     */
    LOCKED,

    /** Seat is permanently booked - payment confirmed */
    RESERVED
}
