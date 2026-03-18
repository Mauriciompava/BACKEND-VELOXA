package org.cesde.velotax.repository;

import org.cesde.velotax.entity.ShipmentTimeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ShipmentTimelineRepository extends JpaRepository<ShipmentTimeline, Long> {
    List<ShipmentTimeline> findByShipmentIdOrderByTimestampDesc(Long shipmentId);
}
