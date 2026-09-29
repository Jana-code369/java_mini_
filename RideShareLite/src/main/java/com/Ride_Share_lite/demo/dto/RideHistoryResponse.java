package com.Ride_Share_lite.demo.dto;

import com.Ride_Share_lite.demo.model.RideOffer;
import com.Ride_Share_lite.demo.model.RideRequest;
import com.Ride_Share_lite.demo.model.User;

import java.util.List;

/** A user's ride history as both driver and rider. */
public record RideHistoryResponse(User user, List<RideOffer> asDriver, List<RideRequest> asRider) {}
