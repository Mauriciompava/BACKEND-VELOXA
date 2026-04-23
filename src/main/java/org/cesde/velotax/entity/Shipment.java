package org.cesde.velotax.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;

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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private Recipient recipient;
    
    @Column(nullable = false, length = 50)
    private String origin;
    
    @Column(nullable = false, length = 50)
    private String destination;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalWeight;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('standard', 'express', 'overnight')")
    private ServiceType serviceType;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal totalValueDeclared;
    
    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean insurance = false;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('Pendiente', 'Recogido', 'En tránsito', 'En reparto', 'Entregado', 'Cancelado') DEFAULT 'Pendiente'")
    private ShipmentStatus status = ShipmentStatus.PENDIENTE;
    
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal estimatedCost;
    
    @Column(name = "estimated_delivery_date")
    private LocalDate estimatedDeliveryDate;
    
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
    
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShipmentItem> items;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum ServiceType {
        STANDARD, EXPRESS, OVERNIGHT
    }
    
    public enum ShipmentStatus {
        PENDIENTE, RECOGIDO, EN_TRANSITO, EN_REPARTO, ENTREGADO, CANCELADO
    }
}
