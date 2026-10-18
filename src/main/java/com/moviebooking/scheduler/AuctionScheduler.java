package com.moviebooking.scheduler;

import com.moviebooking.service.AuctionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Background Scheduler running every 10 seconds to detect expired auctions
 * and atomically award the winning seat to the highest bidder.
 */
@Component
public class AuctionScheduler {

    private final AuctionService auctionService;

    public AuctionScheduler(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    /**
     * Resolves all live auctions where end_time has elapsed.
     */
    @Scheduled(fixedRate = 10000)
    public void checkAndResolveAuctions() {
        auctionService.resolveExpiredAuctions();
    }
}
