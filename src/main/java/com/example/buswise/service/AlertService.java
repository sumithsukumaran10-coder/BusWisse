package com.example.buswise.service;

import com.example.buswise.dto.OperationalAlertDTO;
import com.example.buswise.entity.*;
import com.example.buswise.exception.ResourceNotFoundException;
import com.example.buswise.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AlertService {

    private final OperationalAlertRepository alertRepository;
    private final BusRepository busRepository;
    private final RouteRepository routeRepository;
    private final TripRepository tripRepository;
    private final FuelRecordRepository fuelRecordRepository;

    public AlertService(OperationalAlertRepository alertRepository,
                        BusRepository busRepository,
                        RouteRepository routeRepository,
                        TripRepository tripRepository,
                        FuelRecordRepository fuelRecordRepository) {
        this.alertRepository = alertRepository;
        this.busRepository = busRepository;
        this.routeRepository = routeRepository;
        this.tripRepository = tripRepository;
        this.fuelRecordRepository = fuelRecordRepository;
    }

    @Transactional(readOnly = true)
    public List<OperationalAlertDTO> getActiveAlerts() {
        return alertRepository.findByAcknowledgedFalseOrderByCreatedAtDesc().stream()
                .map(OperationalAlertDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OperationalAlertDTO> getAllAlerts() {
        return alertRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(OperationalAlertDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public OperationalAlertDTO acknowledgeAlert(Long id) {
        OperationalAlert alert = alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found with ID: " + id));
        alert.setAcknowledged(true);
        OperationalAlert saved = alertRepository.save(alert);
        return OperationalAlertDTO.fromEntity(saved);
    }

    /**
     * Rules-Based Engine: Scans database records and fires operational alerts
     * based on fleet thresholds, occupancy analytics, and maintenance metrics.
     */
    public void scanAndGenerateAlerts() {
        // Rule 1: High Odometer / Maintenance Check
        List<Bus> allBuses = busRepository.findAll();
        for (Bus bus : allBuses) {
            if (bus.getStatus() == BusStatus.MAINTENANCE) {
                createAlertIfNotExists(
                        "Bus Under Maintenance: " + bus.getRegistrationNumber(),
                        "Vehicle " + bus.getRegistrationNumber() + " (" + bus.getBusModel() + ") is flagged under MAINTENANCE. Inspect maintenance logs before dispatch.",
                        AlertSeverity.WARNING,
                        AlertType.MAINTENANCE_DUE,
                        "BUS",
                        bus.getId()
                );
            } else if (bus.getOdometerReading().compareTo(BigDecimal.valueOf(80000)) >= 0) {
                createAlertIfNotExists(
                        "High Mileage Inspection Due: " + bus.getRegistrationNumber(),
                        "Vehicle " + bus.getRegistrationNumber() + " has reached " + bus.getOdometerReading() + " km. Recommended scheduled brake and axle servicing.",
                        AlertSeverity.INFO,
                        AlertType.MAINTENANCE_DUE,
                        "BUS",
                        bus.getId()
                );
            }
        }

        // Rule 2: Fuel Efficiency Anomalies (< 3.8 km/L)
        List<FuelRecord> fuelRecords = fuelRecordRepository.findAll();
        for (FuelRecord record : fuelRecords) {
            if (record.getFuelEfficiencyKmPerLiter() != null &&
                record.getFuelEfficiencyKmPerLiter().compareTo(BigDecimal.valueOf(3.8)) < 0 &&
                record.getFuelEfficiencyKmPerLiter().compareTo(BigDecimal.ZERO) > 0) {
                createAlertIfNotExists(
                        "High Fuel Consumption: " + record.getBus().getRegistrationNumber(),
                        "Fuel efficiency dropped to " + record.getFuelEfficiencyKmPerLiter() + " km/L (below 3.8 km/L threshold) on " + record.getRecordDate() + ".",
                        AlertSeverity.WARNING,
                        AlertType.HIGH_FUEL_CONSUMPTION,
                        "BUS",
                        record.getBus().getId()
                );
            }
        }

        // Rule 3: Route Demand & Low Occupancy Rules
        List<Route> routes = routeRepository.findAll();
        for (Route route : routes) {
            List<Trip> routeTrips = tripRepository.findByRouteId(route.getId());
            long completed = routeTrips.stream().filter(t -> t.getStatus() == TripStatus.COMPLETED).count();
            if (completed >= 2) {
                double avgOccupancy = routeTrips.stream()
                        .filter(t -> t.getStatus() == TripStatus.COMPLETED && t.getBus().getSeatingCapacity() > 0)
                        .mapToDouble(t -> ((double) t.getPassengerCount() / t.getBus().getSeatingCapacity()) * 100.0)
                        .average().orElse(0.0);

                if (avgOccupancy >= 90.0) {
                    createAlertIfNotExists(
                            "High Demand Corridor: " + route.getRouteNumber(),
                            "Route " + route.getRouteName() + " is experiencing " +
                            BigDecimal.valueOf(avgOccupancy).setScale(1, RoundingMode.HALF_UP) +
                            "% average passenger occupancy. Consider adding extra frequency.",
                            AlertSeverity.INFO,
                            AlertType.HIGH_DEMAND,
                            "ROUTE",
                            route.getId()
                    );
                } else if (avgOccupancy < 40.0 && avgOccupancy > 0.0) {
                    createAlertIfNotExists(
                            "Low Occupancy Warning: " + route.getRouteNumber(),
                            "Route " + route.getRouteName() + " has only " +
                            BigDecimal.valueOf(avgOccupancy).setScale(1, RoundingMode.HALF_UP) +
                            "% occupancy. Evaluate schedule adjustments or smaller coach deployment.",
                            AlertSeverity.WARNING,
                            AlertType.LOW_OCCUPANCY,
                            "ROUTE",
                            route.getId()
                    );
                }
            }
        }
    }

    private void createAlertIfNotExists(String title, String message, AlertSeverity severity,
                                        AlertType alertType, String entityType, Long entityId) {
        if (!alertRepository.existsByAlertTypeAndRelatedEntityIdAndAcknowledgedFalse(alertType, entityId)) {
            OperationalAlert alert = new OperationalAlert(
                    null, title, message, severity, alertType, entityType, entityId, false
            );
            alertRepository.save(alert);
        }
    }
}
