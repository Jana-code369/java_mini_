package com.Ride_Share_lite.demo.service;

import com.Ride_Share_lite.demo.dto.RideRequestCreate;
import com.Ride_Share_lite.demo.model.RideRequest;

import java.util.List;

public interface RideRequestService {
    RideRequest create(RideRequestCreate request);
    List<RideRequest> getAll();
    RideRequest getById(Long id);
    List<RideRequest> getByRide(Long rideOfferId);
    RideRequest approve(Long requestId, Long driverId);
    RideRequest reject(Long requestId, Long driverId);
    RideRequest cancel(Long requestId, Long riderId);
}
