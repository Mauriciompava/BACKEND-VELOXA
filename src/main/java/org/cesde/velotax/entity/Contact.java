package org.cesde.velotax.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contacts", indexes = {
    @Index(name = "idx_ticket_number", columnList = "ticket_number"),
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_created_at", columnList = "created_at"),
    @Index(name = "idx_contact_status_created", columnList = "status,created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contact {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String fullName;
    
    @Column(nullable = false, length = 100)
    private String email;
    
    @Column(length = 20)
    private String phone;
    
    @Column(length = 100)
    private String company;
    
    @Column(nullable = false, length = 255)
    private String subject;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
    
    @Column(length = 50)
    private String category;
    
    @Column(unique = true, nullable = false, length = 20)
    private String ticketNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('Nuevo', 'En proceso', 'Resuelto', 'Cerrado') DEFAULT 'Nuevo'")
    private ContactStatus status = ContactStatus.NUEVO;
    
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('Baja', 'Media', 'Alta', 'Crítica') DEFAULT 'Media'")
    private ContactPriority priority = ContactPriority.MEDIA;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;
    
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
    
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
    
    @Column(name = "responded_at")
    private LocalDateTime respondedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum ContactStatus {
        NUEVO, EN_PROCESO, RESUELTO, CERRADO
    }
    
    public enum ContactPriority {
        BAJA, MEDIA, ALTA, CRITICA
    }
}
