package com.Ride_Share_lite.demo.repository;

import com.Ride_Share_lite.demo.model.RideOffer;
import com.Ride_Share_lite.demo.model.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RideOfferRepository extends JpaRepository<RideOffer, Long> {

    List<RideOffer> findByDriverIdOrderByDepartureTimeDesc(Long driverId);

    /** Matching: same route (partial, case-insensitive) inside the rider's time window, with seats left. */
    @Query("SELECT r FROM RideOffer r WHERE r.status = :status AND r.availableSeats > 0 "
            + "AND LOWER(r.origin) LIKE LOWER(CONCAT('%', :origin, '%')) "
            + "AND LOWER(r.destination) LIKE LOWER(CONCAT('%', :destination, '%')) "
            + "AND r.departureTime BETWEEN :from AND :to ORDER BY r.departureTime")
    List<RideOffer> search(@Param("status") RideStatus status,
                           @Param("origin") String origin,
                           @Param("destination") String destination,
                           @Param("from") LocalDateTime from,
                           @Param("to") LocalDateTime to);
}
