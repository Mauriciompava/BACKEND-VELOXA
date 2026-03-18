package org.cesde.velotax.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipment_timeline", indexes = {
    @Index(name = "idx_shipment_id", columnList = "shipment_id"),
    @Index(name = "idx_timestamp", columnList = "timestamp"),
    @Index(name = "idx_timeline_shipment_timestamp", columnList = "shipment_id,timestamp")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentTimeline {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;
    
    @Column(nullable = false, length = 50)
    private String status;
    
    @Column(nullable = false, length = 100)
    private String location;
    
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime timestamp;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}
