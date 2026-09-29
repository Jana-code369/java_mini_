package com.Ride_Share_lite.demo.controller;

import com.Ride_Share_lite.demo.dto.RideRequestCreate;
import com.Ride_Share_lite.demo.model.RideRequest;
import com.Ride_Share_lite.demo.service.RideRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@CrossOrigin
public class RideRequestController {

    private final RideRequestService service;

    public RideRequestController(RideRequestService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RideRequest create(@Valid @RequestBody RideRequestCreate request) { return service.create(request); }

    @GetMapping
    public List<RideRequest> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public RideRequest getById(@PathVariable Long id) { return service.getById(id); }

    @GetMapping("/ride/{rideOfferId}")
    public List<RideRequest> getByRide(@PathVariable Long rideOfferId) { return service.getByRide(rideOfferId); }

    @PutMapping("/{id}/approve")
    public RideRequest approve(@PathVariable Long id, @RequestParam Long driverId) {
        return service.approve(id, driverId);
    }

    @PutMapping("/{id}/reject")
    public RideRequest reject(@PathVariable Long id, @RequestParam Long driverId) {
        return service.reject(id, driverId);
    }

    @PutMapping("/{id}/cancel")
    public RideRequest cancel(@PathVariable Long id, @RequestParam Long riderId) {
        return service.cancel(id, riderId);
    }
}
