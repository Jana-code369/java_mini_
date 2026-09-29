package com.Ride_Share_lite.demo.service;

import com.Ride_Share_lite.demo.dto.RideHistoryResponse;
import com.Ride_Share_lite.demo.model.User;

import java.util.List;

public interface UserService {
    User create(User user);
    List<User> getAll();
    User getById(Long id);
    User update(Long id, User user);
    void delete(Long id);
    RideHistoryResponse getHistory(Long userId);
}
