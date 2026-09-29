package com.Ride_Share_lite.demo.exception;

/** Thrown when a user tries to act on something they do not own. */
public class ForbiddenActionException extends RuntimeException {
    public ForbiddenActionException(String message) { super(message); }
}
