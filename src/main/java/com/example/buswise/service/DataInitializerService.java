package com.example.buswise.service;

import com.example.buswise.entity.*;
import com.example.buswise.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class DataInitializerService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializerService.class);

    private final BusRepository busRepository;
    private final RouteRepository routeRepository;
    private final TripRepository tripRepository;
    private final FuelRecordRepository fuelRecordRepository;
    private final ExpenseRepository expenseRepository;
    private final OperationalAlertRepository alertRepository;
    private final AlertService alertService;

    public DataInitializerService(BusRepository busRepository,
                                  RouteRepository routeRepository,
                                  TripRepository tripRepository,
                                  FuelRecordRepository fuelRecordRepository,
                                  ExpenseRepository expenseRepository,
                                  OperationalAlertRepository alertRepository,
                                  AlertService alertService) {
        this.busRepository = busRepository;
        this.routeRepository = routeRepository;
        this.tripRepository = tripRepository;
        this.fuelRecordRepository = fuelRecordRepository;
        this.expenseRepository = expenseRepository;
        this.alertRepository = alertRepository;
        this.alertService = alertService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Safe check: Only seed if database has zero buses
        if (busRepository.count() > 0) {
            log.info("Database already seeded with fleet data. Skipping initial seeding.");
            alertService.scanAndGenerateAlerts();
            return;
        }

        log.info("Initializing BusWise sample fleet and operational records...");

        // 1. Seed Buses
        Bus b1 = busRepository.save(new Bus(null, "KA-01-F-9001", "Volvo 9600 Multi-Axle Luxury", 45, FuelType.DIESEL, BigDecimal.valueOf(48250.00), BusStatus.AVAILABLE, "Airavata Club Class spec, regular AC servicing done."));
        Bus b2 = busRepository.save(new Bus(null, "KA-51-AA-4200", "Tata Starbus Ultra EV", 35, FuelType.ELECTRIC, BigDecimal.valueOf(14200.00), BusStatus.AVAILABLE, "Electric intercity pilot coach, fast-charger compatible."));
        Bus b3 = busRepository.save(new Bus(null, "MH-12-RN-3310", "Ashok Leyland 12M Viking", 50, FuelType.CNG, BigDecimal.valueOf(67800.00), BusStatus.ON_TRIP, "CNG express fleet, scheduled on expressway runs."));
        Bus b4 = busRepository.save(new Bus(null, "DL-01-AX-7890", "BharatBenz 1624 Sleeper", 40, FuelType.DIESEL, BigDecimal.valueOf(92400.00), BusStatus.MAINTENANCE, "Due for scheduled 90,000 km brake overhaul and suspension inspection."));
        Bus b5 = busRepository.save(new Bus(null, "TN-09-Q-5522", "Scania Metrolink HD 13.7m", 48, FuelType.DIESEL, BigDecimal.valueOf(31500.00), BusStatus.AVAILABLE, "Premium sleeper coach with telemetry log."));
        Bus b6 = busRepository.save(new Bus(null, "AP-28-Z-1104", "Eicher Skyline Pro 3009", 42, FuelType.DIESEL, BigDecimal.valueOf(22100.00), BusStatus.AVAILABLE, "Regional feeder coach."));

        // 2. Seed Routes
        Route r1 = routeRepository.save(new Route(null, "RT-KA-101", "Bangalore - Mysuru Expressway", "Kempegowda BS (Majestic), Bengaluru", "Mysuru Suburb Bus Stand", BigDecimal.valueOf(145.00), 150, "Access-controlled 10-lane expressway service via Bidadi & Mandya."));
        Route r2 = routeRepository.save(new Route(null, "RT-MH-202", "Mumbai - Pune Expressway Superfast", "Dadar Asiad Stand, Mumbai", "Swargate Central, Pune", BigDecimal.valueOf(152.00), 180, "High-density commuter corridor via Navi Mumbai & Lonavala Ghat."));
        Route r3 = routeRepository.save(new Route(null, "RT-DL-303", "Delhi - Agra Taj Express", "Sarai Kale Khan ISBT, New Delhi", "Idgah Bus Stand, Agra", BigDecimal.valueOf(210.00), 210, "Yamuna Expressway tourist & commuter corridor."));
        Route r4 = routeRepository.save(new Route(null, "RT-TN-404", "Chennai - Bengaluru Intercity", "CMBT Koyambedu, Chennai", "Shantinagar BS, Bengaluru", BigDecimal.valueOf(330.00), 360, "NH-48 interstate arterial route via Vellore & Hosur."));
        Route r5 = routeRepository.save(new Route(null, "RT-TS-505", "Hyderabad - Vijayawada Express", "MGBS, Hyderabad", "Pandit Nehru BS, Vijayawada", BigDecimal.valueOf(275.00), 300, "NH-65 high passenger load corridor."));

        // 3. Seed Trips
        Trip t1 = tripRepository.save(new Trip(null, b1, r1, LocalDateTime.now().minusDays(2).withHour(6).withMinute(0), LocalDateTime.now().minusDays(2).withHour(8).withMinute(30), LocalDateTime.now().minusDays(2).withHour(8).withMinute(25), TripStatus.COMPLETED, 44, BigDecimal.valueOf(17600.00), BigDecimal.valueOf(450.00), false, null, "Early morning business express, 97.8% occupancy."));
        Trip t2 = tripRepository.save(new Trip(null, b3, r2, LocalDateTime.now().minusDays(2).withHour(7).withMinute(15), LocalDateTime.now().minusDays(2).withHour(10).withMinute(15), LocalDateTime.now().minusDays(2).withHour(10).withMinute(20), TripStatus.COMPLETED, 48, BigDecimal.valueOf(16800.00), BigDecimal.valueOf(620.00), false, null, "Expressway toll included in additional expenses."));
        Trip t3 = tripRepository.save(new Trip(null, b5, r4, LocalDateTime.now().minusDays(2).withHour(13).withMinute(0), LocalDateTime.now().minusDays(2).withHour(19).withMinute(0), LocalDateTime.now().minusDays(2).withHour(19).withMinute(15), TripStatus.COMPLETED, 46, BigDecimal.valueOf(32200.00), BigDecimal.valueOf(1200.00), false, null, "Interstate toll and permit clearances."));
        Trip t4 = tripRepository.save(new Trip(null, b2, r1, LocalDateTime.now().minusDays(1).withHour(16).withMinute(30), LocalDateTime.now().minusDays(1).withHour(19).withMinute(0), LocalDateTime.now().minusDays(1).withHour(18).withMinute(55), TripStatus.COMPLETED, 34, BigDecimal.valueOf(11900.00), BigDecimal.valueOf(200.00), false, null, "Electric coach return run. Quick recharge at halfway depot."));
        Trip t5 = tripRepository.save(new Trip(null, b6, r5, LocalDateTime.now().minusDays(1).withHour(21).withMinute(0), LocalDateTime.now().withHour(2).withMinute(0), LocalDateTime.now().withHour(2).withMinute(10), TripStatus.COMPLETED, 38, BigDecimal.valueOf(20900.00), BigDecimal.valueOf(500.00), false, null, "Night superfast service."));
        Trip t6 = tripRepository.save(new Trip(null, b3, r2, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(1), null, TripStatus.IN_PROGRESS, 49, BigDecimal.valueOf(17150.00), BigDecimal.valueOf(620.00), false, null, "Currently en route via Khandala ghat section."));
        Trip t7 = tripRepository.save(new Trip(null, b1, r1, LocalDateTime.now().plusHours(3), LocalDateTime.now().plusHours(5).plusMinutes(30), null, TripStatus.SCHEDULED, 42, BigDecimal.valueOf(16800.00), BigDecimal.valueOf(450.00), false, null, "Evening commuter peak service."));
        Trip t8 = tripRepository.save(new Trip(null, b2, r1, LocalDateTime.now().plusDays(1).withHour(7).withMinute(0), LocalDateTime.now().plusDays(1).withHour(9).withMinute(30), null, TripStatus.SCHEDULED, 28, BigDecimal.valueOf(9800.00), BigDecimal.valueOf(200.00), false, null, "Morning EV slot."));

        // 4. Seed Fuel Records
        fuelRecordRepository.save(new FuelRecord(null, b1, t1, LocalDate.now().minusDays(2), BigDecimal.valueOf(145.00), BigDecimal.valueOf(36.50), BigDecimal.valueOf(92.50), "Refueled at Bengaluru depot. Good engine condition."));
        fuelRecordRepository.save(new FuelRecord(null, b3, t2, LocalDate.now().minusDays(2), BigDecimal.valueOf(152.00), BigDecimal.valueOf(39.00), BigDecimal.valueOf(85.00), "CNG refuel at Panvel station."));
        fuelRecordRepository.save(new FuelRecord(null, b5, t3, LocalDate.now().minusDays(2), BigDecimal.valueOf(330.00), BigDecimal.valueOf(84.00), BigDecimal.valueOf(92.50), "Interstate long-distance diesel fill."));
        fuelRecordRepository.save(new FuelRecord(null, b2, t4, LocalDate.now().minusDays(1), BigDecimal.valueOf(145.00), BigDecimal.valueOf(32.00), BigDecimal.valueOf(18.00), "Electricity charging equivalent units."));
        fuelRecordRepository.save(new FuelRecord(null, b6, t5, LocalDate.now().minusDays(1), BigDecimal.valueOf(275.00), BigDecimal.valueOf(78.50), BigDecimal.valueOf(92.50), "High consumption observed during night high speed cruise."));

        // 5. Seed Expenses
        expenseRepository.save(new Expense(null, b1, t1, LocalDate.now().minusDays(2), ExpenseCategory.TOLL, BigDecimal.valueOf(450.00), "Bangalore-Mysuru Expressway FASTag toll charge."));
        expenseRepository.save(new Expense(null, b3, t2, LocalDate.now().minusDays(2), ExpenseCategory.TOLL, BigDecimal.valueOf(620.00), "Mumbai-Pune Expressway toll deduction."));
        expenseRepository.save(new Expense(null, b5, t3, LocalDate.now().minusDays(2), ExpenseCategory.PERMIT, BigDecimal.valueOf(1200.00), "Karnataka-Tamil Nadu interstate border transport permit fee."));
        expenseRepository.save(new Expense(null, b4, null, LocalDate.now().minusDays(3), ExpenseCategory.MAINTENANCE, BigDecimal.valueOf(12500.00), "Air brake booster replacement and front suspension bushing kit."));
        expenseRepository.save(new Expense(null, b1, null, LocalDate.now().minusDays(4), ExpenseCategory.DRIVER_SALARY, BigDecimal.valueOf(3500.00), "Driver and co-driver daily allowance and duty wage."));

        // 6. Run Alert Engine
        alertService.scanAndGenerateAlerts();

        log.info("BusWise sample database records successfully seeded!");
    }
}
