package com.example.buswise.repository;

import com.example.buswise.entity.Route;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    Optional<Route> findByRouteNumberIgnoreCase(String routeNumber);

    boolean existsByRouteNumberIgnoreCase(String routeNumber);

    boolean existsByRouteNumberIgnoreCaseAndIdNot(String routeNumber, Long id);

    @Query("SELECT r FROM Route r WHERE " +
           "(:search IS NULL OR LOWER(r.routeNumber) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(r.routeName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(r.sourceLocation) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(r.destinationLocation) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Route> searchRoutes(@Param("search") String search, Pageable pageable);

    @Query("SELECT r FROM Route r WHERE " +
           "(:search IS NULL OR LOWER(r.routeNumber) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(r.routeName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(r.sourceLocation) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(r.destinationLocation) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Route> searchRoutesList(@Param("search") String search);
}
