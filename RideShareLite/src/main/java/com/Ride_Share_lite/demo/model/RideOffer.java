package com.Ride_Share_lite.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A route published by a driver. The driver "owns" the offer (ManyToOne),
 * and the offer owns its incoming RideRequests (OneToMany).
 * @Version gives optimistic locking so two concurrent approvals cannot oversell a seat.
 */
@Entity
@Table(name = "ride_offers")
public class RideOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "driver_id")
    private User driver;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    private int totalSeats;
    private int availableSeats;

    @Enumerated(EnumType.STRING)
    private RideStatus status = RideStatus.OPEN;

    @Version
    @JsonIgnore
    private Long version;

    @OneToMany(mappedBy = "rideOffer")
    @JsonIgnore
    private List<RideRequest> requests = new ArrayList<>();

    public RideOffer() {}

    public Long getId() { return id; }
    public User getDriver() { return driver; }
    public void setDriver(User driver) { this.driver = driver; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }
    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }
    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
    public RideStatus getStatus() { return status; }
    public void setStatus(RideStatus status) { this.status = status; }
    public List<RideRequest> getRequests() { return requests; }
}
