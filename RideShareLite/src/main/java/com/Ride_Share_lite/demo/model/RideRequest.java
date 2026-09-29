package com.Ride_Share_lite.demo.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/** A rider's request for one seat on a RideOffer. */
@Entity
@Table(name = "ride_requests")
public class RideRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "rider_id")
    private User rider;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ride_offer_id")
    private RideOffer rideOffer;

    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING;

    private LocalDateTime requestedAt = LocalDateTime.now();

    public RideRequest() {}

    public Long getId() { return id; }
    public User getRider() { return rider; }
    public void setRider(User rider) { this.rider = rider; }
    public RideOffer getRideOffer() { return rideOffer; }
    public void setRideOffer(RideOffer rideOffer) { this.rideOffer = rideOffer; }
    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
}
