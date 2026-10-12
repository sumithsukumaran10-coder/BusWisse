package com.example.buswise.controller;

import com.example.buswise.dto.BusResponseDTO;
import com.example.buswise.dto.ExpenseResponseDTO;
import com.example.buswise.dto.FuelRecordResponseDTO;
import com.example.buswise.dto.TripResponseDTO;
import com.example.buswise.service.BusService;
import com.example.buswise.service.ExpenseService;
import com.example.buswise.service.FuelService;
import com.example.buswise.service.TripService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final BusService busService;
    private final TripService tripService;
    private final FuelService fuelService;
    private final ExpenseService expenseService;

    public ReportController(BusService busService,
                            TripService tripService,
                            FuelService fuelService,
                            ExpenseService expenseService) {
        this.busService = busService;
        this.tripService = tripService;
        this.fuelService = fuelService;
        this.expenseService = expenseService;
    }

    @GetMapping("/buses.csv")
    public ResponseEntity<byte[]> exportBusesCsv() {
        List<BusResponseDTO> buses = busService.getAllBusesList(null, null);
        StringBuilder sb = new StringBuilder();
        sb.append("Bus ID,Registration Number,Model,Capacity,Fuel Type,Odometer (km),Status,Notes\n");
        for (BusResponseDTO b : buses) {
            sb.append(escapeCsv(String.valueOf(b.getId()))).append(",")
              .append(escapeCsv(b.getRegistrationNumber())).append(",")
              .append(escapeCsv(b.getBusModel())).append(",")
              .append(b.getSeatingCapacity()).append(",")
              .append(b.getFuelType()).append(",")
              .append(b.getOdometerReading()).append(",")
              .append(b.getStatus()).append(",")
              .append(escapeCsv(b.getNotes())).append("\n");
        }

        byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"buswise_fleet_report.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(bytes);
    }

    @GetMapping("/trips.csv")
    public ResponseEntity<byte[]> exportTripsCsv() {
        List<TripResponseDTO> trips = tripService.filterTripsList(null, null, null, null, null);
        StringBuilder sb = new StringBuilder();
        sb.append("Trip ID,Bus Reg,Route,Departure,Expected Arrival,Status,Passengers,Occupancy %,Revenue (INR),Additional Expenses (INR),Estimated Profit (INR)\n");
        for (TripResponseDTO t : trips) {
            sb.append(t.getId()).append(",")
              .append(escapeCsv(t.getBusRegistrationNumber())).append(",")
              .append(escapeCsv(t.getRouteName())).append(",")
              .append(t.getDepartureTime()).append(",")
              .append(t.getExpectedArrivalTime()).append(",")
              .append(t.getStatus()).append(",")
              .append(t.getPassengerCount()).append(",")
              .append(t.getOccupancyRate()).append("% ,")
              .append(t.getTicketRevenue()).append(",")
              .append(t.getAdditionalExpenses()).append(",")
              .append(t.getEstimatedProfit()).append("\n");
        }

        byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"buswise_trips_report.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(bytes);
    }

    @GetMapping("/fuel.csv")
    public ResponseEntity<byte[]> exportFuelCsv() {
        List<FuelRecordResponseDTO> records = fuelService.filterFuelRecordsList(null, null, null);
        StringBuilder sb = new StringBuilder();
        sb.append("Record ID,Bus Reg,Trip ID,Date,Distance (km),Fuel Consumed (L),Price/L (INR),Total Cost (INR),Efficiency (km/L)\n");
        for (FuelRecordResponseDTO f : records) {
            sb.append(f.getId()).append(",")
              .append(escapeCsv(f.getBusRegistrationNumber())).append(",")
              .append(f.getTripId() != null ? f.getTripId() : "").append(",")
              .append(f.getRecordDate()).append(",")
              .append(f.getDistanceTravelledKm()).append(",")
              .append(f.getFuelConsumedLiters()).append(",")
              .append(f.getFuelPricePerLiter()).append(",")
              .append(f.getTotalFuelCost()).append(",")
              .append(f.getFuelEfficiencyKmPerLiter()).append("\n");
        }

        byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"buswise_fuel_report.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(bytes);
    }

    @GetMapping("/expenses.csv")
    public ResponseEntity<byte[]> exportExpensesCsv() {
        List<ExpenseResponseDTO> expenses = expenseService.filterExpensesList(null, null, null, null);
        StringBuilder sb = new StringBuilder();
        sb.append("Expense ID,Date,Category,Amount (INR),Bus Reg,Trip ID,Description\n");
        for (ExpenseResponseDTO e : expenses) {
            sb.append(e.getId()).append(",")
              .append(e.getExpenseDate()).append(",")
              .append(e.getCategory()).append(",")
              .append(e.getAmount()).append(",")
              .append(escapeCsv(e.getBusRegistrationNumber())).append(",")
              .append(e.getTripId() != null ? e.getTripId() : "").append(",")
              .append(escapeCsv(e.getDescription())).append("\n");
        }

        byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"buswise_expenses_report.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(bytes);
    }

    private String escapeCsv(String val) {
        if (val == null) return "";
        if (val.contains(",") || val.contains("\"") || val.contains("\n")) {
            return "\"" + val.replace("\"", "\"\"") + "\"";
        }
        return val;
    }
}
