package org.cesde.velotax.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.math.BigDecimal;

@Entity
@Table(name = "shipments", indexes = {
    @Index(name = "idx_tracking_number", columnList = "tracking_number"),
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_created_at", columnList = "created_at"),
    @Index(name = "idx_shipment_user_created", columnList = "user_id,created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 20)
    private String trackingNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false, length = 50)
    private String origin;
    
    @Column(nullable = false, length = 50)
    private String destination;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal weight;
    
    @Column(nullable = false, length = 20)
    private String serviceType;
    
    @Column(nullable = false, length = 100)
    private String recipientName;
    
    @Column(nullable = false, length = 20)
    private String recipientPhone;
    
    @Column(nullable = false, length = 100)
    private String recipientEmail;
    
    @Column(nullable = false, length = 255)
    private String recipientAddress;
    
    @Column(columnDefinition = "TEXT")
    private String itemsDescription;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal valueDeclared;
    
    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean insurance = false;
    
    @Column(nullable = false, length = 50, columnDefinition = "VARCHAR(50) DEFAULT 'Pendiente'")
    private String status = "Pendiente";
    
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal estimatedCost;
    
    @Column(name = "estimated_delivery_date")
    private LocalDate estimatedDeliveryDate;
    
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
    
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
