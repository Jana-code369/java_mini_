package com.Ride_Share_lite.demo.exception;

/** Thrown when a request is well-formed but breaks a business rule (e.g. no seats remaining). */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) { super(message); }
}
