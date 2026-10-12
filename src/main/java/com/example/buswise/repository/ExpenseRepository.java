package com.example.buswise.repository;

import com.example.buswise.entity.Expense;
import com.example.buswise.entity.ExpenseCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByBusId(Long busId);

    List<Expense> findByTripId(Long tripId);

    List<Expense> findByCategory(ExpenseCategory category);

    long countByBusId(Long busId);

    @Query("SELECT e FROM Expense e WHERE " +
           "(:busId IS NULL OR e.bus.id = :busId) AND " +
           "(:category IS NULL OR e.category = :category) AND " +
           "(:startDate IS NULL OR e.expenseDate >= :startDate) AND " +
           "(:endDate IS NULL OR e.expenseDate <= :endDate) " +
           "ORDER BY e.expenseDate DESC")
    Page<Expense> filterExpenses(@Param("busId") Long busId,
                                 @Param("category") ExpenseCategory category,
                                 @Param("startDate") LocalDate startDate,
                                 @Param("endDate") LocalDate endDate,
                                 Pageable pageable);

    @Query("SELECT e FROM Expense e WHERE " +
           "(:busId IS NULL OR e.bus.id = :busId) AND " +
           "(:category IS NULL OR e.category = :category) AND " +
           "(:startDate IS NULL OR e.expenseDate >= :startDate) AND " +
           "(:endDate IS NULL OR e.expenseDate <= :endDate) " +
           "ORDER BY e.expenseDate DESC")
    List<Expense> filterExpensesList(@Param("busId") Long busId,
                                     @Param("category") ExpenseCategory category,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(e.amount), 0.0) FROM Expense e")
    BigDecimal getTotalExpenses();

    @Query("SELECT COALESCE(SUM(e.amount), 0.0) FROM Expense e WHERE e.category <> 'FUEL'")
    BigDecimal getTotalNonFuelExpenses();

    @Query("SELECT COALESCE(SUM(e.amount), 0.0) FROM Expense e WHERE e.trip.id = :tripId")
    BigDecimal getTotalExpensesForTrip(@Param("tripId") Long tripId);
}
