package com.parking.demo.domain.exception;

public class ParkingFullException extends BusinessException {
    public ParkingFullException(String message) {
        super(message);
    }
}
