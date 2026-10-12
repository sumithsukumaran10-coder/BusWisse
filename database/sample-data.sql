-- ==============================================================================
-- BusWise: Sample Seed Data for Indian Bus Transport Network Demonstration
-- ==============================================================================

USE buswise_db;

-- 1. Buses Seed Data
INSERT INTO buses (id, registration_number, bus_model, seating_capacity, fuel_type, odometer_reading, status, notes) VALUES
(1, 'KA-01-F-9001', 'Volvo 9600 Multi-Axle Luxury', 45, 'DIESEL', 48250.00, 'AVAILABLE', 'Airavata Club Class spec, regular AC servicing done.'),
(2, 'KA-51-AA-4200', 'Tata Starbus Ultra EV', 35, 'ELECTRIC', 14200.00, 'AVAILABLE', 'Electric intercity pilot coach, fast-charger compatible.'),
(3, 'MH-12-RN-3310', 'Ashok Leyland 12M Viking', 50, 'CNG', 67800.00, 'ON_TRIP', 'CNG express fleet, scheduled on expressway runs.'),
(4, 'DL-01-AX-7890', 'BharatBenz 1624 Sleeper', 40, 'DIESEL', 92400.00, 'MAINTENANCE', 'Due for scheduled 90,000 km brake overhaul and suspension inspection.'),
(5, 'TN-09-Q-5522', 'Scania Metrolink HD 13.7m', 48, 'DIESEL', 31500.00, 'AVAILABLE', 'Premium sleeper coach with telemetry log.'),
(6, 'AP-28-Z-1104', 'Eicher Skyline Pro 3009', 42, 'DIESEL', 22100.00, 'AVAILABLE', 'Regional feeder coach.')
ON DUPLICATE KEY UPDATE id=id;

-- 2. Routes Seed Data
INSERT INTO routes (id, route_number, route_name, source_location, destination_location, distance_km, estimated_duration_minutes, description) VALUES
(1, 'RT-KA-101', 'Bangalore - Mysuru Expressway', 'Kempegowda BS (Majestic), Bengaluru', 'Mysuru Suburb Bus Stand', 145.00, 150, 'Access-controlled 10-lane expressway service via Bidadi & Mandya.'),
(2, 'RT-MH-202', 'Mumbai - Pune Expressway Superfast', 'Dadar Asiad Stand, Mumbai', 'Swargate Central, Pune', 152.00, 180, 'High-density commuter corridor via Navi Mumbai & Lonavala Ghat.'),
(3, 'RT-DL-303', 'Delhi - Agra Taj Express', 'Sarai Kale Khan ISBT, New Delhi', 'Idgah Bus Stand, Agra', 210.00, 210, 'Yamuna Expressway tourist & commuter corridor.'),
(4, 'RT-TN-404', 'Chennai - Bengaluru Intercity', 'CMBT Koyambedu, Chennai', 'Shantinagar BS, Bengaluru', 330.00, 360, 'NH-48 interstate arterial route via Vellore & Hosur.'),
(5, 'RT-TS-505', 'Hyderabad - Vijayawada Express', 'MGBS, Hyderabad', 'Pandit Nehru BS, Vijayawada', 275.00, 300, 'NH-65 high passenger load corridor.')
ON DUPLICATE KEY UPDATE id=id;

-- 3. Trips Seed Data
INSERT INTO trips (id, bus_id, route_id, departure_time, expected_arrival_time, actual_arrival_time, status, passenger_count, ticket_revenue, additional_expenses, overload_exception, overload_reason, notes) VALUES
(1, 1, 1, '2026-10-10 06:00:00', '2026-10-10 08:30:00', '2026-10-10 08:25:00', 'COMPLETED', 44, 17600.00, 450.00, FALSE, NULL, 'Early morning business express, 97.8% occupancy.'),
(2, 3, 2, '2026-10-10 07:15:00', '2026-10-10 10:15:00', '2026-10-10 10:20:00', 'COMPLETED', 48, 16800.00, 620.00, FALSE, NULL, 'Expressway toll included in additional expenses.'),
(3, 5, 4, '2026-10-10 13:00:00', '2026-10-10 19:00:00', '2026-10-10 19:15:00', 'COMPLETED', 46, 32200.00, 1200.00, FALSE, NULL, 'Interstate toll and permit clearances.'),
(4, 2, 1, '2026-10-10 16:30:00', '2026-10-10 19:00:00', '2026-10-10 18:55:00', 'COMPLETED', 34, 11900.00, 200.00, FALSE, NULL, 'Electric coach return run. Quick recharge at halfway depot.'),
(5, 6, 5, '2026-10-10 21:00:00', '2026-10-11 02:00:00', '2026-10-11 02:10:00', 'COMPLETED', 38, 20900.00, 500.00, FALSE, NULL, 'Night superfast service.'),
(6, 3, 2, '2026-10-11 15:00:00', '2026-10-11 18:00:00', NULL, 'IN_PROGRESS', 49, 17150.00, 620.00, FALSE, NULL, 'Currently en route via Khandala ghat section.'),
(7, 1, 1, '2026-10-11 17:30:00', '2026-10-11 20:00:00', NULL, 'SCHEDULED', 42, 16800.00, 450.00, FALSE, NULL, 'Evening commuter peak service.'),
(8, 2, 1, '2026-10-12 07:00:00', '2026-10-12 09:30:00', NULL, 'SCHEDULED', 28, 9800.00, 200.00, FALSE, NULL, 'Morning EV slot.'),
(9, 5, 4, '2026-10-12 22:00:00', '2026-10-13 04:00:00', NULL, 'SCHEDULED', 40, 28000.00, 1200.00, FALSE, NULL, 'Overnight AC sleeper service.')
ON DUPLICATE KEY UPDATE id=id;

-- 4. Fuel Records Seed Data
INSERT INTO fuel_records (id, bus_id, trip_id, record_date, distance_travelled_km, fuel_consumed_liters, fuel_price_per_liter, total_fuel_cost, fuel_efficiency_km_per_liter, notes) VALUES
(1, 1, 1, '2026-10-10', 145.00, 36.50, 92.50, 3376.25, 3.97, 'Refueled at Bengaluru depot. Good engine condition.'),
(2, 3, 2, '2026-10-10', 152.00, 39.00, 85.00, 3315.00, 3.90, 'CNG refuel at Panvel station.'),
(3, 5, 3, '2026-10-10', 330.00, 84.00, 92.50, 7770.00, 3.93, 'Interstate long-distance diesel fill.'),
(4, 2, 4, '2026-10-10', 145.00, 32.00, 18.00, 576.00, 4.53, 'Electricity grid unit charging equivalent (kWh translated). High efficiency.'),
(5, 6, 5, '2026-10-10', 275.00, 78.50, 92.50, 7261.25, 3.50, 'Higher fuel consumption observed during night high-speed cruise.')
ON DUPLICATE KEY UPDATE id=id;

-- 5. Expenses Seed Data
INSERT INTO expenses (id, bus_id, trip_id, expense_date, category, amount, description) VALUES
(1, 1, 1, '2026-10-10', 'TOLL', 450.00, 'Bangalore-Mysuru Expressway FASTag toll charge.'),
(2, 3, 2, '2026-10-10', 'TOLL', 620.00, 'Mumbai-Pune Expressway toll deduction.'),
(3, 5, 3, '2026-10-10', 'PERMIT', 1200.00, 'Karnataka-Tamil Nadu interstate border transport permit fee.'),
(4, 4, NULL, '2026-10-09', 'MAINTENANCE', 12500.00, 'Air brake booster replacement and front suspension bushing kit.'),
(5, 1, NULL, '2026-10-08', 'DRIVER_SALARY', 3500.00, 'Driver and co-driver daily allowance and duty wage.'),
(6, 6, 5, '2026-10-10', 'TOLL', 500.00, 'NH-65 toll plaza payments.')
ON DUPLICATE KEY UPDATE id=id;

-- 6. Operational Alerts Seed Data
INSERT INTO operational_alerts (id, title, message, severity, alert_type, related_entity_type, related_entity_id, acknowledged, created_at) VALUES
(1, 'Maintenance Overhaul Due', 'Bus KA-01-AX-7890 (BharatBenz 1624) has exceeded 90,000 km baseline threshold. Scheduled brake and transmission inspection required.', 'CRITICAL', 'MAINTENANCE_DUE', 'BUS', 4, FALSE, '2026-10-10 09:30:00'),
(2, 'High Fuel Consumption Anomaly', 'Bus AP-28-Z-1104 recorded fuel efficiency of 3.50 km/L on Route RT-TS-505, which is 18% below the fleet baseline of 4.10 km/L.', 'WARNING', 'HIGH_FUEL_CONSUMPTION', 'BUS', 6, FALSE, '2026-10-11 03:00:00'),
(3, 'High Route Demand Peak', 'Route RT-KA-101 (Bangalore - Mysuru) sustained 95%+ occupancy on multiple consecutive departures. Consider scheduling an extra coach.', 'INFO', 'HIGH_DEMAND', 'ROUTE', 1, FALSE, '2026-10-11 08:00:00'),
(4, 'Trip Capacity Utilization', 'Trip #1 (Bus KA-01-F-9001) achieved 97.8% seat occupancy with zero incident reports.', 'INFO', 'TRIP_OVERLOAD', 'TRIP', 1, TRUE, '2026-10-10 08:35:00')
ON DUPLICATE KEY UPDATE id=id;
