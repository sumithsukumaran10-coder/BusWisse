package com.example.buswise.repository;

import com.example.buswise.entity.Trip;
import com.example.buswise.entity.TripStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findByBusId(Long busId);

    List<Trip> findByRouteId(Long routeId);

    List<Trip> findByStatus(TripStatus status);

    long countByBusId(Long busId);

    long countByRouteId(Long routeId);

    long countByStatus(TripStatus status);

    boolean existsByBusIdAndStatusIn(Long busId, List<TripStatus> statuses);

    @Query("SELECT t FROM Trip t WHERE t.bus.id = :busId " +
           "AND t.status IN ('SCHEDULED', 'IN_PROGRESS') " +
           "AND (:excludeTripId IS NULL OR t.id <> :excludeTripId) " +
           "AND (t.departureTime < :endTime AND t.expectedArrivalTime > :startTime)")
    List<Trip> findConflictingTrips(@Param("busId") Long busId,
                                    @Param("startTime") LocalDateTime startTime,
                                    @Param("endTime") LocalDateTime endTime,
                                    @Param("excludeTripId") Long excludeTripId);

    @Query("SELECT t FROM Trip t WHERE " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:busId IS NULL OR t.bus.id = :busId) AND " +
           "(:routeId IS NULL OR t.route.id = :routeId) AND " +
           "(:startDate IS NULL OR t.departureTime >= :startDate) AND " +
           "(:endDate IS NULL OR t.departureTime <= :endDate)")
    Page<Trip> filterTrips(@Param("status") TripStatus status,
                           @Param("busId") Long busId,
                           @Param("routeId") Long routeId,
                           @Param("startDate") LocalDateTime startDate,
                           @Param("endDate") LocalDateTime endDate,
                           Pageable pageable);

    @Query("SELECT t FROM Trip t WHERE " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:busId IS NULL OR t.bus.id = :busId) AND " +
           "(:routeId IS NULL OR t.route.id = :routeId) AND " +
           "(:startDate IS NULL OR t.departureTime >= :startDate) AND " +
           "(:endDate IS NULL OR t.departureTime <= :endDate) " +
           "ORDER BY t.departureTime DESC")
    List<Trip> filterTripsList(@Param("status") TripStatus status,
                               @Param("busId") Long busId,
                               @Param("routeId") Long routeId,
                               @Param("startDate") LocalDateTime startDate,
                               @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(t.passengerCount), 0) FROM Trip t WHERE t.status = 'COMPLETED'")
    Long getTotalCompletedPassengers();

    @Query("SELECT COALESCE(SUM(t.ticketRevenue), 0.0) FROM Trip t WHERE t.status = 'COMPLETED'")
    BigDecimal getTotalTicketRevenue();

    @Query("SELECT COALESCE(SUM(t.additionalExpenses), 0.0) FROM Trip t WHERE t.status = 'COMPLETED'")
    BigDecimal getTotalAdditionalExpenses();

    List<Trip> findTop5ByOrderByDepartureTimeDesc();
}
