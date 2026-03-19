package com.parking.demo.domain.ports.in;

import java.time.LocalDateTime;

public interface ManageParkingUseCase {
    void processEntry(String licensePlate, LocalDateTime entryTime);
    void processParked(String licensePlate, double lat, double lng);
    void processExit(String licensePlate, LocalDateTime exitTime);
}
