package com.example.buswise.service;

import com.example.buswise.dto.*;
import com.example.buswise.entity.*;
import com.example.buswise.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    private final BusRepository busRepository;
    private final RouteRepository routeRepository;
    private final TripRepository tripRepository;
    private final FuelRecordRepository fuelRecordRepository;
    private final ExpenseRepository expenseRepository;
    private final OperationalAlertRepository alertRepository;

    public AnalyticsService(BusRepository busRepository,
                            RouteRepository routeRepository,
                            TripRepository tripRepository,
                            FuelRecordRepository fuelRecordRepository,
                            ExpenseRepository expenseRepository,
                            OperationalAlertRepository alertRepository) {
        this.busRepository = busRepository;
        this.routeRepository = routeRepository;
        this.tripRepository = tripRepository;
        this.fuelRecordRepository = fuelRecordRepository;
        this.expenseRepository = expenseRepository;
        this.alertRepository = alertRepository;
    }

    public DashboardSummaryDTO getDashboardSummary() {
        DashboardSummaryDTO summary = new DashboardSummaryDTO();

        // 1. Bus Fleet Metrics
        long totalBuses = busRepository.count();
        long availableBuses = busRepository.countByStatus(BusStatus.AVAILABLE);
        long onTripBuses = busRepository.countByStatus(BusStatus.ON_TRIP);
        long maintenanceBuses = busRepository.countByStatus(BusStatus.MAINTENANCE);

        summary.setTotalBuses(totalBuses);
        summary.setAvailableBuses(availableBuses);
        summary.setOnTripBuses(onTripBuses);
        summary.setMaintenanceBuses(maintenanceBuses);

        // 2. Routes & Trips Metrics
        summary.setTotalRoutes(routeRepository.count());
        summary.setTotalTrips(tripRepository.count());
        summary.setScheduledTrips(tripRepository.countByStatus(TripStatus.SCHEDULED));
        summary.setInProgressTrips(tripRepository.countByStatus(TripStatus.IN_PROGRESS));
        summary.setCompletedTrips(tripRepository.countByStatus(TripStatus.COMPLETED));
        summary.setCancelledTrips(tripRepository.countByStatus(TripStatus.CANCELLED));

        // 3. Operational Volume
        Long totalPassengers = tripRepository.getTotalCompletedPassengers();
        summary.setTotalPassengersCarried(totalPassengers != null ? totalPassengers : 0L);

        BigDecimal fuelLiters = fuelRecordRepository.getTotalFuelConsumedLiters();
        summary.setTotalFuelConsumedLiters(fuelLiters != null ? fuelLiters.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

        Double avgFuelEff = fuelRecordRepository.getAverageFleetFuelEfficiency();
        summary.setAverageFuelEfficiency(avgFuelEff != null ? BigDecimal.valueOf(avgFuelEff).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0.0);

        // Occupancy calculation across completed trips
        List<Trip> completedTripsList = tripRepository.findByStatus(TripStatus.COMPLETED);
        if (!completedTripsList.isEmpty()) {
            double avgOcc = completedTripsList.stream()
                    .filter(t -> t.getBus() != null && t.getBus().getSeatingCapacity() > 0)
                    .mapToDouble(t -> ((double) t.getPassengerCount() / t.getBus().getSeatingCapacity()) * 100.0)
                    .average().orElse(0.0);
            summary.setAverageOccupancyRate(BigDecimal.valueOf(avgOcc).setScale(1, RoundingMode.HALF_UP).doubleValue());
        } else {
            summary.setAverageOccupancyRate(0.0);
        }

        // 4. Financial Performance
        BigDecimal totalRevenue = tripRepository.getTotalTicketRevenue();
        BigDecimal totalFuelCost = fuelRecordRepository.getTotalFuelCost();
        BigDecimal nonFuelExpenses = expenseRepository.getTotalNonFuelExpenses();
        BigDecimal tripAdditionalExp = tripRepository.getTotalAdditionalExpenses();

        BigDecimal otherExpenses = nonFuelExpenses.add(tripAdditionalExp);
        BigDecimal totalOperatingCost = totalFuelCost.add(otherExpenses);
        BigDecimal netProfit = totalRevenue.subtract(totalOperatingCost);

        summary.setTotalRevenue(totalRevenue.setScale(2, RoundingMode.HALF_UP));
        summary.setTotalFuelCost(totalFuelCost.setScale(2, RoundingMode.HALF_UP));
        summary.setTotalOtherExpenses(otherExpenses.setScale(2, RoundingMode.HALF_UP));
        summary.setTotalOperatingCost(totalOperatingCost.setScale(2, RoundingMode.HALF_UP));
        summary.setEstimatedOperatingProfit(netProfit.setScale(2, RoundingMode.HALF_UP));

        if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
            double margin = netProfit.divide(totalRevenue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
            summary.setProfitMarginPercentage(BigDecimal.valueOf(margin).setScale(1, RoundingMode.HALF_UP).doubleValue());
        } else {
            summary.setProfitMarginPercentage(0.0);
        }

        // 5. Active Alerts Count
        summary.setActiveAlertsCount(alertRepository.countByAcknowledgedFalse());

        // 6. Chart Datasets
        Map<String, Long> statusMap = new LinkedHashMap<>();
        statusMap.put("Available", availableBuses);
        statusMap.put("On Trip", onTripBuses);
        statusMap.put("Maintenance", maintenanceBuses);
        summary.setBusStatusDistribution(statusMap);

        // Route passenger demand map
        Map<String, Long> demandMap = new LinkedHashMap<>();
        List<Route> routes = routeRepository.findAll();
        for (Route r : routes) {
            long passengers = tripRepository.findByRouteId(r.getId()).stream()
                    .filter(t -> t.getStatus() == TripStatus.COMPLETED)
                    .mapToLong(Trip::getPassengerCount)
                    .sum();
            demandMap.put(r.getRouteNumber(), passengers);
        }
        summary.setRoutePassengerDemand(demandMap);

        // Monthly trends
        DateTimeFormatter monthFmt = DateTimeFormatter.ofPattern("MMM yyyy");
        Map<String, BigDecimal> revMap = new LinkedHashMap<>();
        Map<String, BigDecimal> expMap = new LinkedHashMap<>();
        for (Trip t : completedTripsList) {
            String month = t.getDepartureTime().format(monthFmt);
            revMap.put(month, revMap.getOrDefault(month, BigDecimal.ZERO).add(t.getTicketRevenue()));
        }
        summary.setMonthlyRevenue(revMap);

        for (FuelRecord f : fuelRecordRepository.findAll()) {
            String month = f.getRecordDate().format(DateTimeFormatter.ofPattern("MMM yyyy"));
            expMap.put(month, expMap.getOrDefault(month, BigDecimal.ZERO).add(f.getTotalFuelCost()));
        }
        summary.setMonthlyExpenses(expMap);

        // Recent Trips & Alerts
        summary.setRecentTrips(tripRepository.findTop5ByOrderByDepartureTimeDesc().stream()
                .map(TripResponseDTO::fromEntity).collect(Collectors.toList()));
        summary.setRecentAlerts(alertRepository.findByAcknowledgedFalseOrderByCreatedAtDesc().stream()
                .limit(4).map(OperationalAlertDTO::fromEntity).collect(Collectors.toList()));

        return summary;
    }

    public PassengerAnalyticsDTO getPassengerAnalytics() {
        PassengerAnalyticsDTO dto = new PassengerAnalyticsDTO();

        List<Trip> completed = tripRepository.findByStatus(TripStatus.COMPLETED);
        long totalPassengers = completed.stream().mapToLong(Trip::getPassengerCount).sum();
        dto.setTotalPassengersCarried(totalPassengers);

        double avgOcc = completed.stream()
                .filter(t -> t.getBus() != null && t.getBus().getSeatingCapacity() > 0)
                .mapToDouble(t -> ((double) t.getPassengerCount() / t.getBus().getSeatingCapacity()) * 100.0)
                .average().orElse(0.0);
        dto.setAverageOccupancyRate(BigDecimal.valueOf(avgOcc).setScale(1, RoundingMode.HALF_UP).doubleValue());

        long highTrips = completed.stream()
                .filter(t -> t.getBus() != null && t.getBus().getSeatingCapacity() > 0 &&
                        ((double) t.getPassengerCount() / t.getBus().getSeatingCapacity()) >= 0.90)
                .count();
        dto.setFullyBookedOrOverloadedTrips(highTrips);

        long lowTrips = completed.stream()
                .filter(t -> t.getBus() != null && t.getBus().getSeatingCapacity() > 0 &&
                        ((double) t.getPassengerCount() / t.getBus().getSeatingCapacity()) < 0.40)
                .count();
        dto.setUnderutilizedTrips(lowTrips);

        List<Route> routes = routeRepository.findAll();
        List<PassengerAnalyticsDTO.RouteDemandItem> routeDemandList = new ArrayList<>();
        Map<String, Double> occMap = new LinkedHashMap<>();

        List<String> recommendations = new ArrayList<>();

        for (Route r : routes) {
            List<Trip> routeTrips = tripRepository.findByRouteId(r.getId()).stream()
                    .filter(t -> t.getStatus() == TripStatus.COMPLETED)
                    .collect(Collectors.toList());

            long passengers = routeTrips.stream().mapToLong(Trip::getPassengerCount).sum();
            double routeAvgOcc = routeTrips.stream()
                    .filter(t -> t.getBus() != null && t.getBus().getSeatingCapacity() > 0)
                    .mapToDouble(t -> ((double) t.getPassengerCount() / t.getBus().getSeatingCapacity()) * 100.0)
                    .average().orElse(0.0);

            String category = "OPTIMAL";
            if (routeAvgOcc >= 85.0) {
                category = "HIGH";
                recommendations.add("Route " + r.getRouteNumber() + " (" + r.getRouteName() + ") has high average occupancy of " +
                        BigDecimal.valueOf(routeAvgOcc).setScale(1, RoundingMode.HALF_UP) + "%. Recommendation: Increase departure frequency during peak hours or deploy higher capacity multi-axle coaches.");
            } else if (routeAvgOcc < 40.0 && !routeTrips.isEmpty()) {
                category = "LOW";
                recommendations.add("Route " + r.getRouteNumber() + " (" + r.getRouteName() + ") is underutilized at " +
                        BigDecimal.valueOf(routeAvgOcc).setScale(1, RoundingMode.HALF_UP) + "% occupancy. Recommendation: Consolidate schedules or deploy smaller fuel-efficient midi-buses.");
            }

            routeDemandList.add(new PassengerAnalyticsDTO.RouteDemandItem(
                    r.getId(), r.getRouteNumber(), r.getRouteName(),
                    passengers, BigDecimal.valueOf(routeAvgOcc).setScale(1, RoundingMode.HALF_UP).doubleValue(),
                    category
            ));
            occMap.put(r.getRouteNumber(), BigDecimal.valueOf(routeAvgOcc).setScale(1, RoundingMode.HALF_UP).doubleValue());
        }

        if (recommendations.isEmpty()) {
            recommendations.add("All evaluated routes are currently operating within the optimal occupancy band (40% - 85%). Continue monitoring schedule adherence.");
        }

        dto.setRouteDemandList(routeDemandList);
        dto.setOccupancyByRouteMap(occMap);
        dto.setOperationalRecommendations(recommendations);
        return dto;
    }

    public FuelAnalyticsDTO getFuelAnalytics() {
        FuelAnalyticsDTO dto = new FuelAnalyticsDTO();
        BigDecimal totalLiters = fuelRecordRepository.getTotalFuelConsumedLiters();
        BigDecimal totalCost = fuelRecordRepository.getTotalFuelCost();
        Double fleetAvg = fuelRecordRepository.getAverageFleetFuelEfficiency();

        dto.setTotalFuelConsumedLiters(totalLiters != null ? totalLiters.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        dto.setTotalFuelCost(totalCost != null ? totalCost.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        dto.setFleetAverageEfficiency(fleetAvg != null ? BigDecimal.valueOf(fleetAvg).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0.0);
        dto.setEfficiencyThresholdBaseline(3.80);

        List<Bus> buses = busRepository.findAll();
        List<FuelAnalyticsDTO.BusEfficiencyItem> list = new ArrayList<>();
        long inefficientCount = 0;
        Map<String, BigDecimal> fuelByType = new LinkedHashMap<>();

        for (Bus b : buses) {
            List<FuelRecord> records = fuelRecordRepository.findByBusId(b.getId());
            BigDecimal km = records.stream().map(FuelRecord::getDistanceTravelledKm).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal ltr = records.stream().map(FuelRecord::getFuelConsumedLiters).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal cost = records.stream().map(FuelRecord::getTotalFuelCost).reduce(BigDecimal.ZERO, BigDecimal::add);

            double eff = 0.0;
            if (ltr.compareTo(BigDecimal.ZERO) > 0) {
                eff = km.divide(ltr, 2, RoundingMode.HALF_UP).doubleValue();
            }

            boolean inefficient = (eff > 0 && eff < 3.80);
            if (inefficient) inefficientCount++;

            FuelAnalyticsDTO.BusEfficiencyItem item = new FuelAnalyticsDTO.BusEfficiencyItem();
            item.setBusId(b.getId());
            item.setRegistrationNumber(b.getRegistrationNumber());
            item.setBusModel(b.getBusModel());
            item.setFuelType(b.getFuelType().name());
            item.setTotalDistanceKm(km);
            item.setTotalLitersConsumed(ltr);
            item.setTotalCost(cost);
            item.setAverageEfficiencyKmPerLiter(eff);
            item.setFlaggedInefficient(inefficient);
            list.add(item);

            String ftype = b.getFuelType().name();
            fuelByType.put(ftype, fuelByType.getOrDefault(ftype, BigDecimal.ZERO).add(ltr));
        }

        dto.setInefficientBusesCount(inefficientCount);
        dto.setBusEfficiencyList(list);
        dto.setFuelByFuelTypeMap(fuelByType);
        return dto;
    }

    public ProfitabilityAnalyticsDTO getProfitabilityAnalytics() {
        ProfitabilityAnalyticsDTO dto = new ProfitabilityAnalyticsDTO();

        BigDecimal revenue = tripRepository.getTotalTicketRevenue();
        BigDecimal fuelCost = fuelRecordRepository.getTotalFuelCost();
        BigDecimal nonFuelExpenses = expenseRepository.getTotalNonFuelExpenses();
        BigDecimal tripAdditionalExp = tripRepository.getTotalAdditionalExpenses();
        BigDecimal otherExp = nonFuelExpenses.add(tripAdditionalExp);
        BigDecimal operatingCost = fuelCost.add(otherExp);
        BigDecimal netProfit = revenue.subtract(operatingCost);

        dto.setTotalRevenue(revenue.setScale(2, RoundingMode.HALF_UP));
        dto.setTotalFuelCost(fuelCost.setScale(2, RoundingMode.HALF_UP));
        dto.setTotalOtherExpenses(otherExp.setScale(2, RoundingMode.HALF_UP));
        dto.setTotalOperatingCost(operatingCost.setScale(2, RoundingMode.HALF_UP));
        dto.setNetOperatingProfit(netProfit.setScale(2, RoundingMode.HALF_UP));

        if (revenue.compareTo(BigDecimal.ZERO) > 0) {
            double margin = netProfit.divide(revenue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
            dto.setProfitMarginPercentage(BigDecimal.valueOf(margin).setScale(1, RoundingMode.HALF_UP).doubleValue());
        } else {
            dto.setProfitMarginPercentage(0.0);
        }

        // Expenses breakdown by Category
        Map<String, BigDecimal> catMap = new LinkedHashMap<>();
        catMap.put("FUEL", fuelCost);
        for (Expense e : expenseRepository.findAll()) {
            String cat = e.getCategory().name();
            catMap.put(cat, catMap.getOrDefault(cat, BigDecimal.ZERO).add(e.getAmount()));
        }
        dto.setExpensesByCategoryMap(catMap);

        // Route profitability rollup
        List<Route> routes = routeRepository.findAll();
        List<ProfitabilityAnalyticsDTO.RouteProfitabilityItem> routeProfList = new ArrayList<>();

        for (Route r : routes) {
            List<Trip> trips = tripRepository.findByRouteId(r.getId()).stream()
                    .filter(t -> t.getStatus() == TripStatus.COMPLETED)
                    .collect(Collectors.toList());

            BigDecimal rRev = trips.stream().map(Trip::getTicketRevenue).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal rDirectExp = trips.stream().map(Trip::getAdditionalExpenses).reduce(BigDecimal.ZERO, BigDecimal::add);

            // Associate fuel cost from trips of this route
            BigDecimal rFuel = BigDecimal.ZERO;
            for (Trip t : trips) {
                List<FuelRecord> fRecords = fuelRecordRepository.findByTripId(t.getId());
                for (FuelRecord fr : fRecords) {
                    rFuel = rFuel.add(fr.getTotalFuelCost());
                }
            }

            BigDecimal rCost = rDirectExp.add(rFuel);
            BigDecimal rProfit = rRev.subtract(rCost);

            ProfitabilityAnalyticsDTO.RouteProfitabilityItem item = new ProfitabilityAnalyticsDTO.RouteProfitabilityItem();
            item.setRouteId(r.getId());
            item.setRouteNumber(r.getRouteNumber());
            item.setRouteName(r.getRouteName());
            item.setTotalTrips(trips.size());
            item.setTotalRevenue(rRev.setScale(2, RoundingMode.HALF_UP));
            item.setTotalOperatingCost(rCost.setScale(2, RoundingMode.HALF_UP));
            item.setNetProfit(rProfit.setScale(2, RoundingMode.HALF_UP));

            if (rRev.compareTo(BigDecimal.ZERO) > 0) {
                double rMargin = rProfit.divide(rRev, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
                item.setProfitMarginPercentage(BigDecimal.valueOf(rMargin).setScale(1, RoundingMode.HALF_UP).doubleValue());
            } else {
                item.setProfitMarginPercentage(0.0);
            }
            routeProfList.add(item);
        }
        dto.setRouteProfitabilityList(routeProfList);

        // Top profitable and loss making trips
        List<Trip> completedTrips = tripRepository.findByStatus(TripStatus.COMPLETED);
        List<ProfitabilityAnalyticsDTO.TripFinancialSummary> allTripSummaries = new ArrayList<>();
        for (Trip t : completedTrips) {
            BigDecimal tRev = t.getTicketRevenue();
            BigDecimal tCost = t.getAdditionalExpenses();
            for (FuelRecord fr : fuelRecordRepository.findByTripId(t.getId())) {
                tCost = tCost.add(fr.getTotalFuelCost());
            }
            BigDecimal tProfit = tRev.subtract(tCost);
            allTripSummaries.add(new ProfitabilityAnalyticsDTO.TripFinancialSummary(
                    t.getId(),
                    t.getRoute().getRouteName(),
                    t.getBus().getRegistrationNumber(),
                    tRev, tCost, tProfit
            ));
        }

        allTripSummaries.sort((a, b) -> b.getProfit().compareTo(a.getProfit()));
        dto.setTopProfitableTrips(allTripSummaries.stream().limit(5).collect(Collectors.toList()));
        dto.setLossMakingTrips(allTripSummaries.stream()
                .filter(s -> s.getProfit().compareTo(BigDecimal.ZERO) < 0)
                .collect(Collectors.toList()));

        return dto;
    }

    public SustainabilityAnalyticsDTO getSustainabilityAnalytics() {
        SustainabilityAnalyticsDTO dto = new SustainabilityAnalyticsDTO();

        // Configurable emission factor: Diesel emits approx 2.68 kg CO2 per liter consumed
        final double DIESEL_FACTOR = 2.68;
        dto.setEmissionFactorUsedKgPerLiter(DIESEL_FACTOR);
        dto.setMethodologyNote("Estimations are calculated based on recorded fuel consumption records using standard automotive emissions benchmark (2.68 kg CO2 per liter of diesel). Electric vehicle savings benchmarked against equivalent diesel baseline.");

        BigDecimal totalLiters = fuelRecordRepository.getTotalFuelConsumedLiters();
        double co2Kg = totalLiters.doubleValue() * DIESEL_FACTOR;
        dto.setEstimatedTotalCo2EmissionsKg(BigDecimal.valueOf(co2Kg).setScale(2, RoundingMode.HALF_UP));
        dto.setEstimatedTotalCo2EmissionsTonnes(BigDecimal.valueOf(co2Kg / 1000.0).setScale(2, RoundingMode.HALF_UP));

        // EV offset calculation
        BigDecimal evKm = BigDecimal.ZERO;
        List<Bus> evBuses = busRepository.findAll().stream()
                .filter(b -> b.getFuelType() == FuelType.ELECTRIC).collect(Collectors.toList());
        for (Bus ev : evBuses) {
            for (FuelRecord f : fuelRecordRepository.findByBusId(ev.getId())) {
                evKm = evKm.add(f.getDistanceTravelledKm());
            }
        }
        // Assuming diesel would consume ~ 1L per 4.0 km
        double avoidedDieselLiters = evKm.doubleValue() / 4.0;
        double avoidedCo2 = avoidedDieselLiters * DIESEL_FACTOR;
        dto.setEstimatedCo2AvoidedKgByEv(BigDecimal.valueOf(avoidedCo2).setScale(2, RoundingMode.HALF_UP));

        // Passenger-km emissions
        Long totalPax = tripRepository.getTotalCompletedPassengers();
        if (totalPax != null && totalPax > 0 && co2Kg > 0) {
            double gramsPerPax = (co2Kg * 1000.0) / (totalPax * 100.0); // approx avg trip 100 km
            dto.setAverageCo2GramsPerPassengerKm(BigDecimal.valueOf(gramsPerPax).setScale(1, RoundingMode.HALF_UP).doubleValue());
        } else {
            dto.setAverageCo2GramsPerPassengerKm(0.0);
        }

        // Bus emission items
        List<SustainabilityAnalyticsDTO.BusEmissionItem> busList = new ArrayList<>();
        for (Bus b : busRepository.findAll()) {
            BigDecimal ltr = fuelRecordRepository.findByBusId(b.getId()).stream()
                    .map(FuelRecord::getFuelConsumedLiters).reduce(BigDecimal.ZERO, BigDecimal::add);
            double bCo2 = b.getFuelType() == FuelType.ELECTRIC ? 0.0 : ltr.doubleValue() * DIESEL_FACTOR;

            SustainabilityAnalyticsDTO.BusEmissionItem item = new SustainabilityAnalyticsDTO.BusEmissionItem();
            item.setBusId(b.getId());
            item.setRegistrationNumber(b.getRegistrationNumber());
            item.setBusModel(b.getBusModel());
            item.setFuelType(b.getFuelType().name());
            item.setFuelConsumedLiters(ltr);
            item.setEstimatedCo2Kg(BigDecimal.valueOf(bCo2).setScale(2, RoundingMode.HALF_UP));
            busList.add(item);
        }
        dto.setBusEmissionsList(busList);

        // Sustainability recommendations
        List<String> recs = new ArrayList<>();
        recs.add("Gradually prioritize Electric/CNG buses on shorter intercity corridors under 200 km to cut tailpipe emissions by up to 60%.");
        recs.add("Enforce eco-driving training and reduce depot idling: an improvement from 3.5 to 4.2 km/L yields ~18% reduction in total fleet emissions.");
        recs.add("Target routes with consistently low occupancy for bus downsizing to decrease emissions per passenger-kilometer.");
        dto.setSustainabilityRecommendations(recs);

        return dto;
    }
}
