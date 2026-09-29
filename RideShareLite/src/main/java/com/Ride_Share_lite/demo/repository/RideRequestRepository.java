package com.Ride_Share_lite.demo.repository;

import com.Ride_Share_lite.demo.model.RequestStatus;
import com.Ride_Share_lite.demo.model.RideRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface RideRequestRepository extends JpaRepository<RideRequest, Long> {

    List<RideRequest> findByRiderIdOrderByRequestedAtDesc(Long riderId);

    List<RideRequest> findByRideOfferId(Long rideOfferId);

    boolean existsByRiderIdAndRideOfferIdAndStatusIn(Long riderId, Long rideOfferId, Collection<RequestStatus> statuses);
}
