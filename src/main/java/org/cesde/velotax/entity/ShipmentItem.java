package org.cesde.velotax.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "shipment_items", indexes = {
    @Index(name = "idx_shipment_id", columnList = "shipment_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;
    
    @Column(nullable = false, length = 150)
    private String description;
    
    @Column(nullable = false, columnDefinition = "INT DEFAULT 1")
    private Integer quantity = 1;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal weightPerUnit;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal valuePerUnit;
}
