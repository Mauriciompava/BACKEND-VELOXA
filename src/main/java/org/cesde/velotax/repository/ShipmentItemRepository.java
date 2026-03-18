package org.cesde.velotax.repository;

import org.cesde.velotax.entity.ShipmentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ShipmentItemRepository extends JpaRepository<ShipmentItem, Long> {
    List<ShipmentItem> findByShipmentId(Long shipmentId);
}
