package com.parking.demo.domain.exception;

public class SpotNotFoundException extends BusinessException {
    public SpotNotFoundException(String message) {
        super(message);
    }
}
