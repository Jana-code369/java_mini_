package com.Ride_Share_lite.demo.service;

import com.Ride_Share_lite.demo.dto.RideRequestCreate;
import com.Ride_Share_lite.demo.exception.BusinessRuleException;
import com.Ride_Share_lite.demo.exception.DuplicateResourceException;
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
import java.util.Set;

/**
 * Business rules live HERE (not only in the database):
 *  1. A user cannot request a seat on their own ride.
 *  2. A request is rejected immediately when the ride has no seats left.
 *  3. A request cannot be approved when no seats remain.
 *  4. Only the ride's driver can approve/reject; only the rider can cancel.
 */
@Service
public class RideRequestServiceImpl implements RideRequestService {

    private static final Set<RequestStatus> ACTIVE = Set.of(RequestStatus.PENDING, RequestStatus.APPROVED);

    private final RideRequestRepository requestRepository;
    private final RideOfferRepository offerRepository;
    private final UserRepository userRepository;

    public RideRequestServiceImpl(RideRequestRepository requestRepository, RideOfferRepository offerRepository,
                                  UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.offerRepository = offerRepository;
        this.userRepository = userRepository;
    }

    /** Feature 3: rider requests a seat. */
    @Override
    @Transactional
    public RideRequest create(RideRequestCreate dto) {
        User rider = userRepository.findById(dto.riderId())
                .orElseThrow(() -> new ResourceNotFoundException("User", dto.riderId()));
        RideOffer ride = offerRepository.findById(dto.rideOfferId())
                .orElseThrow(() -> new ResourceNotFoundException("RideOffer", dto.rideOfferId()));

        if (ride.getDriver().getId().equals(rider.getId())) {
            throw new BusinessRuleException("You cannot request a seat on your own ride");
        }
        if (ride.getStatus() == RideStatus.CANCELLED) {
            throw new BusinessRuleException("This ride has been cancelled");
        }
        if (ride.getDepartureTime().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("This ride has already departed");
        }
        if (ride.getAvailableSeats() <= 0 || ride.getStatus() == RideStatus.FULL) {
            throw new BusinessRuleException("No seats remaining on this ride");
        }
        if (requestRepository.existsByRiderIdAndRideOfferIdAndStatusIn(rider.getId(), ride.getId(), ACTIVE)) {
            throw new DuplicateResourceException("You already have an active request for this ride");
        }

        RideRequest request = new RideRequest();
        request.setRider(rider);
        request.setRideOffer(ride);
        request.setStatus(RequestStatus.PENDING);
        request.setRequestedAt(LocalDateTime.now());
        return requestRepository.save(request);
    }

    @Override
    public List<RideRequest> getAll() {
        return requestRepository.findAll();
    }

    @Override
    public RideRequest getById(Long id) {
        return requestRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("RideRequest", id));
    }

    @Override
    public List<RideRequest> getByRide(Long rideOfferId) {
        return requestRepository.findByRideOfferId(rideOfferId);
    }

    /** Feature 4: driver approves. Seat count is checked BEFORE saving, then decremented. */
    @Override
    @Transactional
    public RideRequest approve(Long requestId, Long driverId) {
        RideRequest request = getById(requestId);
        RideOffer ride = request.getRideOffer();
        requireDriver(ride, driverId);
        requirePending(request);

        if (ride.getStatus() == RideStatus.CANCELLED) {
            throw new BusinessRuleException("This ride has been cancelled");
        }
        if (ride.getAvailableSeats() <= 0) {
            throw new BusinessRuleException("Cannot approve: no seats remaining on this ride");
        }

        ride.setAvailableSeats(ride.getAvailableSeats() - 1);
        if (ride.getAvailableSeats() == 0) {
            ride.setStatus(RideStatus.FULL);
        }
        request.setStatus(RequestStatus.APPROVED);
        offerRepository.save(ride);
        return requestRepository.save(request);
    }

    @Override
    @Transactional
    public RideRequest reject(Long requestId, Long driverId) {
        RideRequest request = getById(requestId);
        requireDriver(request.getRideOffer(), driverId);
        requirePending(request);
        request.setStatus(RequestStatus.REJECTED);
        return requestRepository.save(request);
    }

    /** Rider withdraws; an approved seat is handed back to the ride. */
    @Override
    @Transactional
    public RideRequest cancel(Long requestId, Long riderId) {
        RideRequest request = getById(requestId);
        if (!request.getRider().getId().equals(riderId)) {
            throw new ForbiddenActionException("Only the rider who made this request can cancel it");
        }
        if (!ACTIVE.contains(request.getStatus())) {
            throw new BusinessRuleException("Only pending or approved requests can be cancelled");
        }
        if (request.getStatus() == RequestStatus.APPROVED) {
            RideOffer ride = request.getRideOffer();
            ride.setAvailableSeats(ride.getAvailableSeats() + 1);
            if (ride.getStatus() == RideStatus.FULL) {
                ride.setStatus(RideStatus.OPEN);
            }
            offerRepository.save(ride);
        }
        request.setStatus(RequestStatus.CANCELLED);
        return requestRepository.save(request);
    }

    private void requireDriver(RideOffer ride, Long driverId) {
        if (!ride.getDriver().getId().equals(driverId)) {
            throw new ForbiddenActionException("Only the driver of this ride can approve or reject requests");
        }
    }

    private void requirePending(RideRequest request) {
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new BusinessRuleException("Request is already " + request.getStatus() + " and cannot be changed");
        }
    }
}
