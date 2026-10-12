package com.example.buswise.service;

import com.example.buswise.dto.FuelRecordRequestDTO;
import com.example.buswise.dto.FuelRecordResponseDTO;
import com.example.buswise.entity.Bus;
import com.example.buswise.entity.FuelRecord;
import com.example.buswise.entity.Trip;
import com.example.buswise.exception.BusinessValidationException;
import com.example.buswise.exception.ResourceNotFoundException;
import com.example.buswise.repository.BusRepository;
import com.example.buswise.repository.FuelRecordRepository;
import com.example.buswise.repository.TripRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FuelService {

    private final FuelRecordRepository fuelRecordRepository;
    private final BusRepository busRepository;
    private final TripRepository tripRepository;

    public FuelService(FuelRecordRepository fuelRecordRepository,
                       BusRepository busRepository,
                       TripRepository tripRepository) {
        this.fuelRecordRepository = fuelRecordRepository;
        this.busRepository = busRepository;
        this.tripRepository = tripRepository;
    }

    public FuelRecordResponseDTO recordFuel(FuelRecordRequestDTO dto) {
        Bus bus = busRepository.findById(dto.getBusId())
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with ID: " + dto.getBusId()));

        Trip trip = null;
        if (dto.getTripId() != null) {
            trip = tripRepository.findById(dto.getTripId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + dto.getTripId()));
            if (!trip.getBus().getId().equals(bus.getId())) {
                throw new BusinessValidationException(
                        "Trip #" + trip.getId() + " was served by bus " + trip.getBus().getRegistrationNumber() +
                        ", not bus " + bus.getRegistrationNumber() + "."
                );
            }
        }

        if (dto.getFuelConsumedLiters().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("Fuel consumed must be greater than zero liters.");
        }

        FuelRecord record = new FuelRecord();
        record.setBus(bus);
        record.setTrip(trip);
        record.setRecordDate(dto.getRecordDate());
        record.setDistanceTravelledKm(dto.getDistanceTravelledKm());
        record.setFuelConsumedLiters(dto.getFuelConsumedLiters());
        record.setFuelPricePerLiter(dto.getFuelPricePerLiter());
        record.setNotes(dto.getNotes());
        record.calculateDerivedFields();

        FuelRecord saved = fuelRecordRepository.save(record);
        return FuelRecordResponseDTO.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public FuelRecordResponseDTO getFuelRecordById(Long id) {
        FuelRecord record = fuelRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fuel record not found with ID: " + id));
        return FuelRecordResponseDTO.fromEntity(record);
    }

    public void deleteFuelRecord(Long id) {
        FuelRecord record = fuelRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fuel record not found with ID: " + id));
        fuelRecordRepository.delete(record);
    }

    @Transactional(readOnly = true)
    public Page<FuelRecordResponseDTO> filterFuelRecords(Long busId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return fuelRecordRepository.filterFuelRecords(busId, startDate, endDate, pageable)
                .map(FuelRecordResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public List<FuelRecordResponseDTO> filterFuelRecordsList(Long busId, LocalDate startDate, LocalDate endDate) {
        return fuelRecordRepository.filterFuelRecordsList(busId, startDate, endDate).stream()
                .map(FuelRecordResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
