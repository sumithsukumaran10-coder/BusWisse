package com.example.buswise.service;

import com.example.buswise.dto.ExpenseRequestDTO;
import com.example.buswise.dto.ExpenseResponseDTO;
import com.example.buswise.entity.Bus;
import com.example.buswise.entity.Expense;
import com.example.buswise.entity.ExpenseCategory;
import com.example.buswise.entity.Trip;
import com.example.buswise.exception.BusinessValidationException;
import com.example.buswise.exception.ResourceNotFoundException;
import com.example.buswise.repository.BusRepository;
import com.example.buswise.repository.ExpenseRepository;
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
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final BusRepository busRepository;
    private final TripRepository tripRepository;

    public ExpenseService(ExpenseRepository expenseRepository,
                          BusRepository busRepository,
                          TripRepository tripRepository) {
        this.expenseRepository = expenseRepository;
        this.busRepository = busRepository;
        this.tripRepository = tripRepository;
    }

    public ExpenseResponseDTO recordExpense(ExpenseRequestDTO dto) {
        Bus bus = null;
        if (dto.getBusId() != null) {
            bus = busRepository.findById(dto.getBusId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bus not found with ID: " + dto.getBusId()));
        }

        Trip trip = null;
        if (dto.getTripId() != null) {
            trip = tripRepository.findById(dto.getTripId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + dto.getTripId()));
            if (bus == null && trip.getBus() != null) {
                bus = trip.getBus();
            }
        }

        if (dto.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessValidationException("Expense amount cannot be negative.");
        }

        Expense expense = new Expense();
        expense.setBus(bus);
        expense.setTrip(trip);
        expense.setExpenseDate(dto.getExpenseDate());
        expense.setCategory(dto.getCategory());
        expense.setAmount(dto.getAmount());
        expense.setDescription(dto.getDescription().trim());

        Expense saved = expenseRepository.save(expense);
        return ExpenseResponseDTO.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public ExpenseResponseDTO getExpenseById(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with ID: " + id));
        return ExpenseResponseDTO.fromEntity(expense);
    }

    public void deleteExpense(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with ID: " + id));
        expenseRepository.delete(expense);
    }

    @Transactional(readOnly = true)
    public Page<ExpenseResponseDTO> filterExpenses(Long busId, ExpenseCategory category,
                                                   LocalDate startDate, LocalDate endDate,
                                                   Pageable pageable) {
        return expenseRepository.filterExpenses(busId, category, startDate, endDate, pageable)
                .map(ExpenseResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponseDTO> filterExpensesList(Long busId, ExpenseCategory category,
                                                       LocalDate startDate, LocalDate endDate) {
        return expenseRepository.filterExpensesList(busId, category, startDate, endDate).stream()
                .map(ExpenseResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
