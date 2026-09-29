package com.Ride_Share_lite.demo.service;

import com.Ride_Share_lite.demo.dto.RideOfferRequest;
import com.Ride_Share_lite.demo.model.RideOffer;

import java.time.LocalDateTime;
import java.util.List;

public interface RideOfferService {
    RideOffer publish(RideOfferRequest request);
    List<RideOffer> getAll();
    RideOffer getById(Long id);
    List<RideOffer> search(String origin, String destination, LocalDateTime from, LocalDateTime to);
    RideOffer update(Long id, RideOfferRequest request);
    void cancel(Long id, Long driverId);
}
