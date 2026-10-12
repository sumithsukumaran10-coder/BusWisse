package com.example.buswise.repository;

import com.example.buswise.entity.Bus;
import com.example.buswise.entity.BusStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {

    Optional<Bus> findByRegistrationNumberIgnoreCase(String registrationNumber);

    boolean existsByRegistrationNumberIgnoreCase(String registrationNumber);

    boolean existsByRegistrationNumberIgnoreCaseAndIdNot(String registrationNumber, Long id);

    long countByStatus(BusStatus status);

    List<Bus> findByStatus(BusStatus status);

    @Query("SELECT b FROM Bus b WHERE " +
           "(:search IS NULL OR LOWER(b.registrationNumber) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(b.busModel) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:status IS NULL OR b.status = :status)")
    Page<Bus> searchBuses(@Param("search") String search,
                          @Param("status") BusStatus status,
                          Pageable pageable);

    @Query("SELECT b FROM Bus b WHERE " +
           "(:search IS NULL OR LOWER(b.registrationNumber) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(b.busModel) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:status IS NULL OR b.status = :status)")
    List<Bus> searchBusesList(@Param("search") String search,
                              @Param("status") BusStatus status);
}
