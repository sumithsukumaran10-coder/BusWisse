package com.example.buswise.repository;

import com.example.buswise.entity.AlertType;
import com.example.buswise.entity.OperationalAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperationalAlertRepository extends JpaRepository<OperationalAlert, Long> {

    List<OperationalAlert> findByAcknowledgedFalseOrderByCreatedAtDesc();

    List<OperationalAlert> findAllByOrderByCreatedAtDesc();

    long countByAcknowledgedFalse();

    boolean existsByAlertTypeAndRelatedEntityIdAndAcknowledgedFalse(AlertType alertType, Long relatedEntityId);
}
