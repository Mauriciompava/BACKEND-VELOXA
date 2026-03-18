package org.cesde.velotax.repository;

import org.cesde.velotax.entity.Shipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    Optional<Shipment> findByTrackingNumber(String trackingNumber);
    Page<Shipment> findByUserId(Long userId, Pageable pageable);
    Page<Shipment> findByUserIdAndStatus(Long userId, String status, Pageable pageable);
    boolean existsByTrackingNumber(String trackingNumber);
}
