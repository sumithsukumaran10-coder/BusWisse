# BusWise REST API Documentation

The **BusWise** backend exposes a standard RESTful API communicating via JSON payloads. All responses use the unified `ApiResponse<T>` envelope or standard RFC-compliant HTTP error status codes.

---

## 1. Response Format Specification

### Successful Response Envelope (`200 OK` / `201 CREATED`)
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "timestamp": "2026-10-11T20:30:00"
}
```

### Error Response Envelope (`400 BAD REQUEST`, `404 NOT FOUND`, `409 CONFLICT`)
```json
{
  "timestamp": "2026-10-11T20:30:00",
  "status": 400,
  "error": "Input Validation Failed",
  "message": "One or more fields failed validation checks",
  "path": "/api/buses",
  "validationErrors": {
    "registrationNumber": "Registration number is required",
    "seatingCapacity": "Seating capacity must be at least 1"
  }
}
```

---

## 2. Bus Fleet Endpoints (`/api/buses`)

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/buses` | Search & paginate buses (`?page=0&size=10&search=KA&status=AVAILABLE`) |
| `GET` | `/api/buses/all` | List all buses without pagination (for forms/dropdowns) |
| `GET` | `/api/buses/available` | List only buses with `status = AVAILABLE` |
| `GET` | `/api/buses/{id}` | Retrieve details for a single bus |
| `POST` | `/api/buses` | Register a new vehicle in the fleet |
| `PUT` | `/api/buses/{id}` | Update bus details or operational status |
| `DELETE` | `/api/buses/{id}` | Delete bus (safeguarded against dependent trips/fuel logs) |

### Sample Request: `POST /api/buses`
```json
{
  "registrationNumber": "KA-01-F-9001",
  "busModel": "Volvo 9600 Multi-Axle Luxury",
  "seatingCapacity": 45,
  "fuelType": "DIESEL",
  "odometerReading": 48250.00,
  "status": "AVAILABLE",
  "notes": "Airavata Club Class spec with telematics log."
}
```

---

## 3. Route Corridor Endpoints (`/api/routes`)

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/routes` | Search & paginate routes (`?page=0&size=10&search=Bangalore`) |
| `GET` | `/api/routes/all` | List all routes without pagination |
| `GET` | `/api/routes/{id}` | Get specific route information |
| `POST` | `/api/routes` | Define a new transit corridor |
| `PUT` | `/api/routes/{id}` | Update route details |
| `DELETE` | `/api/routes/{id}` | Delete route (blocked if trips are linked) |

### Sample Request: `POST /api/routes`
```json
{
  "routeNumber": "RT-KA-101",
  "routeName": "Bangalore - Mysuru Expressway",
  "sourceLocation": "Kempegowda BS (Majestic), Bengaluru",
  "destinationLocation": "Mysuru Suburb Bus Stand",
  "distanceKm": 145.0,
  "estimatedDurationMinutes": 150,
  "description": "Access-controlled expressway service via Bidadi."
}
```

---

## 4. Trip Dispatch Endpoints (`/api/trips`)

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/trips` | Filter trips (`?status=IN_PROGRESS&busId=1&routeId=1`) |
| `GET` | `/api/trips/all` | List all trips |
| `GET` | `/api/trips/{id}` | Get trip details and occupancy rate |
| `POST` | `/api/trips` | Schedule and assign a bus to a route |
| `PUT` | `/api/trips/{id}/status` | Execute lifecycle transition (`IN_PROGRESS`, `COMPLETED`, `CANCELLED`) |
| `DELETE` | `/api/trips/{id}` | Delete trip |

### Sample Request: `POST /api/trips`
```json
{
  "busId": 1,
  "routeId": 1,
  "departureTime": "2026-10-12 06:00:00",
  "expectedArrivalTime": "2026-10-12 08:30:00",
  "passengerCount": 42,
  "ticketRevenue": 16800.00,
  "additionalExpenses": 450.00,
  "overloadException": false,
  "overloadReason": null,
  "notes": "Morning expressway commuter express."
}
```

### Sample Request: `PUT /api/trips/{id}/status`
```json
{
  "status": "COMPLETED",
  "actualArrivalTime": "2026-10-12 08:25:00",
  "passengerCount": 44,
  "ticketRevenue": 17600.00,
  "additionalExpenses": 450.00
}
```

---

## 5. Analytics & Dashboard Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/dashboard/summary` | Real-time aggregated KPIs, revenue vs expense, fuel trends |
| `GET` | `/api/analytics/passengers`| Corridor occupancy rates, overloaded/underutilized trips |
| `GET` | `/api/analytics/fuel` | Fleet km/L benchmarks, inefficient vehicles, fuel costs |
| `GET` | `/api/analytics/profitability`| Route-level profitability, net operating margins |
| `GET` | `/api/analytics/sustainability`| CO₂ emissions estimates based on 2.68 kg CO₂/L diesel |

---

## 6. Alerts & Reports Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/alerts` | Active unacknowledged operational alerts |
| `GET` | `/api/alerts/all` | History of all alerts |
| `PUT` | `/api/alerts/{id}/acknowledge`| Acknowledge an alert |
| `POST` | `/api/alerts/scan` | Trigger immediate rules scan across the fleet |
| `GET` | `/api/reports/buses.csv` | Download verified fleet CSV |
| `GET` | `/api/reports/trips.csv` | Download dispatch trips CSV |
| `GET` | `/api/reports/fuel.csv` | Download fuel logs CSV |
| `GET` | `/api/reports/expenses.csv`| Download expenses ledger CSV |
