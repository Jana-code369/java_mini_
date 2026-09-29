package com.Ride_Share_lite.demo.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(LocalDateTime timestamp, int status, String error, String message,
                            String path, List<String> details) {}
