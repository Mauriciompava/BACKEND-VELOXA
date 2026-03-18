package org.cesde.velotax.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "quotes", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quote {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(nullable = false, length = 50)
    private String origin;
    
    @Column(nullable = false, length = 50)
    private String destination;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal weight;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('standard', 'express', 'overnight')")
    private QuoteServiceType serviceType;
    
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal baseCost;
    
    @Column(precision = 5, scale = 2, columnDefinition = "DECIMAL(5,2) DEFAULT 1")
    private BigDecimal distanceFactor = BigDecimal.ONE;
    
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalCost;
    
    @Column(length = 10)
    private String estimatedDays;
    
    @Column(name = "valid_until")
    private LocalDateTime validUntil;
    
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum QuoteServiceType {
        STANDARD, EXPRESS, OVERNIGHT
    }
}
