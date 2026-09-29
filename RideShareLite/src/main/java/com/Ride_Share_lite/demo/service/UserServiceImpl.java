package com.Ride_Share_lite.demo.service;

import com.Ride_Share_lite.demo.dto.RideHistoryResponse;
import com.Ride_Share_lite.demo.exception.DuplicateResourceException;
import com.Ride_Share_lite.demo.exception.ResourceNotFoundException;
import com.Ride_Share_lite.demo.model.User;
import com.Ride_Share_lite.demo.repository.RideOfferRepository;
import com.Ride_Share_lite.demo.repository.RideRequestRepository;
import com.Ride_Share_lite.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RideOfferRepository offerRepository;
    private final RideRequestRepository requestRepository;

    public UserServiceImpl(UserRepository userRepository, RideOfferRepository offerRepository,
                           RideRequestRepository requestRepository) {
        this.userRepository = userRepository;
        this.offerRepository = offerRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    public User create(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("A user with email " + user.getEmail() + " already exists");
        }
        return userRepository.save(user);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    @Override
    @Transactional
    public User update(Long id, User user) {
        User existing = getById(id);
        if (userRepository.existsByEmailAndIdNot(user.getEmail(), id)) {
            throw new DuplicateResourceException("A user with email " + user.getEmail() + " already exists");
        }
        existing.setName(user.getName());
        existing.setEmail(user.getEmail());
        existing.setPhone(user.getPhone());
        return userRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(getById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public RideHistoryResponse getHistory(Long userId) {
        User user = getById(userId);
        return new RideHistoryResponse(user,
                offerRepository.findByDriverIdOrderByDepartureTimeDesc(userId),
                requestRepository.findByRiderIdOrderByRequestedAtDesc(userId));
    }
}
