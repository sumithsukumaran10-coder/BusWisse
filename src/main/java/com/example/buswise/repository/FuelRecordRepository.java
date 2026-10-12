package com.example.buswise.repository;

import com.example.buswise.entity.FuelRecord;
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
public interface FuelRecordRepository extends JpaRepository<FuelRecord, Long> {

    List<FuelRecord> findByBusId(Long busId);

    List<FuelRecord> findByTripId(Long tripId);

    long countByBusId(Long busId);

    @Query("SELECT f FROM FuelRecord f WHERE " +
           "(:busId IS NULL OR f.bus.id = :busId) AND " +
           "(:startDate IS NULL OR f.recordDate >= :startDate) AND " +
           "(:endDate IS NULL OR f.recordDate <= :endDate) " +
           "ORDER BY f.recordDate DESC")
    Page<FuelRecord> filterFuelRecords(@Param("busId") Long busId,
                                       @Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate,
                                       Pageable pageable);

    @Query("SELECT f FROM FuelRecord f WHERE " +
           "(:busId IS NULL OR f.bus.id = :busId) AND " +
           "(:startDate IS NULL OR f.recordDate >= :startDate) AND " +
           "(:endDate IS NULL OR f.recordDate <= :endDate) " +
           "ORDER BY f.recordDate DESC")
    List<FuelRecord> filterFuelRecordsList(@Param("busId") Long busId,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(f.fuelConsumedLiters), 0.0) FROM FuelRecord f")
    BigDecimal getTotalFuelConsumedLiters();

    @Query("SELECT COALESCE(SUM(f.totalFuelCost), 0.0) FROM FuelRecord f")
    BigDecimal getTotalFuelCost();

    @Query("SELECT COALESCE(AVG(f.fuelEfficiencyKmPerLiter), 0.0) FROM FuelRecord f")
    Double getAverageFleetFuelEfficiency();
}
