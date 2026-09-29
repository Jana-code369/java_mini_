package com.Ride_Share_lite.demo.service;

import com.Ride_Share_lite.demo.dto.RideRequestCreate;
import com.Ride_Share_lite.demo.exception.BusinessRuleException;
import com.Ride_Share_lite.demo.exception.DuplicateResourceException;
import com.Ride_Share_lite.demo.exception.ForbiddenActionException;
import com.Ride_Share_lite.demo.model.*;
import com.Ride_Share_lite.demo.repository.RideOfferRepository;
import com.Ride_Share_lite.demo.repository.RideRequestRepository;
import com.Ride_Share_lite.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Verifies the business rules from the question paper at the service layer. */
@ExtendWith(MockitoExtension.class)
class RideRequestServiceTest {

    @Mock RideRequestRepository requestRepository;
    @Mock RideOfferRepository offerRepository;
    @Mock UserRepository userRepository;

    RideRequestServiceImpl service;
    User driver, rider;
    RideOffer ride;

    @BeforeEach
    void setUp() {
        service = new RideRequestServiceImpl(requestRepository, offerRepository, userRepository);
        driver = user(1L, "Driver");
        rider = user(2L, "Rider");
        ride = new RideOffer();
        ReflectionTestUtils.setField(ride, "id", 10L);
        ride.setDriver(driver);
        ride.setOrigin("Gandhipuram");
        ride.setDestination("College");
        ride.setDepartureTime(LocalDateTime.now().plusDays(1));
        ride.setTotalSeats(1);
        ride.setAvailableSeats(1);
    }

    private User user(Long id, String name) {
        User u = new User();
        ReflectionTestUtils.setField(u, "id", id);
        u.setName(name);
        return u;
    }

    private void stubLookups(User who) {
        when(userRepository.findById(who.getId())).thenReturn(Optional.of(who));
        when(offerRepository.findById(10L)).thenReturn(Optional.of(ride));
    }

    @Test
    void userCannotRequestSeatOnOwnRide() {
        stubLookups(driver);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> service.create(new RideRequestCreate(1L, 10L)));
        assertTrue(ex.getMessage().contains("own ride"));
        verify(requestRepository, never()).save(any());
    }

    @Test
    void requestRejectedWhenNoSeatsRemain() {
        ride.setAvailableSeats(0);
        ride.setStatus(RideStatus.FULL);
        stubLookups(rider);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> service.create(new RideRequestCreate(2L, 10L)));
        assertTrue(ex.getMessage().contains("No seats remaining"));
    }

    @Test
    void duplicateActiveRequestIsRejected() {
        stubLookups(rider);
        when(requestRepository.existsByRiderIdAndRideOfferIdAndStatusIn(eq(2L), eq(10L), any())).thenReturn(true);
        assertThrows(DuplicateResourceException.class, () -> service.create(new RideRequestCreate(2L, 10L)));
    }

    @Test
    void validRequestIsSavedAsPending() {
        stubLookups(rider);
        when(requestRepository.save(any(RideRequest.class))).thenAnswer(i -> i.getArgument(0));
        RideRequest saved = service.create(new RideRequestCreate(2L, 10L));
        assertEquals(RequestStatus.PENDING, saved.getStatus());
    }

    private RideRequest pendingRequest() {
        RideRequest r = new RideRequest();
        ReflectionTestUtils.setField(r, "id", 100L);
        r.setRider(rider);
        r.setRideOffer(ride);
        return r;
    }

    @Test
    void approveDecrementsSeatsAndMarksRideFull() {
        RideRequest r = pendingRequest();
        when(requestRepository.findById(100L)).thenReturn(Optional.of(r));
        when(requestRepository.save(any(RideRequest.class))).thenAnswer(i -> i.getArgument(0));

        RideRequest result = service.approve(100L, 1L);

        assertEquals(RequestStatus.APPROVED, result.getStatus());
        assertEquals(0, ride.getAvailableSeats());
        assertEquals(RideStatus.FULL, ride.getStatus());
    }

    @Test
    void cannotApproveWhenNoSeatsRemain() {
        ride.setAvailableSeats(0);
        RideRequest r = pendingRequest();
        when(requestRepository.findById(100L)).thenReturn(Optional.of(r));

        assertThrows(BusinessRuleException.class, () -> service.approve(100L, 1L));
        assertEquals(RequestStatus.PENDING, r.getStatus());
        verify(requestRepository, never()).save(any());
    }

    @Test
    void onlyDriverCanApprove() {
        when(requestRepository.findById(100L)).thenReturn(Optional.of(pendingRequest()));
        assertThrows(ForbiddenActionException.class, () -> service.approve(100L, 2L));
    }

    @Test
    void cancellingApprovedRequestReturnsTheSeat() {
        ride.setAvailableSeats(0);
        ride.setStatus(RideStatus.FULL);
        RideRequest r = pendingRequest();
        r.setStatus(RequestStatus.APPROVED);
        when(requestRepository.findById(100L)).thenReturn(Optional.of(r));
        when(requestRepository.save(any(RideRequest.class))).thenAnswer(i -> i.getArgument(0));

        service.cancel(100L, 2L);

        assertEquals(1, ride.getAvailableSeats());
        assertEquals(RideStatus.OPEN, ride.getStatus());
    }
}
