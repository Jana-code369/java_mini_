package com.Ride_Share_lite.demo.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

/** Payload a driver sends to publish (or update) a route. */
public record RideOfferRequest(
        @NotNull(message = "driverId is required") Long driverId,
        @NotBlank(message = "Origin is required") String origin,
        @NotBlank(message = "Destination is required") String destination,
        @NotNull(message = "Departure time is required")
        @Future(message = "Departure time must be in the future") LocalDateTime departureTime,
        @Min(value = 1, message = "At least 1 seat must be offered")
        @Max(value = 8, message = "At most 8 seats can be offered") int totalSeats) {}
