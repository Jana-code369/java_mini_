package com.Ride_Share_lite.demo.controller;

import com.Ride_Share_lite.demo.dto.RideOfferRequest;
import com.Ride_Share_lite.demo.model.RideOffer;
import com.Ride_Share_lite.demo.service.RideOfferService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/rides")
@CrossOrigin
public class RideOfferController {

    private final RideOfferService service;

    public RideOfferController(RideOfferService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RideOffer publish(@Valid @RequestBody RideOfferRequest request) { return service.publish(request); }

    @GetMapping
    public List<RideOffer> getAll() { return service.getAll(); }

    /** GET /api/rides/search?origin=Gandhipuram&destination=Campus&from=2026-09-30T07:00:00&to=2026-09-30T10:00:00 */
    @GetMapping("/search")
    public List<RideOffer> search(
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return service.search(origin, destination, from, to);
    }

    @GetMapping("/{id}")
    public RideOffer getById(@PathVariable Long id) { return service.getById(id); }

    @PutMapping("/{id}")
    public RideOffer update(@PathVariable Long id, @Valid @RequestBody RideOfferRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable Long id, @RequestParam Long driverId) { service.cancel(id, driverId); }
}
