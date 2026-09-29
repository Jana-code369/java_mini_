package com.Ride_Share_lite.demo.service;

import com.Ride_Share_lite.demo.dto.RideOfferRequest;
import com.Ride_Share_lite.demo.exception.BusinessRuleException;
import com.Ride_Share_lite.demo.exception.ForbiddenActionException;
import com.Ride_Share_lite.demo.exception.ResourceNotFoundException;
import com.Ride_Share_lite.demo.model.*;
import com.Ride_Share_lite.demo.repository.RideOfferRepository;
import com.Ride_Share_lite.demo.repository.RideRequestRepository;
import com.Ride_Share_lite.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RideOfferServiceImpl implements RideOfferService {

    private final RideOfferRepository offerRepository;
    private final RideRequestRepository requestRepository;
    private final UserRepository userRepository;

    public RideOfferServiceImpl(RideOfferRepository offerRepository, RideRequestRepository requestRepository,
                                UserRepository userRepository) {
        this.offerRepository = offerRepository;
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
    }

    /** Feature 1: driver publishes a route with origin, destination, departure time and seats. */
    @Override
    public RideOffer publish(RideOfferRequest request) {
        User driver = userRepository.findById(request.driverId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.driverId()));
        if (request.origin().trim().equalsIgnoreCase(request.destination().trim())) {
            throw new BusinessRuleException("Origin and destination cannot be the same");
        }
        RideOffer offer = new RideOffer();
        offer.setDriver(driver);
        offer.setOrigin(request.origin().trim());
        offer.setDestination(request.destination().trim());
        offer.setDepartureTime(request.departureTime());
        offer.setTotalSeats(request.totalSeats());
        offer.setAvailableSeats(request.totalSeats());
        offer.setStatus(RideStatus.OPEN);
        return offerRepository.save(offer);
    }

    @Override
    public List<RideOffer> getAll() {
        return offerRepository.findAll();
    }

    @Override
    public RideOffer getById(Long id) {
        return offerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("RideOffer", id));
    }

    /** Feature 2: rider searches by route and time window; only future rides with free seats are returned. */
    @Override
    public List<RideOffer> search(String origin, String destination, LocalDateTime from, LocalDateTime to) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = (from == null || from.isBefore(now)) ? now : from;
        LocalDateTime end = (to == null) ? now.plusYears(1) : to;
        if (end.isBefore(start)) {
            throw new BusinessRuleException("The end of the time window must be after its start");
        }
        return offerRepository.search(RideStatus.OPEN,
                origin == null ? "" : origin.trim(),
                destination == null ? "" : destination.trim(), start, end);
    }

    @Override
    @Transactional
    public RideOffer update(Long id, RideOfferRequest request) {
        RideOffer offer = getById(id);
        requireOwner(offer, request.driverId());
        if (offer.getStatus() == RideStatus.CANCELLED) {
            throw new BusinessRuleException("A cancelled ride cannot be edited");
        }
        int booked = offer.getTotalSeats() - offer.getAvailableSeats();
        if (request.totalSeats() < booked) {
            throw new BusinessRuleException("Cannot reduce seats below the " + booked + " already approved");
        }
        offer.setOrigin(request.origin().trim());
        offer.setDestination(request.destination().trim());
        offer.setDepartureTime(request.departureTime());
        offer.setTotalSeats(request.totalSeats());
        offer.setAvailableSeats(request.totalSeats() - booked);
        offer.setStatus(offer.getAvailableSeats() == 0 ? RideStatus.FULL : RideStatus.OPEN);
        return offerRepository.save(offer);
    }

    /** Soft-delete: keeps history, and closes every open request on the ride. */
    @Override
    @Transactional
    public void cancel(Long id, Long driverId) {
        RideOffer offer = getById(id);
        requireOwner(offer, driverId);
        if (offer.getStatus() == RideStatus.CANCELLED) {
            throw new BusinessRuleException("Ride is already cancelled");
        }
        offer.setStatus(RideStatus.CANCELLED);
        for (RideRequest r : requestRepository.findByRideOfferId(id)) {
            if (r.getStatus() == RequestStatus.PENDING || r.getStatus() == RequestStatus.APPROVED) {
                r.setStatus(RequestStatus.CANCELLED);
                requestRepository.save(r);
            }
        }
        offerRepository.save(offer);
    }

    private void requireOwner(RideOffer offer, Long driverId) {
        if (!offer.getDriver().getId().equals(driverId)) {
            throw new ForbiddenActionException("Only the driver who published this ride can change it");
        }
    }
}
