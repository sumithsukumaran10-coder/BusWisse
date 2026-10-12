-- ==============================================================================
-- BusWise: RE:ENGINEER Intelligent Bus Transport Management System
-- Relational Database Schema (MySQL 8.x DDL)
-- ==============================================================================

CREATE DATABASE IF NOT EXISTS buswise_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE buswise_db;

-- ------------------------------------------------------------------------------
-- 1. Table: buses
-- Stores physical fleet details, registration, capacity, fuel type, odometer
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS buses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    registration_number VARCHAR(30) NOT NULL UNIQUE,
    bus_model VARCHAR(100) NOT NULL,
    seating_capacity INT NOT NULL,
    fuel_type VARCHAR(20) NOT NULL, -- DIESEL, CNG, ELECTRIC, PETROL
    odometer_reading DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE', -- AVAILABLE, ON_TRIP, MAINTENANCE
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_bus_capacity CHECK (seating_capacity > 0),
    CONSTRAINT chk_bus_odometer CHECK (odometer_reading >= 0),
    CONSTRAINT chk_bus_fuel_type CHECK (fuel_type IN ('DIESEL', 'CNG', 'ELECTRIC', 'PETROL')),
    CONSTRAINT chk_bus_status CHECK (status IN ('AVAILABLE', 'ON_TRIP', 'MAINTENANCE'))
) ENGINE=InnoDB;

CREATE INDEX idx_bus_registration ON buses (registration_number);
CREATE INDEX idx_bus_status ON buses (status);

-- ------------------------------------------------------------------------------
-- 2. Table: routes
-- Stores transit corridors, start/end locations, distance and expected duration
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS routes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    route_number VARCHAR(50) NOT NULL UNIQUE,
    route_name VARCHAR(150) NOT NULL,
    source_location VARCHAR(120) NOT NULL,
    destination_location VARCHAR(120) NOT NULL,
    distance_km DECIMAL(8, 2) NOT NULL,
    estimated_duration_minutes INT NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_route_distance CHECK (distance_km > 0),
    CONSTRAINT chk_route_duration CHECK (estimated_duration_minutes > 0)
) ENGINE=InnoDB;

CREATE INDEX idx_route_number ON routes (route_number);
CREATE INDEX idx_route_locations ON routes (source_location, destination_location);

-- ------------------------------------------------------------------------------
-- 3. Table: trips
-- Schedules and records actual bus runs on routes, revenue, passengers & state
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS trips (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bus_id BIGINT NOT NULL,
    route_id BIGINT NOT NULL,
    departure_time DATETIME NOT NULL,
    expected_arrival_time DATETIME NOT NULL,
    actual_arrival_time DATETIME NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED', -- SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    passenger_count INT NOT NULL DEFAULT 0,
    ticket_revenue DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    additional_expenses DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    overload_exception BOOLEAN NOT NULL DEFAULT FALSE,
    overload_reason VARCHAR(255) NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_trips_bus FOREIGN KEY (bus_id) REFERENCES buses (id) ON DELETE RESTRICT,
    CONSTRAINT fk_trips_route FOREIGN KEY (route_id) REFERENCES routes (id) ON DELETE RESTRICT,
    CONSTRAINT chk_trip_passengers CHECK (passenger_count >= 0),
    CONSTRAINT chk_trip_revenue CHECK (ticket_revenue >= 0.00),
    CONSTRAINT chk_trip_expenses CHECK (additional_expenses >= 0.00),
    CONSTRAINT chk_trip_status CHECK (status IN ('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'))
) ENGINE=InnoDB;

CREATE INDEX idx_trip_bus_time ON trips (bus_id, departure_time, expected_arrival_time);
CREATE INDEX idx_trip_route ON trips (route_id);
CREATE INDEX idx_trip_status ON trips (status);
CREATE INDEX idx_trip_departure ON trips (departure_time);

-- ------------------------------------------------------------------------------
-- 4. Table: fuel_records
-- Tracks fuel refueling, distance, efficiency (km/L) and costs
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS fuel_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bus_id BIGINT NOT NULL,
    trip_id BIGINT NULL,
    record_date DATE NOT NULL,
    distance_travelled_km DECIMAL(8, 2) NOT NULL,
    fuel_consumed_liters DECIMAL(8, 2) NOT NULL,
    fuel_price_per_liter DECIMAL(8, 2) NOT NULL,
    total_fuel_cost DECIMAL(10, 2) NOT NULL,
    fuel_efficiency_km_per_liter DECIMAL(8, 2) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_fuel_bus FOREIGN KEY (bus_id) REFERENCES buses (id) ON DELETE CASCADE,
    CONSTRAINT fk_fuel_trip FOREIGN KEY (trip_id) REFERENCES trips (id) ON DELETE SET NULL,
    CONSTRAINT chk_fuel_distance CHECK (distance_travelled_km >= 0),
    CONSTRAINT chk_fuel_consumed CHECK (fuel_consumed_liters > 0),
    CONSTRAINT chk_fuel_price CHECK (fuel_price_per_liter >= 0)
) ENGINE=InnoDB;

CREATE INDEX idx_fuel_bus ON fuel_records (bus_id);
CREATE INDEX idx_fuel_date ON fuel_records (record_date);

-- ------------------------------------------------------------------------------
-- 5. Table: expenses
-- Categorized operational expenditures associated with buses or trips
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS expenses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bus_id BIGINT NULL,
    trip_id BIGINT NULL,
    expense_date DATE NOT NULL,
    category VARCHAR(30) NOT NULL, -- FUEL, MAINTENANCE, DRIVER_SALARY, TOLL, PERMIT, MISC
    amount DECIMAL(10, 2) NOT NULL,
    description VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_expense_bus FOREIGN KEY (bus_id) REFERENCES buses (id) ON DELETE CASCADE,
    CONSTRAINT fk_expense_trip FOREIGN KEY (trip_id) REFERENCES trips (id) ON DELETE SET NULL,
    CONSTRAINT chk_expense_amount CHECK (amount >= 0),
    CONSTRAINT chk_expense_category CHECK (category IN ('FUEL', 'MAINTENANCE', 'DRIVER_SALARY', 'TOLL', 'PERMIT', 'MISC'))
) ENGINE=InnoDB;

CREATE INDEX idx_expense_date ON expenses (expense_date);
CREATE INDEX idx_expense_category ON expenses (category);

-- ------------------------------------------------------------------------------
-- 6. Table: operational_alerts
-- Stores rules-based proactive notifications for fleet managers
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS operational_alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    severity VARCHAR(20) NOT NULL, -- INFO, WARNING, CRITICAL
    alert_type VARCHAR(50) NOT NULL, -- MAINTENANCE_DUE, HIGH_FUEL_CONSUMPTION, LOW_OCCUPANCY, HIGH_DEMAND, TRIP_OVERLOAD, LOW_PROFITABILITY
    related_entity_type VARCHAR(30) NULL, -- BUS, ROUTE, TRIP
    related_entity_id BIGINT NULL,
    acknowledged BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_alert_severity CHECK (severity IN ('INFO', 'WARNING', 'CRITICAL'))
) ENGINE=InnoDB;

CREATE INDEX idx_alert_severity ON operational_alerts (severity);
CREATE INDEX idx_alert_acknowledged ON operational_alerts (acknowledged);
