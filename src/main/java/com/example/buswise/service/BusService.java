package com.example.buswise.service;

import com.example.buswise.dto.BusRequestDTO;
import com.example.buswise.dto.BusResponseDTO;
import com.example.buswise.entity.Bus;
import com.example.buswise.entity.BusStatus;
import com.example.buswise.entity.TripStatus;
import com.example.buswise.exception.BusinessValidationException;
import com.example.buswise.exception.DuplicateResourceException;
import com.example.buswise.exception.ResourceNotFoundException;
import com.example.buswise.repository.BusRepository;
import com.example.buswise.repository.ExpenseRepository;
import com.example.buswise.repository.FuelRecordRepository;
import com.example.buswise.repository.TripRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BusService {

    private final BusRepository busRepository;
    private final TripRepository tripRepository;
    private final FuelRecordRepository fuelRecordRepository;
    private final ExpenseRepository expenseRepository;

    public BusService(BusRepository busRepository,
                      TripRepository tripRepository,
                      FuelRecordRepository fuelRecordRepository,
                      ExpenseRepository expenseRepository) {
        this.busRepository = busRepository;
        this.tripRepository = tripRepository;
        this.fuelRecordRepository = fuelRecordRepository;
        this.expenseRepository = expenseRepository;
    }

    public BusResponseDTO createBus(BusRequestDTO dto) {
        String cleanReg = dto.getRegistrationNumber().trim().toUpperCase();
        if (busRepository.existsByRegistrationNumberIgnoreCase(cleanReg)) {
            throw new DuplicateResourceException("Bus with registration number '" + cleanReg + "' already exists.");
        }

        Bus bus = new Bus();
        bus.setRegistrationNumber(cleanReg);
        bus.setBusModel(dto.getBusModel().trim());
        bus.setSeatingCapacity(dto.getSeatingCapacity());
        bus.setFuelType(dto.getFuelType());
        bus.setOdometerReading(dto.getOdometerReading());
        bus.setStatus(dto.getStatus() != null ? dto.getStatus() : BusStatus.AVAILABLE);
        bus.setNotes(dto.getNotes());

        Bus saved = busRepository.save(bus);
        return BusResponseDTO.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public BusResponseDTO getBusById(Long id) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with ID: " + id));
        return BusResponseDTO.fromEntity(bus);
    }

    public BusResponseDTO updateBus(Long id, BusRequestDTO dto) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with ID: " + id));

        String cleanReg = dto.getRegistrationNumber().trim().toUpperCase();
        if (busRepository.existsByRegistrationNumberIgnoreCaseAndIdNot(cleanReg, id)) {
            throw new DuplicateResourceException("Another bus with registration number '" + cleanReg + "' already exists.");
        }

        // Business Rule: Prevent inappropriate status change if bus is assigned to an active trip
        if (dto.getStatus() != null && dto.getStatus() != bus.getStatus()) {
            boolean hasActiveTrip = tripRepository.existsByBusIdAndStatusIn(id, List.of(TripStatus.IN_PROGRESS));
            if (hasActiveTrip && dto.getStatus() != BusStatus.ON_TRIP) {
                throw new BusinessValidationException(
                        "Cannot change status to " + dto.getStatus() + ". Bus #" + bus.getRegistrationNumber() +
                        " is currently assigned to an ongoing IN_PROGRESS trip."
                );
            }
        }

        bus.setRegistrationNumber(cleanReg);
        bus.setBusModel(dto.getBusModel().trim());
        bus.setSeatingCapacity(dto.getSeatingCapacity());
        bus.setFuelType(dto.getFuelType());
        bus.setOdometerReading(dto.getOdometerReading());
        if (dto.getStatus() != null) {
            bus.setStatus(dto.getStatus());
        }
        bus.setNotes(dto.getNotes());

        Bus updated = busRepository.save(bus);
        return BusResponseDTO.fromEntity(updated);
    }

    public void deleteBus(Long id) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with ID: " + id));

        // Business Rule: Safe deletion check
        long tripCount = tripRepository.countByBusId(id);
        if (tripCount > 0) {
            throw new BusinessValidationException(
                    "Cannot delete bus " + bus.getRegistrationNumber() + " because it is linked to " +
                    tripCount + " trip records. Cancel or remove associated trips first to preserve audit history."
            );
        }

        long fuelCount = fuelRecordRepository.countByBusId(id);
        if (fuelCount > 0) {
            throw new BusinessValidationException(
                    "Cannot delete bus " + bus.getRegistrationNumber() + " because it has " +
                    fuelCount + " associated fuel consumption records."
            );
        }

        long expenseCount = expenseRepository.countByBusId(id);
        if (expenseCount > 0) {
            throw new BusinessValidationException(
                    "Cannot delete bus " + bus.getRegistrationNumber() + " because it has " +
                    expenseCount + " operational expense records."
            );
        }

        busRepository.delete(bus);
    }

    @Transactional(readOnly = true)
    public Page<BusResponseDTO> getAllBuses(String search, BusStatus status, Pageable pageable) {
        String query = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        return busRepository.searchBuses(query, status, pageable).map(BusResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public List<BusResponseDTO> getAllBusesList(String search, BusStatus status) {
        String query = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        return busRepository.searchBusesList(query, status).stream()
                .map(BusResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BusResponseDTO> getAvailableBuses() {
        return busRepository.findByStatus(BusStatus.AVAILABLE).stream()
                .map(BusResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
