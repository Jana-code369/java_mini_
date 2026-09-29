package com.Ride_Share_lite.demo.dto;

import jakarta.validation.constraints.NotNull;

/** Payload a rider sends to ask for a seat. */
public record RideRequestCreate(
        @NotNull(message = "riderId is required") Long riderId,
        @NotNull(message = "rideOfferId is required") Long rideOfferId) {}
