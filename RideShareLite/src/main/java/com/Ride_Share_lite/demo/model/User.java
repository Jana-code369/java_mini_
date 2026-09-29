package com.Ride_Share_lite.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/** A person who can act as a driver (owns RideOffers) and/or a rider (owns RideRequests). */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(nullable = false, unique = true)
    private String email;

    @Pattern(regexp = "^[0-9+\\- ]{7,15}$", message = "Phone must be 7-15 digits")
    private String phone;

    @OneToMany(mappedBy = "driver")
    @JsonIgnore
    private List<RideOffer> rideOffers = new ArrayList<>();

    @OneToMany(mappedBy = "rider")
    @JsonIgnore
    private List<RideRequest> rideRequests = new ArrayList<>();

    public User() {}

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public List<RideOffer> getRideOffers() { return rideOffers; }
    public List<RideRequest> getRideRequests() { return rideRequests; }
}
