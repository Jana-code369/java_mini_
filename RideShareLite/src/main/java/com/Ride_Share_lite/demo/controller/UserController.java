package com.Ride_Share_lite.demo.controller;

import com.Ride_Share_lite.demo.dto.RideHistoryResponse;
import com.Ride_Share_lite.demo.model.User;
import com.Ride_Share_lite.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    private final UserService service;

    public UserController(UserService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) { return service.create(user); }

    @GetMapping
    public List<User> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) { return service.getById(id); }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @Valid @RequestBody User user) { return service.update(id, user); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }

    /** Feature 5: a user's ride history as both driver and rider. */
    @GetMapping("/{id}/history")
    public RideHistoryResponse history(@PathVariable Long id) { return service.getHistory(id); }
}
