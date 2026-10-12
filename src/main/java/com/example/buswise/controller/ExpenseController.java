package com.example.buswise.controller;

import com.example.buswise.dto.ApiResponse;
import com.example.buswise.dto.ExpenseRequestDTO;
import com.example.buswise.dto.ExpenseResponseDTO;
import com.example.buswise.entity.ExpenseCategory;
import com.example.buswise.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ExpenseResponseDTO>>> getExpenses(
            @RequestParam(required = false) Long busId,
            @RequestParam(required = false) ExpenseCategory category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "expenseDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ExpenseResponseDTO> expenses = expenseService.filterExpenses(busId, category, startDate, endDate, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Expenses retrieved successfully", expenses));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ExpenseResponseDTO>>> getAllExpensesList(
            @RequestParam(required = false) Long busId,
            @RequestParam(required = false) ExpenseCategory category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<ExpenseResponseDTO> expenses = expenseService.filterExpensesList(busId, category, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.ok("All expenses retrieved", expenses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponseDTO>> getExpenseById(@PathVariable Long id) {
        ExpenseResponseDTO expense = expenseService.getExpenseById(id);
        return ResponseEntity.ok(ApiResponse.ok("Expense details retrieved", expense));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponseDTO>> recordExpense(@Valid @RequestBody ExpenseRequestDTO dto) {
        ExpenseResponseDTO created = expenseService.recordExpense(dto);
        return new ResponseEntity<>(ApiResponse.ok("Expense recorded successfully", created), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.ok(ApiResponse.ok("Expense deleted successfully", null));
    }
}
